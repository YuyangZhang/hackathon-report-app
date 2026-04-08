package com.legacy.report.service;

import com.legacy.report.dao.ReportDao;
import com.legacy.report.model.Report;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @Mock
    private ReportDao reportDao;

    @InjectMocks
    private ReportService reportService;

    private Report testReport;

    @BeforeEach
    void setUp() {
        testReport = new Report();
        testReport.setId(1L);
        testReport.setName("Test Report");
        testReport.setSql("SELECT * FROM test_table");
    }

    @Test
    void testGetAllReports() {
        // Given
        List<Report> expectedReports = List.of(testReport);
        when(reportDao.findAll()).thenReturn(expectedReports);

        // When
        List<Report> result = reportService.getAllReports();

        // Then
        assertEquals(expectedReports, result);
        verify(reportDao).findAll();
    }

    @Test
    void testGetReportById() {
        // Given
        when(reportDao.findById(1L)).thenReturn(testReport);

        // When
        Report result = reportService.getReportById(1L);

        // Then
        assertEquals(testReport, result);
        verify(reportDao).findById(1L);
    }

    @Test
    void testRunReport() {
        // Given
        String sql = "SELECT * FROM test_table";
        List<Map<String, Object>> expectedData = List.of(
            Map.of("id", 1, "name", "test"),
            Map.of("id", 2, "name", "test2")
        );
        when(reportDao.executeSql(sql)).thenReturn(expectedData);

        // When
        List<Map<String, Object>> result = reportService.runReport(sql);

        // Then
        assertEquals(expectedData, result);
        verify(reportDao).executeSql(sql);
    }

    /*@Test
    void testCreateReportSuccess() {
        // Given
        when(reportDao.save(any())).thenReturn(testReport);

        // When
        assertDoesNotThrow(() -> reportService.createReport(testReport));

        // Then
        verify(reportDao).save(testReport);
    }*/

    @Test
    void testCreateReportWithEmptyName() {
        // Given
        testReport.setName("");

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            reportService.createReport(testReport);
        });
        assertEquals("名称不能为空", exception.getMessage());
        verify(reportDao, never()).save(any());
    }

    @Test
    void testCreateReportWithNullName() {
        // Given
        testReport.setName(null);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            reportService.createReport(testReport);
        });
        assertEquals("名称不能为空", exception.getMessage());
        verify(reportDao, never()).save(any());
    }

    @Test
    void testCreateReportWithEmptySql() {
        // Given
        testReport.setSql("");

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            reportService.createReport(testReport);
        });
        assertEquals("SQL不能为空", exception.getMessage());
        verify(reportDao, never()).save(any());
    }

    @Test
    void testCreateReportWithNullSql() {
        // Given
        testReport.setSql(null);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            reportService.createReport(testReport);
        });
        assertEquals("SQL不能为空", exception.getMessage());
        verify(reportDao, never()).save(any());
    }

    @Test
    void testGenerateReportSuccess() {
        // Given
        Long reportId = 1L;
        String params = "id = 1";
        List<Map<String, Object>> mockData = List.of(
            Map.of("id", 1, "name", "test")
        );
        
        when(reportDao.findById(reportId)).thenReturn(testReport);
        when(reportDao.executeSql(anyString())).thenReturn(mockData);

        // When
        Map<String, Object> result = reportService.generateReport(reportId, params);

        // Then
        assertNotNull(result);
        assertEquals(testReport.getName(), result.get("reportName"));
        assertEquals(mockData, result.get("data"));
        assertEquals(1, result.get("count"));
        verify(reportDao).findById(reportId);
        verify(reportDao).executeSql(anyString());
    }

    @Test
    void testGenerateReportWithEmptyParams() {
        // Given
        Long reportId = 1L;
        String params = "";
        List<Map<String, Object>> mockData = List.of(
            Map.of("id", 1, "name", "test")
        );
        
        when(reportDao.findById(reportId)).thenReturn(testReport);
        when(reportDao.executeSql(anyString())).thenReturn(mockData);

        // When
        Map<String, Object> result = reportService.generateReport(reportId, params);

        // Then
        assertNotNull(result);
        assertEquals(testReport.getName(), result.get("reportName"));
        assertEquals(mockData, result.get("data"));
        assertEquals(1, result.get("count"));
        verify(reportDao).findById(reportId);
        verify(reportDao).executeSql(anyString());
    }

    @Test
    void testGenerateReportWithNullParams() {
        // Given
        Long reportId = 1L;
        String params = null;
        List<Map<String, Object>> mockData = List.of(
            Map.of("id", 1, "name", "test")
        );
        
        when(reportDao.findById(reportId)).thenReturn(testReport);
        when(reportDao.executeSql(anyString())).thenReturn(mockData);

        // When
        Map<String, Object> result = reportService.generateReport(reportId, params);

        // Then
        assertNotNull(result);
        assertEquals(testReport.getName(), result.get("reportName"));
        assertEquals(mockData, result.get("data"));
        assertEquals(1, result.get("count"));
        verify(reportDao).findById(reportId);
        verify(reportDao).executeSql(testReport.getSql());
    }

    @Test
    void testGenerateReportNotFound() {
        // Given
        Long reportId = 999L;
        when(reportDao.findById(reportId)).thenReturn(null);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            reportService.generateReport(reportId, "");
        });
        assertEquals("报表不存在", exception.getMessage());
        verify(reportDao).findById(reportId);
        verify(reportDao, never()).executeSql(anyString());
    }
}
