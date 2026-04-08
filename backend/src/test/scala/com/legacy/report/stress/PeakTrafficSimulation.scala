package com.legacy.report.stress

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

/**
 * Gatling simulation for peak traffic testing.
 * Simulates ramp-up from 10 to 500 concurrent users over 10 minutes.
 */
class PeakTrafficSimulation extends BaseSimulation {

  // Scenario 1: Light user - just login and list reports
  val lightUserScenario = scenario("Light User - Browse Only")
    .exec(http("Login")
      .post("/api/auth/login")
      .body(StringBody(s"""{"username": "maker1", "password": "${makerPassword}"}"""))
      .headers(contentTypeHeader)
      .check(jsonPath("$.token").saveAs("token"))
    )
    .pause(2, 5)
    .exec(http("Get Reports")
      .get("/api/reports")
      .headers(authorizationHeader("${token}"))
      .check(status.is(200))
    )
    .pause(5, 10)
    .exec(http("Get Profile")
      .get("/api/auth/profile")
      .headers(authorizationHeader("${token}"))
      .check(status.is(200))
    )

  // Scenario 2: Heavy user - login, execute report, generate, export
  val heavyUserScenario = scenario("Heavy User - Full Workflow")
    .exec(http("Login")
      .post("/api/auth/login")
      .body(StringBody(s"""{"username": "admin", "password": "password"}"""))
      .headers(contentTypeHeader)
      .check(jsonPath("$.token").saveAs("token"))
    )
    .pause(1)
    .exec(http("List Reports")
      .get("/api/reports")
      .headers(authorizationHeader("${token}"))
      .check(status.is(200))
    )
    .pause(2)
    .exec(http("Execute Report")
      .post("/api/reports/1/execute")
      .headers(authorizationHeader("${token}"))
      .check(status.is(200))
    )
    .pause(3)
    .exec(http("Generate Report")
      .post("/api/reports/generate")
      .headers(authorizationHeader("${token}"))
      .body(StringBody("""{"reportId": 1, "params": null}"""))
      .check(status.is(200))
    )

  // Load profile: Peak traffic from 10 to 500 users over 10 minutes
  setUp(
    lightUserScenario.inject(
      rampConcurrentUsers(10).to(300).during(10.minutes),
      constantConcurrentUsers(300).during(5.minutes),
      rampConcurrentUsers(300).to(0).during(2.minutes)
    ),
    heavyUserScenario.inject(
      rampConcurrentUsers(0).to(200).during(10.minutes),
      constantConcurrentUsers(200).during(5.minutes),
      rampConcurrentUsers(200).to(0).during(2.minutes)
    )
  ).protocols(httpProtocol)
    .assertions(
      // Availability > 99%
      global.successfulRequests.percent.gt(99),

      // Response times degrade gracefully
      global.responseTime.percentile(50).lt(1000),  // Median < 1s
      global.responseTime.percentile(95).lt(2000),  // 95th < 2s
      global.responseTime.percentile(99).lt(5000),  // 99th < 5s at peak

      // Error rate < 1%
      global.failedRequests.percent.lt(1),

      // No requests dropped during ramp-down
      forAll.failedRequests.count.lt(50)
    )
}
