package com.legacy.report.stress

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

/**
 * Gatling simulation for load testing authentication endpoints.
 * Tests POST /api/auth/login with 200 concurrent users.
 */
class AuthenticationSimulation extends BaseSimulation {

  // Feeder for user credentials
  val userFeeder = Seq(
    Map("username" -> "maker1", "password" -> "password"),
    Map("username" -> "checker1", "password" -> "password"),
    Map("username" -> "admin", "password" -> "password")
  ).circular

  // Scenario 1: Login only
  val loginScenario = scenario("Login Authentication")
    .feed(userFeeder)
    .exec(http("Login Request")
      .post("/api/auth/login")
      .body(StringBody(s"""{"username": "${username}", "password": "${password}"}"""))
      .headers(contentTypeHeader)
      .check(status.is(200))
      .check(jsonPath("$.token").exists)
      .check(responseTimeInMillis.lte(1000)) // 95th percentile < 1 second
    )

  // Scenario 2: Login and access protected resource
  val loginAndAccessProfile = scenario("Login and Access Profile")
    .feed(userFeeder)
    .exec(http("Login")
      .post("/api/auth/login")
      .body(StringBody(s"""{"username": "${username}", "password": "${password}"}"""))
      .headers(contentTypeHeader)
      .check(jsonPath("$.token").saveAs("token"))
    )
    .pause(1)
    .exec(http("Get Profile")
      .get("/api/auth/profile")
      .headers(authorizationHeader("${token}"))
      .check(status.is(200))
      .check(jsonPath("$.username").exists)
    )

  // Scenario 3: Failed login attempts
  val failedLoginScenario = scenario("Failed Login Attempts")
    .exec(http("Invalid Login")
      .post("/api/auth/login")
      .body(StringBody("""{"username": "invalid", "password": "wrong"}"""))
      .headers(contentTypeHeader)
      .check(status.not(200))
    )

  // Scenario 4: Mixed authentication workflow
  val mixedAuthWorkflow = scenario("Mixed Authentication Workflow")
    .randomSwitch(
      70.0 -> exec(http("Valid Login")
        .post("/api/auth/login")
        .body(StringBody(s"""{"username": "maker1", "password": "password"}"""))
        .headers(contentTypeHeader)
        .check(status.is(200))
      ),
      30.0 -> exec(http("Invalid Login")
        .post("/api/auth/login")
        .body(StringBody("""{"username": "unknown", "password": "bad"}"""))
        .headers(contentTypeHeader)
        .check(status.not(200))
      )
    )

  // Load profile: 200 concurrent users for authentication endpoint
  setUp(
    loginScenario.inject(
      rampUsers(200).during(10.minutes),
      constantUsersPerSec(200).during(5.minutes)
    ),
    loginAndAccessProfile.inject(
      rampUsers(100).during(5.minutes),
      constantUsersPerSec(100).during(5.minutes)
    ),
    failedLoginScenario.inject(
      rampUsers(50).during(2.minutes)
    )
  ).protocols(httpProtocol)
    .assertions(
      global.responseTime.percentile(95).lt(1000), // 95th percentile < 1 second
      global.responseTime.max.lt(5000), // Max response time < 5 seconds
      global.successfulRequests.percent.gt(95),
      forAll.failedRequests.percent.lt(5)
    )
}
