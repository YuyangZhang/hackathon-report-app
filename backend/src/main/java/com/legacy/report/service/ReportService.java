package com.legacy.report.service;

import com.legacy.report.model.Report;
import com.legacy.report.repository.ReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class ReportService {

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private SqlQueryService sqlQueryService;

    /**
     * Get all active reports (not deleted).
     * Business logic: Filter deleted reports in Java, not SQL.
     */
    public List<Report> getAllReports() {
        return reportRepository.findByIsDeleted(0);
    }

    /**
     * Get report by ID.
     * Business logic: Check if report is active in Java layer.
     */
    public Report getReportById(Long id) {
        return reportRepository.findByIdAndIsDeleted(id, 0)
                .orElseThrow(() -> new RuntimeException("Report not found or has been deleted"));
    }

    /**
     * Create a new report with validation.
     * Business logic: Input validation in Java, not SQL constraints.
     */
    @Transactional
    public void createReport(Report report) {
        validateReport(report);
        report.setIsDeleted(0);
        reportRepository.save(report);
    }

    /**
     * Update an existing report.
     * Business logic: Check existence and validate in Java.
     */
    @Transactional
    public void updateReport(Long id, Report reportUpdate) {
        Report existingReport = getReportById(id);
        validateReport(reportUpdate);

        existingReport.setName(reportUpdate.getName());
        existingReport.setSql(reportUpdate.getSql());
        existingReport.setDescription(reportUpdate.getDescription());

        reportRepository.save(existingReport);
    }

    /**
     * Soft delete a report.
     * Business logic: Soft delete implemented in Java, not SQL.
     */
    @Transactional
    public void deleteReport(Long id) {
        Report report = getReportById(id);
        report.setIsDeleted(1);
        reportRepository.save(report);
    }

    /**
     * Generate report with safe SQL execution.
     * Business logic: SQL validation and safe parameter handling in Java.
     */
    public Map<String, Object> generateReport(Long reportId, String params) {
        Report report = getReportById(reportId);
        String sql = report.getSql();

        // Safe SQL execution with business logic validation
        List<Map<String, Object>> data = sqlQueryService.executeSafeQuery(sql, params);

        // Business logic: Calculate derived metrics in Java
        Map<String, Object> summary = calculateReportSummary(data);

        return Map.of(
                "reportName", report.getName(),
                "data", data,
                "count", data.size(),
                "summary", summary
        );
    }

    /**
     * Execute a report by ID and return raw data.
     * Business logic: Safe execution with validation.
     */
    public List<Map<String, Object>> executeReport(Long reportId) {
        Report report = getReportById(reportId);
        return sqlQueryService.executeSafeQuery(report.getSql(), null);
    }

    /**
     * Business logic: Validate report data in Java.
     */
    private void validateReport(Report report) {
        if (report.getName() == null || report.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Report name cannot be empty");
        }
        if (report.getSql() == null || report.getSql().trim().isEmpty()) {
            throw new IllegalArgumentException("Report SQL cannot be empty");
        }
        if (report.getName().length() > 200) {
            throw new IllegalArgumentException("Report name cannot exceed 200 characters");
        }
        if (report.getSql().length() > 4000) {
            throw new IllegalArgumentException("Report SQL cannot exceed 4000 characters");
        }
        // Security: Validate SQL doesn't contain dangerous operations
        sqlQueryService.validateSql(report.getSql());
    }

    /**
     * Business logic: Calculate summary statistics in Java instead of SQL.
     */
    private Map<String, Object> calculateReportSummary(List<Map<String, Object>> data) {
        if (data == null || data.isEmpty()) {
            return Map.of("totalRows", 0, "hasData", false);
        }

        int totalRows = data.size();
        boolean hasData = totalRows > 0;

        // Count numeric columns
        int numericColumns = 0;
        if (!data.isEmpty()) {
            Map<String, Object> firstRow = data.get(0);
            for (Object value : firstRow.values()) {
                if (value instanceof Number) {
                    numericColumns++;
                }
            }
        }

        return Map.of(
                "totalRows", totalRows,
                "hasData", hasData,
                "numericColumns", numericColumns
        );
    }
}