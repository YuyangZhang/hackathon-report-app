package com.legacy.report.stress

import io.gatling.core.Predef._
import io.gatling.http.Predef._

/**
 * Base configuration for Gatling simulations.
 * Contains common settings and HTTP protocol configuration.
 */
abstract class BaseSimulation extends Simulation {

  // Base URL for the application
  val baseUrl: String = System.getProperty("gatling.baseUrl", "http://localhost:8080")

  // Common HTTP headers
  val contentTypeHeader = Map("Content-Type" -> "application/json")

  // HTTP protocol configuration
  val httpProtocol = http
    .baseUrl(baseUrl)
    .acceptHeader("application/json")
    .contentTypeHeader("application/json")
    .doNotTrackHeader("1")
    .acceptLanguageHeader("en-US,en;q=0.5")
    .acceptEncodingHeader("gzip, deflate")
    .userAgentHeader("Gatling/StressTest")

  /**
   * Helper method to create Authorization header with Bearer token
   */
  def authorizationHeader(token: String): Map[String, String] = {
    Map("Authorization" -> s"Bearer $token")
  }

  // Test user credentials
  val makerUsername = "maker1"
  val makerPassword = "password"
  val checkerUsername = "checker1"
  val checkerPassword = "password"

  /**
   * Extract token from login response
   */
  val extractToken: String = {
    """
    |var token = session("token").as[String];
    |token
    """.stripMargin
  }
}
