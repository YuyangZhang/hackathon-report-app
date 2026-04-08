package com.legacy.report;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ReportApprovalFlowIntegrationTest {

    @LocalServerPort
    private int port;

    @Test
    void makerSubmissionCanBeApprovedByCheckerAndViewedInAuditTrail() throws Exception {
        RestAssured.baseURI = "http://127.0.0.1";
        RestAssured.port = port;

        String makerToken = login("maker1", "123456");
        String checkerToken = login("checker1", "123456");

        RestAssured.given()
                .header("Authorization", bearer(makerToken))
                .when()
                .get("/api/reports")
                .then()
                .statusCode(200)
                .body("size()", greaterThan(0));

        RestAssured.given()
                .header("Authorization", bearer(makerToken))
                .contentType("application/json")
                .body("{}")
                .when()
                .post("/api/reports/1/execute")
                .then()
                .statusCode(200)
                .body("size()", greaterThan(0));

        Response latestRun = RestAssured.given()
                .header("Authorization", bearer(makerToken))
                .queryParam("reportId", 1)
                .when()
                .get("/api/report-runs/my-latest");
        latestRun.then().statusCode(200);

        long runId = latestRun.jsonPath().getLong("id");
        assertTrue("Generated".equals(latestRun.jsonPath().getString("status")));

        RestAssured.given()
                .header("Authorization", bearer(makerToken))
                .contentType("application/json")
                .body("{}")
                .when()
                .post("/api/report-runs/{id}/submit", runId)
                .then()
                .statusCode(200);

        String submittedRuns = RestAssured.given()
                .header("Authorization", bearer(checkerToken))
                .when()
                .get("/api/report-runs/submitted")
                .then()
                .statusCode(200)
                .extract()
                .asString();
        assertTrue(submittedRuns.contains("maker1"));

        RestAssured.given()
                .header("Authorization", bearer(checkerToken))
                .contentType("application/json")
                .body("{\"decision\":\"APPROVED\",\"comment\":\"looks good\"}")
                .when()
                .post("/api/report-runs/{id}/decision", runId)
                .then()
                .statusCode(200);

        String audit = RestAssured.given()
                .header("Authorization", bearer(checkerToken))
                .when()
                .get("/api/report-runs/{id}/audit", runId)
                .then()
                .statusCode(200)
                .extract()
                .asString();

        assertTrue(audit.contains("Generated"));
        assertTrue(audit.contains("Submitted"));
        assertTrue(audit.contains("Approved"));
    }

    private String login(String username, String password) {
        Response response = RestAssured.given()
                .contentType("application/json")
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
