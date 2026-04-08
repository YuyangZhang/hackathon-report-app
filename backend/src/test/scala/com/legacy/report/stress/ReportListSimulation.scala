package com.legacy.report.stress

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

/**
 * Gatling simulation for load testing the report list API.
 * Tests GET /api/reports endpoint with 100 concurrent users.
 */
class ReportListSimulation extends BaseSimulation {

  // Scenario 1: Login and then fetch reports
  val loginAndFetchReports = scenario("Login and Fetch Reports")
    .exec(http("Login")
      .post("/api/auth/login")
      .body(StringBody(s"""{"username": "${makerUsername}", "password": "${makerPassword}"}"""))
      .headers(contentTypeHeader)
      .check(jsonPath("$.token").saveAs("token"))
    )
    .pause(1)
    .exec(http("Get Reports List")
      .get("/api/reports")
      .headers(authorizationHeader("${token}"))
      .check(status.is(200))
      .check(jsonPath("$").exists)
    )

  // Scenario 2: Direct reports fetch (assuming token already available)
  val fetchReportsOnly = scenario("Fetch Reports Only")
    .exec(http("Get Reports List - Direct")
      .get("/api/reports")
      .headers(Map("Authorization" -> "Bearer ${token}"))
      .check(status.is(200))
      .check(responseTimeInMillis.lte(2000))
    )

  // Load profile: Ramp up to 100 users over 10 minutes, hold for 5 minutes
  setUp(
    loginAndFetchReports.inject(
      rampUsers(100).during(10.minutes),
      constantUsersPerSec(100).during(5.minutes)
    )
  ).protocols(httpProtocol)
    .assertions(
      global.responseTime.percentile(95).lt(2000),
      global.successfulRequests.percent.gt(99),
      global.failedRequests.percent.lt(1)
    )
}
