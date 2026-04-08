package com.legacy.report.stress

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

/**
 * Gatling simulation for load testing report execution API.
 * Tests POST /api/reports/{id}/execute with concurrent users.
 */
class ReportExecutionSimulation extends BaseSimulation {

  // Feeder for report IDs (simulating different reports)
  val reportIdFeeder = Seq(
    Map("reportId" -> "1"),
    Map("reportId" -> "2"),
    Map("reportId" -> "3")
  ).circular

  // Scenario: Login, fetch reports, then execute a report
  val executeReportScenario = scenario("Execute Report")
    .feed(reportIdFeeder)
    .exec(http("Login")
      .post("/api/auth/login")
      .body(StringBody(s"""{"username": "${makerUsername}", "password": "${makerPassword}"}"""))
      .headers(contentTypeHeader)
      .check(jsonPath("$.token").saveAs("token"))
    )
    .pause(1)
    .exec(http("Execute Report")
      .post("/api/reports/${reportId}/execute")
      .headers(authorizationHeader("${token}"))
      .check(status.is(200))
      .check(jsonPath("$").exists)
      .check(responseTimeInMillis.lte(2000))
    )

  // Scenario: Report execution with parameters
  val executeReportWithGeneration = scenario("Execute Report and Generate")
    .feed(reportIdFeeder)
    .exec(http("Login")
      .post("/api/auth/login")
      .body(StringBody(s"""{"username": "${makerUsername}", "password": "${makerPassword}"}"""))
      .headers(contentTypeHeader)
      .check(jsonPath("$.token").saveAs("token"))
    )
    .pause(1)
    .exec(http("Execute Report")
      .post("/api/reports/${reportId}/execute")
      .headers(authorizationHeader("${token}"))
      .check(status.is(200))
    )
    .pause(2)
    .exec(http("Generate Report")
      .post("/api/reports/generate")
      .headers(authorizationHeader("${token}"))
      .body(StringBody(s"""{"reportId": ${reportId}, "params": null}"""))
      .check(status.is(200))
      .check(responseTimeInMillis.lte(2000))
    )

  // Load profile: Peak load simulation
  setUp(
    executeReportScenario.inject(
      rampUsers(100).during(10.minutes),
      constantUsersPerSec(100).during(5.minutes),
      rampUsers(0).during(2.minutes) // Ramp down
    ),
    executeReportWithGeneration.inject(
      rampUsers(50).during(5.minutes),
      constantUsersPerSec(50).during(5.minutes)
    )
  ).protocols(httpProtocol)
    .assertions(
      global.responseTime.percentile(95).lt(2000),
      global.responseTime.percentile(99).lt(5000),
      global.successfulRequests.percent.gt(99),
      global.failedRequests.count.lt(50)
    )
}
