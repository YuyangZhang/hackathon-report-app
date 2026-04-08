package com.legacy.report.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.legacy.report.model.Report;
import com.legacy.report.model.ReportAuditEvent;
import com.legacy.report.model.ReportRun;
import com.legacy.report.model.User;
import com.legacy.report.repository.ReportAuditEventRepository;
import com.legacy.report.repository.ReportRunRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ReportRunService 单元测试")
class ReportRunServiceTest {

    @Mock
    private ReportService reportService;

    @Mock
    private ReportRunRepository reportRunRepository;

    @Mock
    private AuditService auditService;

    @Mock
    private CurrentUserService currentUserService;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private ReportAuditEventRepository reportAuditEventRepository;

    @Mock
    private MeterRegistry meterRegistry;

    @Mock
    private Counter generatedCounter;

    @Mock
    private Counter submittedCounter;

    @Mock
    private Counter approvedCounter;

    @Mock
    private Counter rejectedCounter;

    @Mock
    private Timer approvalDurationTimer;

    @InjectMocks
    private ReportRunService reportRunService;

    private User testUser;
    private User checkerUser;
    private Report testReport;
    private ReportRun testReportRun;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("makerUser");
        testUser.setRole("MAKER");

        checkerUser = new User();
        checkerUser.setId(2L);
        checkerUser.setUsername("checkerUser");
        checkerUser.setRole("CHECKER");

        testReport = new Report();
        testReport.setId(1L);
        testReport.setName("Test Report");
        testReport.setSql("SELECT * FROM test_table");

        testReportRun = new ReportRun();
        testReportRun.setId(1L);
        testReportRun.setReportId(1L);
        testReportRun.setReportName("Test Report");
        testReportRun.setStatus("Generated");
        testReportRun.setMakerUsername("makerUser");
        testReportRun.setGeneratedAt(LocalDateTime.now());
    }

    @AfterEach
    void tearDown() {
        // 测试后清理
    }

    @Test
    @DisplayName("应该成功执行报表并创建运行记录")
    void should_execute_report_and_create_run_successfully() throws JsonProcessingException {
        // Arrange
        Long reportId = 1L;
        List<Map<String, Object>> testData = List.of(Map.of("id", 1, "name", "test"));
        String snapshotJson = "[{\"id\": 1, \"name\": \"test\"}]";

        when(currentUserService.getCurrentUserOrThrow()).thenReturn(testUser);
        when(reportService.getReportById(reportId)).thenReturn(testReport);
        when(reportService.runReport(testReport.getSql())).thenReturn(testData);
        when(objectMapper.writeValueAsString(testData)).thenReturn(snapshotJson);
        when(reportRunRepository.save(any(ReportRun.class))).thenAnswer(invocation -> {
            ReportRun run = invocation.getArgument(0);
            run.setId(1L);
            return run;
        });

        // Act
        List<Map<String, Object>> result = reportRunService.executeReportWithRun(reportId);

        // Assert
        assertThat(result).isEqualTo(testData);
        verify(currentUserService, times(1)).getCurrentUserOrThrow();
        verify(currentUserService, times(1)).requireRole(testUser, "MAKER");
        verify(reportService, times(1)).getReportById(reportId);
        verify(reportService, times(1)).runReport(testReport.getSql());
        verify(reportRunRepository, times(1)).save(argThat(run -> {
            assertThat(run.getReportId()).isEqualTo(reportId);
            assertThat(run.getReportName()).isEqualTo(testReport.getName());
            assertThat(run.getStatus()).isEqualTo("Generated");
            assertThat(run.getMakerUsername()).isEqualTo(testUser.getUsername());
            assertThat(run.getGeneratedAt()).isNotNull();
            assertThat(run.getResultSnapshot()).isEqualTo(snapshotJson);
            return true;
        }));
        verify(auditService, times(1)).recordEvent(eq(1L), eq(reportId), eq(testUser.getUsername()), eq(testUser.getRole()), eq("Generated"), isNull());
    }

    @Test
    @DisplayName("应该成功执行报表当JSON序列化失败时")
    void should_execute_report_successfully_when_json_serialization_fails() throws JsonProcessingException {
        // Arrange
        Long reportId = 1L;
        List<Map<String, Object>> testData = List.of(Map.of("id", 1, "name", "test"));

        when(currentUserService.getCurrentUserOrThrow()).thenReturn(testUser);
        when(reportService.getReportById(reportId)).thenReturn(testReport);
        when(reportService.runReport(testReport.getSql())).thenReturn(testData);
        when(objectMapper.writeValueAsString(testData)).thenThrow(new JsonProcessingException("Serialization error") {});
        when(reportRunRepository.save(any(ReportRun.class))).thenAnswer(invocation -> {
            ReportRun run = invocation.getArgument(0);
            run.setId(1L);
            return run;
        });

        // Act
        List<Map<String, Object>> result = reportRunService.executeReportWithRun(reportId);

        // Assert
        assertThat(result).isEqualTo(testData);
        verify(reportRunRepository, times(1)).save(argThat(run -> 
            run.getResultSnapshot() == null
        ));
    }

    @Test
    @DisplayName("应该抛出异常当报表不存在时")
    void should_throw_exception_when_report_not_exists() {
        // Arrange
        Long reportId = 1L;

        when(currentUserService.getCurrentUserOrThrow()).thenReturn(testUser);
        when(reportService.getReportById(reportId)).thenReturn(null);

        // Act & Assert
        assertThatThrownBy(() -> reportRunService.executeReportWithRun(reportId))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("报表不存在");

        verify(reportRunRepository, never()).save(any(ReportRun.class));
        verify(auditService, never()).recordEvent(anyLong(), anyLong(), anyString(), anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("应该成功提交运行记录")
    void should_submit_run_successfully() {
        // Arrange
        Long runId = 1L;
        testReportRun.setStatus("Generated");

        when(currentUserService.getCurrentUserOrThrow()).thenReturn(testUser);
        when(reportRunRepository.findById(runId)).thenReturn(java.util.Optional.of(testReportRun));
        when(reportRunRepository.save(any(ReportRun.class))).thenReturn(testReportRun);

        // Act
        ReportRun result = reportRunService.submitRun(runId);

        // Assert
        assertThat(result.getStatus()).isEqualTo("Submitted");
        assertThat(result.getSubmittedAt()).isNotNull();
        verify(currentUserService, times(1)).getCurrentUserOrThrow();
        verify(currentUserService, times(1)).requireRole(testUser, "MAKER");
        verify(reportRunRepository, times(1)).findById(runId);
        verify(reportRunRepository, times(1)).save(argThat(run -> {
            assertThat(run.getStatus()).isEqualTo("Submitted");
            assertThat(run.getSubmittedAt()).isNotNull();
            return true;
        }));
        verify(auditService, times(1)).recordEvent(eq(runId), eq(testReportRun.getReportId()), eq(testUser.getUsername()), eq(testUser.getRole()), eq("Submitted"), isNull());
    }

    @Test
    @DisplayName("应该抛出异常当提交非Generated状态的运行记录时")
    void should_throw_exception_when_submitting_non_generated_run() {
        // Arrange
        Long runId = 1L;
        testReportRun.setStatus("Submitted");

        when(currentUserService.getCurrentUserOrThrow()).thenReturn(testUser);
        when(reportRunRepository.findById(runId)).thenReturn(java.util.Optional.of(testReportRun));

        // Act & Assert
        assertThatThrownBy(() -> reportRunService.submitRun(runId))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("只能提交 Generated 状态的报表运行实例");

        verify(reportRunRepository, never()).save(any(ReportRun.class));
        verify(auditService, never()).recordEvent(anyLong(), anyLong(), anyString(), anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("应该抛出异常当提交其他用户的运行记录时")
    void should_throw_exception_when_submitting_other_users_run() {
        // Arrange
        Long runId = 1L;
        testReportRun.setStatus("Generated");
        testReportRun.setMakerUsername("otherUser");

        when(currentUserService.getCurrentUserOrThrow()).thenReturn(testUser);
        when(reportRunRepository.findById(runId)).thenReturn(java.util.Optional.of(testReportRun));

        // Act & Assert
        assertThatThrownBy(() -> reportRunService.submitRun(runId))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("只能提交由当前 Maker 自己生成的报表运行实例");

        verify(reportRunRepository, never()).save(any(ReportRun.class));
        verify(auditService, never()).recordEvent(anyLong(), anyLong(), anyString(), anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("应该成功批准运行记录")
    void should_approve_run_successfully() {
        // Arrange
        Long runId = 1L;
        testReportRun.setStatus("Submitted");
        testReportRun.setGeneratedAt(LocalDateTime.now().minusMinutes(10));

        when(currentUserService.getCurrentUserOrThrow()).thenReturn(checkerUser);
        when(reportRunRepository.findById(runId)).thenReturn(java.util.Optional.of(testReportRun));
        when(reportRunRepository.save(any(ReportRun.class))).thenReturn(testReportRun);

        // Act
        ReportRun result = reportRunService.decideRun(runId, true, null);

        // Assert
        assertThat(result.getStatus()).isEqualTo("Approved");
        assertThat(result.getCheckerUsername()).isEqualTo(checkerUser.getUsername());
        assertThat(result.getDecidedAt()).isNotNull();
        verify(currentUserService, times(1)).getCurrentUserOrThrow();
        verify(currentUserService, times(1)).requireRole(checkerUser, "CHECKER");
        verify(reportRunRepository, times(1)).findById(runId);
        verify(reportRunRepository, times(1)).save(argThat(run -> {
            assertThat(run.getStatus()).isEqualTo("Approved");
            assertThat(run.getCheckerUsername()).isEqualTo(checkerUser.getUsername());
            assertThat(run.getDecidedAt()).isNotNull();
            return true;
        }));
        verify(auditService, times(1)).recordEvent(eq(runId), eq(testReportRun.getReportId()), eq(checkerUser.getUsername()), eq(checkerUser.getRole()), eq("Approved"), isNull());
    }

    @Test
    @DisplayName("应该成功拒绝运行记录当提供评论时")
    void should_reject_run_successfully_when_comment_provided() {
        // Arrange
        Long runId = 1L;
        String comment = "需要修改";
        testReportRun.setStatus("Submitted");

        when(currentUserService.getCurrentUserOrThrow()).thenReturn(checkerUser);
        when(reportRunRepository.findById(runId)).thenReturn(java.util.Optional.of(testReportRun));
        when(reportRunRepository.save(any(ReportRun.class))).thenReturn(testReportRun);

        // Act
        ReportRun result = reportRunService.decideRun(runId, false, comment);

        // Assert
        assertThat(result.getStatus()).isEqualTo("Rejected");
        assertThat(result.getCheckerUsername()).isEqualTo(checkerUser.getUsername());
        assertThat(result.getDecidedAt()).isNotNull();
        verify(auditService, times(1)).recordEvent(eq(runId), eq(testReportRun.getReportId()), eq(checkerUser.getUsername()), eq(checkerUser.getRole()), eq("Rejected"), eq(comment));
    }

    @Test
    @DisplayName("应该抛出异常当拒绝时未提供评论")
    void should_throw_exception_when_rejecting_without_comment() {
        // Arrange
        Long runId = 1L;
        testReportRun.setStatus("Submitted");

        when(currentUserService.getCurrentUserOrThrow()).thenReturn(checkerUser);
        when(reportRunRepository.findById(runId)).thenReturn(java.util.Optional.of(testReportRun));

        // Act & Assert
        assertThatThrownBy(() -> reportRunService.decideRun(runId, false, null))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("拒绝审批时必须填写 comment");

        verify(reportRunRepository, never()).save(any(ReportRun.class));
        verify(auditService, never()).recordEvent(anyLong(), anyLong(), anyString(), anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("应该抛出异常当拒绝时评论为空字符串")
    void should_throw_exception_when_rejecting_with_empty_comment() {
        // Arrange
        Long runId = 1L;
        testReportRun.setStatus("Submitted");

        when(currentUserService.getCurrentUserOrThrow()).thenReturn(checkerUser);
        when(reportRunRepository.findById(runId)).thenReturn(java.util.Optional.of(testReportRun));

        // Act & Assert
        assertThatThrownBy(() -> reportRunService.decideRun(runId, false, ""))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("拒绝审批时必须填写 comment");
    }

    @Test
    @DisplayName("应该抛出异常当审批非Submitted状态的运行记录时")
    void should_throw_exception_when_deciding_non_submitted_run() {
        // Arrange
        Long runId = 1L;
        testReportRun.setStatus("Generated");

        when(currentUserService.getCurrentUserOrThrow()).thenReturn(checkerUser);
        when(reportRunRepository.findById(runId)).thenReturn(java.util.Optional.of(testReportRun));

        // Act & Assert
        assertThatThrownBy(() -> reportRunService.decideRun(runId, true, null))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("只能对 Submitted 状态的报表运行实例进行审批");

        verify(reportRunRepository, never()).save(any(ReportRun.class));
        verify(auditService, never()).recordEvent(anyLong(), anyLong(), anyString(), anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("应该返回最新运行记录当存在时")
    void should_return_latest_run_when_exists() {
        // Arrange
        Long reportId = 1L;
        List<ReportRun> runs = List.of(testReportRun);

        when(currentUserService.getCurrentUserOrThrow()).thenReturn(testUser);
        when(reportRunRepository.findByMakerUsernameAndReportIdOrderByGeneratedAtDesc(testUser.getUsername(), reportId))
                .thenReturn(runs);

        // Act
        ReportRun result = reportRunService.getLatestRunForCurrentMaker(reportId);

        // Assert
        assertThat(result).isEqualTo(testReportRun);
        verify(currentUserService, times(1)).getCurrentUserOrThrow();
        verify(currentUserService, times(1)).requireRole(testUser, "MAKER");
        verify(reportRunRepository, times(1)).findByMakerUsernameAndReportIdOrderByGeneratedAtDesc(testUser.getUsername(), reportId);
    }

    @Test
    @DisplayName("应该抛出异常当没有运行记录时")
    void should_throw_exception_when_no_runs_exist() {
        // Arrange
        Long reportId = 1L;
        List<ReportRun> runs = List.of();

        when(currentUserService.getCurrentUserOrThrow()).thenReturn(testUser);
        when(reportRunRepository.findByMakerUsernameAndReportIdOrderByGeneratedAtDesc(testUser.getUsername(), reportId))
                .thenReturn(runs);

        // Act & Assert
        assertThatThrownBy(() -> reportRunService.getLatestRunForCurrentMaker(reportId))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("当前用户在该报表下没有执行记录");
    }

    @Test
    @DisplayName("应该返回当前Maker的所有运行记录")
    void should_return_all_runs_for_current_maker() {
        // Arrange
        List<ReportRun> runs = List.of(testReportRun);

        when(currentUserService.getCurrentUserOrThrow()).thenReturn(testUser);
        when(reportRunRepository.findByMakerUsernameOrderByGeneratedAtDesc(testUser.getUsername()))
                .thenReturn(runs);

        // Act
        List<ReportRun> result = reportRunService.getRunsForCurrentMaker();

        // Assert
        assertThat(result).isEqualTo(runs);
        verify(currentUserService, times(1)).getCurrentUserOrThrow();
        verify(currentUserService, times(1)).requireRole(testUser, "MAKER");
        verify(reportRunRepository, times(1)).findByMakerUsernameOrderByGeneratedAtDesc(testUser.getUsername());
    }

    @Test
    @DisplayName("应该返回所有已提交的运行记录给Checker")
    void should_return_submitted_runs_for_checker() {
        // Arrange
        List<ReportRun> runs = List.of(testReportRun);

        when(currentUserService.getCurrentUserOrThrow()).thenReturn(checkerUser);
        when(reportRunRepository.findByStatusOrderBySubmittedAtAsc("Submitted"))
                .thenReturn(runs);

        // Act
        List<ReportRun> result = reportRunService.getSubmittedRunsForChecker();

        // Assert
        assertThat(result).isEqualTo(runs);
        verify(currentUserService, times(1)).getCurrentUserOrThrow();
        verify(currentUserService, times(1)).requireRole(checkerUser, "CHECKER");
        verify(reportRunRepository, times(1)).findByStatusOrderBySubmittedAtAsc("Submitted");
    }

    @Test
    @DisplayName("应该返回Checker的历史运行记录")
    void should_return_history_runs_for_checker() {
        // Arrange
        List<ReportRun> runs = List.of(testReportRun);

        when(currentUserService.getCurrentUserOrThrow()).thenReturn(checkerUser);
        when(reportRunRepository.findByCheckerUsernameOrderByDecidedAtDesc(checkerUser.getUsername()))
                .thenReturn(runs);

        // Act
        List<ReportRun> result = reportRunService.getHistoryRunsForCurrentChecker();

        // Assert
        assertThat(result).isEqualTo(runs);
        verify(currentUserService, times(1)).getCurrentUserOrThrow();
        verify(currentUserService, times(1)).requireRole(checkerUser, "CHECKER");
        verify(reportRunRepository, times(1)).findByCheckerUsernameOrderByDecidedAtDesc(checkerUser.getUsername());
    }

    @Test
    @DisplayName("应该返回指定运行记录的审计事件")
    void should_return_audit_events_for_run() {
        // Arrange
        Long reportRunId = 1L;
        List<ReportAuditEvent> events = List.of(new ReportAuditEvent());

        when(currentUserService.getCurrentUserOrThrow()).thenReturn(testUser);
        when(reportAuditEventRepository.findByReportRunIdOrderByEventTimeAsc(reportRunId))
                .thenReturn(events);

        // Act
        List<ReportAuditEvent> result = reportRunService.getAuditEventsForRun(reportRunId);

        // Assert
        assertThat(result).isEqualTo(events);
    }

    @Test
    @DisplayName("应该处理null的MeterRegistry")
    void should_handle_null_meter_registry() {
        // Act
        reportRunService.setMeterRegistry(null);

        // Assert
        // 验证方法不会抛出异常
    }
}
