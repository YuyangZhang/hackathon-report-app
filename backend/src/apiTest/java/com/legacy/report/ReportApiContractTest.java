package com.legacy.report;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.notNullValue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ReportApiContractTest {

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://127.0.0.1";
        RestAssured.port = port;
    }

    @Test
    void loginReturnsTokenAndUserProfile() {
        RestAssured.given()
                .contentType(ContentType.JSON)
                .body("{\"username\":\"maker1\",\"password\":\"123456\"}")
                .when()
                .post("/api/auth/login")
                .then()
                .statusCode(200)
                .body("token", notNullValue())
                .body("user.username", equalTo("maker1"))
                .body("user.role", equalTo("MAKER"));
    }

    @Test
    void reportsEndpointRequiresAuthentication() {
        RestAssured.given()
                .when()
                .get("/api/reports")
                .then()
                .statusCode(403);
    }

    @Test
    void authenticatedMakerCanReadReportsAndExecuteOne() {
        String token = loginAndExtractToken("maker1", "123456");

        RestAssured.given()
                .header("Authorization", bearer(token))
                .when()
                .get("/api/reports")
                .then()
                .statusCode(200)
                .body("size()", greaterThan(0));

        RestAssured.given()
                .header("Authorization", bearer(token))
                .contentType(ContentType.JSON)
                .body("{}")
                .when()
                .post("/api/reports/1/execute")
                .then()
                .statusCode(200)
                .body("size()", greaterThan(0));
    }

    private String loginAndExtractToken(String username, String password) {
        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                .body("{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}")
                .when()
                .post("/api/auth/login");

        response.then().statusCode(200);
        return response.jsonPath().getString("token");
    }

    private static String bearer(String token) {
        return "Bearer " + token;
    }
}
