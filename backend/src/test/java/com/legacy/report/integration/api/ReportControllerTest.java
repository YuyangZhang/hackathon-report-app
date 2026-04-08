package com.legacy.report.integration.api;

import com.legacy.report.model.Report;
import com.legacy.report.repository.ReportRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for {@link com.legacy.report.controller.ReportController}.
 * Tests all REST endpoints for the report API.
 */
@DisplayName("ReportController API Tests")
class ReportControllerTest extends BaseApiTest {

    @Autowired
    private ReportRepository reportRepository;

    private Report testReport;

    @BeforeEach
    void setUp() {
        // Clean up and create test report
        reportRepository.deleteAll();

        testReport = new Report();
        testReport.setName("Test Report");
        testReport.setSql("SELECT 1 as id, 'test' as name");
        testReport.setDescription("Test Description");
        testReport.setIsDeleted(0);
        testReport = reportRepository.save(testReport);
    }

    @Test
    @DisplayName("GET /api/test should return status ok")
    void testEndpoint_ShouldReturnStatusOk() throws Exception {
        mockMvc.perform(get("/api/test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ok"))
                .andExpect(jsonPath("$.message").value("Backend is working"));
    }

    @Test
    @DisplayName("GET /api/reports should return list of reports")
    void getAllReports_ShouldReturnReportsList() throws Exception {
        mockMvc.perform(get("/api/reports")
                        .header("Authorization", authorizationHeader(makerToken)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").value("Test Report"));
    }

    @Test
    @DisplayName("GET /api/reports/{id} should return report when found")
    void getReportById_ShouldReturnReport_WhenFound() throws Exception {
        mockMvc.perform(get("/api/reports/{id}", testReport.getId())
                        .header("Authorization", authorizationHeader(makerToken)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(testReport.getId()))
                .andExpect(jsonPath("$.name").value("Test Report"))
                .andExpect(jsonPath("$.sql").value("SELECT 1 as id, 'test' as name"));
    }

    @Test
    @DisplayName("GET /api/reports/{id} should return 500 when report not found")
    void getReportById_ShouldReturnError_WhenNotFound() throws Exception {
        mockMvc.perform(get("/api/reports/{id}", 99999)
                        .header("Authorization", authorizationHeader(makerToken)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("POST /api/reports should create new report")
    void createReport_ShouldCreateNewReport() throws Exception {
        String newReportJson = """
                {
                    "name": "New Report",
                    "sql": "SELECT * FROM users",
                    "description": "New description"
                }
                """;

        mockMvc.perform(post("/api/reports")
                        .header("Authorization", authorizationHeader(makerToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newReportJson))
                .andExpect(status().isOk());

        // Verify report was created
        List<Report> reports = reportRepository.findByIsDeleted(0);
        assert reports.stream().anyMatch(r -> r.getName().equals("New Report"));
    }

    @Test
    @DisplayName("POST /api/reports should fail with 400 for invalid report")
    void createReport_ShouldFail_WhenInvalid() throws Exception {
        String invalidReportJson = """
                {
                    "name": "",
                    "sql": "SELECT * FROM users"
                }
                """;

        mockMvc.perform(post("/api/reports")
                        .header("Authorization", authorizationHeader(makerToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidReportJson))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("PUT /api/reports/{id} should update existing report")
    void updateReport_ShouldUpdateExistingReport() throws Exception {
        String updateJson = """
                {
                    "name": "Updated Report Name",
                    "sql": "SELECT * FROM updated_table",
                    "description": "Updated description"
                }
                """;

        mockMvc.perform(put("/api/reports/{id}", testReport.getId())
                        .header("Authorization", authorizationHeader(makerToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk());

        // Verify update
        Report updated = reportRepository.findById(testReport.getId()).orElseThrow();
        assert updated.getName().equals("Updated Report Name");
    }

    @Test
    @DisplayName("DELETE /api/reports/{id} should soft delete report")
    void deleteReport_ShouldSoftDeleteReport() throws Exception {
        mockMvc.perform(delete("/api/reports/{id}", testReport.getId())
                        .header("Authorization", authorizationHeader(makerToken)))
                .andExpect(status().isOk());

        // Verify soft delete (report still exists but isDeleted=1)
        Report deleted = reportRepository.findById(testReport.getId()).orElseThrow();
        assert deleted.getIsDeleted() == 1;
    }

    @Test
    @DisplayName("POST /api/reports/run should execute SQL query")
    void runReport_ShouldExecuteSqlQuery() throws Exception {
        String requestJson = """
                {
                    "sql": "SELECT 1 as id, 'test' as name"
                }
                """;

        mockMvc.perform(post("/api/reports/run")
                        .header("Authorization", authorizationHeader(makerToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].ID").value(1))
                .andExpect(jsonPath("$[0].NAME").value("test"));
    }

    @Test
    @DisplayName("POST /api/reports/run should reject dangerous SQL")
    void runReport_ShouldRejectDangerousSql() throws Exception {
        String requestJson = """
                {
                    "sql": "DROP TABLE users"
                }
                """;

        mockMvc.perform(post("/api/reports/run")
                        .header("Authorization", authorizationHeader(makerToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("POST /api/reports/run should reject non-SELECT SQL")
    void runReport_ShouldRejectNonSelectSql() throws Exception {
        String requestJson = """
                {
                    "sql": "DELETE FROM users WHERE id = 1"
                }
                """;

        mockMvc.perform(post("/api/reports/run")
                        .header("Authorization", authorizationHeader(makerToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("POST /api/reports/generate should generate report")
    void generateReport_ShouldGenerateReport() throws Exception {
        String requestJson = String.format("""
                {
                    "reportId": %d,
                    "params": null
                }
                """, testReport.getId());

        mockMvc.perform(post("/api/reports/generate")
                        .header("Authorization", authorizationHeader(makerToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reportName").value("Test Report"))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.count").exists())
                .andExpect(jsonPath("$.summary").exists());
    }

    @Test
    @DisplayName("POST /api/reports/{id}/execute should execute report and create run")
    void executeReport_ShouldExecuteAndCreateRun() throws Exception {
        mockMvc.perform(post("/api/reports/{id}/execute", testReport.getId())
                        .header("Authorization", authorizationHeader(makerToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("GET /api/reports/{id}/export should return Excel file")
    void exportReport_ShouldReturnExcelFile() throws Exception {
        // First execute the report to have data to export
        mockMvc.perform(post("/api/reports/{id}/execute", testReport.getId())
                        .header("Authorization", authorizationHeader(makerToken)))
                .andExpect(status().isOk());

        // Then export
        mockMvc.perform(get("/api/reports/{id}/export", testReport.getId())
                        .header("Authorization", authorizationHeader(makerToken)))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .andExpect(header().string("Content-Disposition", containsString("attachment")));
    }

    @Test
    @DisplayName("GET /api/reports without token should return 401")
    void getAllReports_WithoutToken_ShouldReturn401() throws Exception {
        mockMvc.perform(get("/api/reports"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /api/reports with invalid token should return 401")
    void getAllReports_WithInvalidToken_ShouldReturn401() throws Exception {
        mockMvc.perform(get("/api/reports")
                        .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /api/test-db should test database connectivity")
    void testDatabase_ShouldReturnStatus() throws Exception {
        mockMvc.perform(get("/api/test-db")
                        .header("Authorization", authorizationHeader(makerToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ok"))
                .andExpect(jsonPath("$.count").exists());
    }
}
