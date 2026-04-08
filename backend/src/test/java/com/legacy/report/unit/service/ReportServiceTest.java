package com.legacy.report.unit.service;

import com.legacy.report.model.Report;
import com.legacy.report.repository.ReportRepository;
import com.legacy.report.service.ReportService;
import com.legacy.report.service.SqlQueryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link ReportService}.
 * Tests all business logic with mocked dependencies.
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("ReportService Unit Tests")
class ReportServiceTest {

    @Mock
    private ReportRepository reportRepository;

    @Mock
    private SqlQueryService sqlQueryService;

    @InjectMocks
    private ReportService reportService;

    private Report testReport;

    @BeforeEach
    void setUp() {
        testReport = new Report();
        testReport.setId(1L);
        testReport.setName("Test Report");
        testReport.setSql("SELECT * FROM test_table");
        testReport.setDescription("Test Description");
        testReport.setIsDeleted(0);
        testReport.setCreatedAt(LocalDateTime.now());
        testReport.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("getAllReports should return only non-deleted reports")
    void getAllReports_ShouldReturnNonDeletedReports() {
        // Given
        Report report2 = new Report();
        report2.setId(2L);
        report2.setName("Report 2");
        report2.setIsDeleted(0);

        List<Report> expectedReports = Arrays.asList(testReport, report2);
        when(reportRepository.findByIsDeleted(0)).thenReturn(expectedReports);

        // When
        List<Report> result = reportService.getAllReports();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).containsExactlyInAnyOrder(testReport, report2);
        verify(reportRepository).findByIsDeleted(0);
    }

    @Test
    @DisplayName("getAllReports should return empty list when no reports exist")
    void getAllReports_ShouldReturnEmptyListWhenNoReports() {
        // Given
        when(reportRepository.findByIsDeleted(0)).thenReturn(List.of());

        // When
        List<Report> result = reportService.getAllReports();

        // Then
        assertThat(result).isEmpty();
        verify(reportRepository).findByIsDeleted(0);
    }

    @Test
    @DisplayName("getReportById should return report when found and not deleted")
    void getReportById_ShouldReturnReportWhenFound() {
        // Given
        when(reportRepository.findByIdAndIsDeleted(1L, 0)).thenReturn(Optional.of(testReport));

        // When
        Report result = reportService.getReportById(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Test Report");
        verify(reportRepository).findByIdAndIsDeleted(1L, 0);
    }

    @Test
    @DisplayName("getReportById should throw exception when report not found")
    void getReportById_ShouldThrowExceptionWhenNotFound() {
        // Given
        when(reportRepository.findByIdAndIsDeleted(999L, 0)).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> reportService.getReportById(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Report not found or has been deleted");

        verify(reportRepository).findByIdAndIsDeleted(999L, 0);
    }

    @Test
    @DisplayName("createReport should save report with isDeleted set to 0")
    void createReport_ShouldSaveReportWithDefaults() {
        // Given
        Report newReport = new Report();
        newReport.setName("New Report");
        newReport.setSql("SELECT 1");
        newReport.setDescription("Description");

        when(reportRepository.save(any(Report.class))).thenAnswer(invocation -> {
            Report saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        // When
        reportService.createReport(newReport);

        // Then
        verify(reportRepository).save(argThat(report ->
                report.getName().equals("New Report") &&
                report.getIsDeleted() == 0
        ));
    }

    @Test
    @DisplayName("createReport should throw exception when report name is empty")
    void createReport_ShouldThrowExceptionWhenNameEmpty() {
        // Given
        Report invalidReport = new Report();
        invalidReport.setName("");
        invalidReport.setSql("SELECT 1");

        // When/Then
        assertThatThrownBy(() -> reportService.createReport(invalidReport))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Report name cannot be empty");

        verify(reportRepository, never()).save(any());
    }

    @Test
    @DisplayName("createReport should throw exception when SQL is empty")
    void createReport_ShouldThrowExceptionWhenSqlEmpty() {
        // Given
        Report invalidReport = new Report();
        invalidReport.setName("Test Report");
        invalidReport.setSql(null);

        // When/Then
        assertThatThrownBy(() -> reportService.createReport(invalidReport))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Report SQL cannot be empty");

        verify(reportRepository, never()).save(any());
    }

    @Test
    @DisplayName("createReport should throw exception when name exceeds 200 characters")
    void createReport_ShouldThrowExceptionWhenNameTooLong() {
        // Given
        Report invalidReport = new Report();
        invalidReport.setName("a".repeat(201));
        invalidReport.setSql("SELECT 1");

        // When/Then
        assertThatThrownBy(() -> reportService.createReport(invalidReport))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Report name cannot exceed 200 characters");
    }

    @Test
    @DisplayName("updateReport should update existing report")
    void updateReport_ShouldUpdateExistingReport() {
        // Given
        Report update = new Report();
        update.setName("Updated Name");
        update.setSql("SELECT * FROM updated");
        update.setDescription("Updated Description");

        when(reportRepository.findByIdAndIsDeleted(1L, 0)).thenReturn(Optional.of(testReport));
        when(reportRepository.save(any(Report.class))).thenReturn(testReport);

        // When
        reportService.updateReport(1L, update);

        // Then
        verify(reportRepository).save(argThat(report ->
                report.getName().equals("Updated Name") &&
                report.getSql().equals("SELECT * FROM updated")
        ));
    }

    @Test
    @DisplayName("deleteReport should perform soft delete")
    void deleteReport_ShouldPerformSoftDelete() {
        // Given
        when(reportRepository.findByIdAndIsDeleted(1L, 0)).thenReturn(Optional.of(testReport));
        when(reportRepository.save(any(Report.class))).thenReturn(testReport);

        // When
        reportService.deleteReport(1L);

        // Then
        verify(reportRepository).save(argThat(report ->
                report.getIsDeleted() == 1
        ));
    }

    @Test
    @DisplayName("generateReport should return report data with summary")
    void generateReport_ShouldReturnReportDataWithSummary() {
        // Given
        String params = "status='ACTIVE'";
        List<Map<String, Object>> mockData = Arrays.asList(
                Map.of("id", 1, "name", "Item 1", "amount", 100),
                Map.of("id", 2, "name", "Item 2", "amount", 200)
        );

        when(reportRepository.findByIdAndIsDeleted(1L, 0)).thenReturn(Optional.of(testReport));
        when(sqlQueryService.executeSafeQuery(any(), any())).thenReturn(mockData);

        // When
        Map<String, Object> result = reportService.generateReport(1L, params);

        // Then
        assertThat(result).containsKeys("reportName", "data", "count", "summary");
        assertThat(result.get("reportName")).isEqualTo("Test Report");
        assertThat(result.get("count")).isEqualTo(2);

        @SuppressWarnings("unchecked")
        Map<String, Object> summary = (Map<String, Object>) result.get("summary");
        assertThat(summary).containsEntry("totalRows", 2);
        assertThat(summary).containsEntry("hasData", true);
        assertThat(summary).containsEntry("numericColumns", 2);
    }

    @Test
    @DisplayName("generateReport should handle empty data")
    void generateReport_ShouldHandleEmptyData() {
        // Given
        when(reportRepository.findByIdAndIsDeleted(1L, 0)).thenReturn(Optional.of(testReport));
        when(sqlQueryService.executeSafeQuery(any(), any())).thenReturn(List.of());

        // When
        Map<String, Object> result = reportService.generateReport(1L, null);

        // Then
        @SuppressWarnings("unchecked")
        Map<String, Object> summary = (Map<String, Object>) result.get("summary");
        assertThat(summary).containsEntry("totalRows", 0);
        assertThat(summary).containsEntry("hasData", false);
    }

    @Test
    @DisplayName("executeReport should return raw data")
    void executeReport_ShouldReturnRawData() {
        // Given
        List<Map<String, Object>> mockData = Arrays.asList(
                Map.of("id", 1, "name", "Item 1"),
                Map.of("id", 2, "name", "Item 2")
        );

        when(reportRepository.findByIdAndIsDeleted(1L, 0)).thenReturn(Optional.of(testReport));
        when(sqlQueryService.executeSafeQuery(any(), isNull())).thenReturn(mockData);

        // When
        List<Map<String, Object>> result = reportService.executeReport(1L);

        // Then
        assertThat(result).hasSize(2);
        verify(sqlQueryService).executeSafeQuery(testReport.getSql(), null);
    }
}
