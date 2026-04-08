package com.legacy.report.controller;

import com.legacy.report.model.Report;
import com.legacy.report.service.ReportExcelExportService;
import com.legacy.report.service.ReportRunService;
import com.legacy.report.service.ReportService;
import com.legacy.report.service.SqlQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @Autowired
    private ReportRunService reportRunService;

    @Autowired
    private ReportExcelExportService reportExcelExportService;

    @Autowired
    private SqlQueryService sqlQueryService;

    // Test endpoint to verify basic functionality
    @GetMapping("/test")
    public Map<String, String> test() {
        return Map.of("status", "ok", "message", "Backend is working");
    }

    // Test database connectivity
    @GetMapping("/test-db")
    public Map<String, Object> testDatabase() {
        try {
            System.out.println("Testing database connection...");
            List<Report> reports = reportService.getAllReports();
            System.out.println("Database test successful, found " + reports.size() + " reports");
            return Map.of("status", "ok", "message", "Database connection working", "count", reports.size());
        } catch (Exception e) {
            System.out.println("Database test failed: " + e.getMessage());
            e.printStackTrace();
            return Map.of("status", "error", "message", "Database connection failed: " + e.getMessage());
        }
    }

    // Unified response format - list all reports
    @GetMapping(value = "/reports", produces = "application/json;charset=UTF-8")
    public List<Report> getAllReports() {
        return reportService.getAllReports();
    }

    @GetMapping("/reports/{id}")
    public Report getReport(@PathVariable Long id) {
        return reportService.getReportById(id);
    }

    // Safe SQL execution with validation (removes SQL injection vulnerability)
    @PostMapping("/reports/run")
    public List<Map<String, Object>> runReport(@RequestBody Map<String, String> request) {
        String sql = request.get("sql");
        // Business logic: SQL validation moved to SqlQueryService
        return sqlQueryService.runReport(sql);
    }

    @PostMapping("/reports/generate")
    public Map<String, Object> generateReport(@RequestBody Map<String, Object> request) {
        Long reportId = Long.valueOf(request.get("reportId").toString());
        String params = (String) request.get("params");
        return reportService.generateReport(reportId, params);
    }

    @PostMapping("/reports")
    public void createReport(@RequestBody Report report) {
        reportService.createReport(report);
    }

    @PutMapping("/reports/{id}")
    public void updateReport(@PathVariable Long id, @RequestBody Report report) {
        reportService.updateReport(id, report);
    }

    @DeleteMapping("/reports/{id}")
    public void deleteReport(@PathVariable Long id) {
        reportService.deleteReport(id);
    }

    @PostMapping("/reports/{id}/execute")
    public List<Map<String, Object>> executeReport(@PathVariable Long id) {
        // Execute report and create ReportRun + audit event
        return reportRunService.executeReportWithRun(id);
    }

    @GetMapping("/reports/{id}/export")
    public ResponseEntity<byte[]> exportReport(@PathVariable Long id) {
        byte[] body = reportExcelExportService.exportLatestByReportId(id);

        String filename = URLEncoder.encode("report-" + id + ".xlsx", StandardCharsets.UTF_8);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.set(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + filename + "\"; filename*=UTF-8''" + filename);

        return new ResponseEntity<>(body, headers, HttpStatus.OK);
    }
}