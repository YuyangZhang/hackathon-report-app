package com.legacy.report.service;

import com.legacy.report.dao.ReportDao;
import com.legacy.report.model.Report;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ReportService 单元测试")
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

    @AfterEach
    void tearDown() {
        // 测试后清理
    }

    @Test
    @DisplayName("应该返回所有报表")
    void should_return_all_reports() {
        // Arrange
        List<Report> expectedReports = List.of(testReport);
        when(reportDao.findAll()).thenReturn(expectedReports);

        // Act
        List<Report> result = reportService.getAllReports();

        // Assert
        assertThat(result).isEqualTo(expectedReports);
        verify(reportDao, times(1)).findAll();
    }

    @Test
    @DisplayName("应该返回指定ID的报表")
    void should_return_report_by_id() {
        // Arrange
        Long reportId = 1L;
        when(reportDao.findById(reportId)).thenReturn(testReport);

        // Act
        Report result = reportService.getReportById(reportId);

        // Assert
        assertThat(result).isEqualTo(testReport);
        verify(reportDao, times(1)).findById(reportId);
    }

    @Test
    @DisplayName("应该返回null当报表ID不存在时")
    void should_return_null_when_report_id_not_exists() {
        // Arrange
        Long reportId = 999L;
        when(reportDao.findById(reportId)).thenReturn(null);

        // Act
        Report result = reportService.getReportById(reportId);

        // Assert
        assertThat(result).isNull();
        verify(reportDao, times(1)).findById(reportId);
    }

    @Test
    @DisplayName("应该执行SQL并返回结果")
    void should_execute_sql_and_return_result() {
        // Arrange
        String sql = "SELECT * FROM test_table";
        List<Map<String, Object>> expectedData = List.of(Map.of("id", 1, "name", "test"));
        when(reportDao.executeSql(sql)).thenReturn(expectedData);

        // Act
        List<Map<String, Object>> result = reportService.runReport(sql);

        // Assert
        assertThat(result).isEqualTo(expectedData);
        verify(reportDao, times(1)).executeSql(sql);
    }

    @Test
    @DisplayName("应该执行复杂SQL并返回结果")
    void should_execute_complex_sql_and_return_result() {
        // Arrange
        String sql = "SELECT t1.id, t2.name FROM table1 t1 JOIN table2 t2 ON t1.id = t2.id WHERE t1.active = 1";
        List<Map<String, Object>> expectedData = List.of(
                Map.of("id", 1, "name", "test1"),
                Map.of("id", 2, "name", "test2")
        );
        when(reportDao.executeSql(sql)).thenReturn(expectedData);

        // Act
        List<Map<String, Object>> result = reportService.runReport(sql);

        // Assert
        assertThat(result).isEqualTo(expectedData);
        verify(reportDao, times(1)).executeSql(sql);
    }

    @Test
    @DisplayName("应该成功创建报表当所有字段有效时")
    void should_create_report_successfully_when_all_fields_valid() {
        // Arrange
        Report newReport = new Report();
        newReport.setName("New Report");
        newReport.setSql("SELECT * FROM new_table");

        // Act
        reportService.createReport(newReport);

        // Assert
        verify(reportDao, times(1)).save(newReport);
    }

    @Test
    @DisplayName("应该抛出异常当报表名称为null时")
    void should_throw_exception_when_report_name_is_null() {
        // Arrange
        Report newReport = new Report();
        newReport.setName(null);
        newReport.setSql("SELECT * FROM test_table");

        // Act & Assert
        assertThatThrownBy(() -> reportService.createReport(newReport))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("名称不能为空");

        verify(reportDao, never()).save(any(Report.class));
    }

    @Test
    @DisplayName("应该抛出异常当报表名称为空字符串时")
    void should_throw_exception_when_report_name_is_empty() {
        // Arrange
        Report newReport = new Report();
        newReport.setName("");
        newReport.setSql("SELECT * FROM test_table");

        // Act & Assert
        assertThatThrownBy(() -> reportService.createReport(newReport))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("名称不能为空");

        verify(reportDao, never()).save(any(Report.class));
    }

    @Test
    @DisplayName("应该成功创建报表当报表名称只有空格时")
    void should_create_report_successfully_when_report_name_is_whitespace() {
        // Arrange
        Report newReport = new Report();
        newReport.setName("   ");
        newReport.setSql("SELECT * FROM test_table");

        // Act
        reportService.createReport(newReport);

        // Assert
        verify(reportDao, times(1)).save(newReport);
    }

    @Test
    @DisplayName("应该抛出异常当SQL为null时")
    void should_throw_exception_when_sql_is_null() {
        // Arrange
        Report newReport = new Report();
        newReport.setName("Test Report");
        newReport.setSql(null);

        // Act & Assert
        assertThatThrownBy(() -> reportService.createReport(newReport))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("SQL不能为空");

        verify(reportDao, never()).save(any(Report.class));
    }

    @Test
    @DisplayName("应该抛出异常当SQL为空字符串时")
    void should_throw_exception_when_sql_is_empty() {
        // Arrange
        Report newReport = new Report();
        newReport.setName("Test Report");
        newReport.setSql("");

        // Act & Assert
        assertThatThrownBy(() -> reportService.createReport(newReport))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("SQL不能为空");

        verify(reportDao, never()).save(any(Report.class));
    }

    @Test
    @DisplayName("应该成功创建报表当SQL是空白时")
    void should_create_report_successfully_when_sql_is_whitespace() {
        // Arrange
        Report newReport = new Report();
        newReport.setName("Test Report");
        newReport.setSql("   ");

        // Act
        reportService.createReport(newReport);

        // Assert
        verify(reportDao, times(1)).save(newReport);
    }

    @Test
    @DisplayName("应该成功生成报表当参数为null时")
    void should_generate_report_successfully_when_params_is_null() {
        // Arrange
        Long reportId = 1L;
        String params = null;
        List<Map<String, Object>> testData = List.of(Map.of("id", 1, "name", "test"));
        Map<String, Object> expectedResult = Map.of(
                "reportName", testReport.getName(),
                "data", testData,
                "count", testData.size()
        );

        when(reportDao.findById(reportId)).thenReturn(testReport);
        when(reportDao.executeSql(testReport.getSql())).thenReturn(testData);

        // Act
        Map<String, Object> result = reportService.generateReport(reportId, params);

        // Assert
        assertThat(result).isEqualTo(expectedResult);
        verify(reportDao, times(1)).findById(reportId);
        verify(reportDao, times(1)).executeSql(testReport.getSql());
    }

    @Test
    @DisplayName("应该成功生成报表当参数为空字符串时")
    void should_generate_report_successfully_when_params_is_empty() {
        // Arrange
        Long reportId = 1L;
        String params = "";
        List<Map<String, Object>> testData = List.of(Map.of("id", 1, "name", "test"));
        Map<String, Object> expectedResult = Map.of(
                "reportName", testReport.getName(),
                "data", testData,
                "count", testData.size()
        );

        when(reportDao.findById(reportId)).thenReturn(testReport);
        when(reportDao.executeSql(testReport.getSql())).thenReturn(testData);

        // Act
        Map<String, Object> result = reportService.generateReport(reportId, params);

        // Assert
        assertThat(result).isEqualTo(expectedResult);
        verify(reportDao, times(1)).findById(reportId);
        verify(reportDao, times(1)).executeSql(testReport.getSql());
    }

    @Test
    @DisplayName("应该成功生成报表当有参数时")
    void should_generate_report_successfully_when_params_provided() {
        // Arrange
        Long reportId = 1L;
        String params = "id = 1 AND status = 'active'";
        String expectedSql = testReport.getSql() + " WHERE " + params;
        List<Map<String, Object>> testData = List.of(Map.of("id", 1, "name", "test"));
        Map<String, Object> expectedResult = Map.of(
                "reportName", testReport.getName(),
                "data", testData,
                "count", testData.size()
        );

        when(reportDao.findById(reportId)).thenReturn(testReport);
        when(reportDao.executeSql(expectedSql)).thenReturn(testData);

        // Act
        Map<String, Object> result = reportService.generateReport(reportId, params);

        // Assert
        assertThat(result).isEqualTo(expectedResult);
        verify(reportDao, times(1)).findById(reportId);
        verify(reportDao, times(1)).executeSql(expectedSql);
    }

    @Test
    @DisplayName("应该抛出异常当报表不存在时")
    void should_throw_exception_when_report_not_exists_for_generate() {
        // Arrange
        Long reportId = 999L;
        String params = "id = 1";

        when(reportDao.findById(reportId)).thenReturn(null);

        // Act & Assert
        assertThatThrownBy(() -> reportService.generateReport(reportId, params))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("报表不存在");

        verify(reportDao, times(1)).findById(reportId);
        verify(reportDao, never()).executeSql(anyString());
    }

    @Test
    @DisplayName("应该返回正确的数据计数")
    void should_return_correct_data_count() {
        // Arrange
        Long reportId = 1L;
        String params = null;
        List<Map<String, Object>> testData = List.of(
                Map.of("id", 1, "name", "test1"),
                Map.of("id", 2, "name", "test2"),
                Map.of("id", 3, "name", "test3")
        );

        when(reportDao.findById(reportId)).thenReturn(testReport);
        when(reportDao.executeSql(testReport.getSql())).thenReturn(testData);

        // Act
        Map<String, Object> result = reportService.generateReport(reportId, params);

        // Assert
        assertThat(result.get("count")).isEqualTo(3);
        assertThat(result).containsKey("reportName");
        assertThat(result).containsKey("data");
        assertThat(result).containsKey("count");
    }

    @Test
    @DisplayName("应该处理空数据结果")
    void should_handle_empty_data_result() {
        // Arrange
        Long reportId = 1L;
        String params = null;
        List<Map<String, Object>> testData = List.of();

        when(reportDao.findById(reportId)).thenReturn(testReport);
        when(reportDao.executeSql(testReport.getSql())).thenReturn(testData);

        // Act
        Map<String, Object> result = reportService.generateReport(reportId, params);

        // Assert
        assertThat(result.get("count")).isEqualTo(0);
        assertThat(result.get("data")).isEqualTo(testData);
    }

    @Test
    @DisplayName("应该处理极值参数")
    void should_handle_boundary_values() {
        // Arrange
        Report newReport = new Report();
        newReport.setName("a");
        newReport.setSql("b");

        // Act
        reportService.createReport(newReport);

        // Assert
        verify(reportDao, times(1)).save(newReport);
    }
}
