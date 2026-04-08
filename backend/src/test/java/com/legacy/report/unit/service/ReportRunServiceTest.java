package com.legacy.report.unit.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.legacy.report.model.Report;
import com.legacy.report.model.ReportAuditEvent;
import com.legacy.report.model.ReportRun;
import com.legacy.report.model.User;
import com.legacy.report.repository.ReportAuditEventRepository;
import com.legacy.report.repository.ReportRunRepository;
import com.legacy.report.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link ReportRunService}.
 * Tests report execution workflow with mocked dependencies.
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("ReportRunService Unit Tests")
class ReportRunServiceTest {

    @Mock
    private ReportService reportService;

    @Mock
    private SqlQueryService sqlQueryService;

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

    @InjectMocks
    private ReportRunService reportRunService;

    private User makerUser;
    private User checkerUser;
    private Report testReport;
    private ReportRun testRun;

    @BeforeEach
    void setUp() {
        makerUser = new User();
        makerUser.setId(1L);
        makerUser.setUsername("maker1");
        makerUser.setRole("MAKER");

        checkerUser = new User();
        checkerUser.setId(2L);
        checkerUser.setUsername("checker1");
        checkerUser.setRole("CHECKER");

        testReport = new Report();
        testReport.setId(10L);
        testReport.setName("Test Report");
        testReport.setSql("SELECT * FROM test");

        testRun = new ReportRun();
        testRun.setId(100L);
        testRun.setReportId(10L);
        testRun.setReportName("Test Report");
        testRun.setStatus("Generated");
        testRun.setMakerUsername("maker1");
        testRun.setGeneratedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("executeReportWithRun should create run and execute report")
    void executeReportWithRun_ShouldCreateRunAndExecuteReport() throws Exception {
        // Given
        List<Map<String, Object>> queryResult = List.of(Map.of("id", 1, "name", "test"));

        when(currentUserService.getCurrentUserOrThrow()).thenReturn(makerUser);
        when(reportService.getReportById(10L)).thenReturn(testReport);
        when(sqlQueryService.runReport(testReport.getSql())).thenReturn(queryResult);
        when(objectMapper.writeValueAsString(queryResult)).thenReturn("[{\"id\":1,\"name\":\"test\"}]");
        when(reportRunRepository.save(any(ReportRun.class))).thenAnswer(invocation -> {
            ReportRun run = invocation.getArgument(0);
            run.setId(100L);
            return run;
        });

        // When
        List<Map<String, Object>> result = reportRunService.executeReportWithRun(10L);

        // Then
        assertThat(result).isEqualTo(queryResult);
        verify(reportRunRepository).save(argThat(run ->
                run.getStatus().equals("Generated") &&
                run.getMakerUsername().equals("maker1") &&
                run.getReportId().equals(10L)
        ));
        verify(auditService).recordEvent(eq(100L), eq(10L), eq("maker1"), eq("MAKER"), eq("Generated"), isNull());
    }

    @Test
    @DisplayName("executeReportWithRun should throw when user is not MAKER")
    void executeReportWithRun_ShouldThrow_WhenUserNotMaker() {
        // Given
        checkerUser.setRole("CHECKER");
        when(currentUserService.getCurrentUserOrThrow()).thenReturn(checkerUser);
        doThrow(new RuntimeException("当前用户没有所需角色: MAKER"))
                .when(currentUserService).requireRole(checkerUser, "MAKER");

        // When/Then
        assertThatThrownBy(() -> reportRunService.executeReportWithRun(10L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("MAKER");

        verify(reportRunRepository, never()).save(any());
    }

    @Test
    @DisplayName("executeReportWithRun should throw when report not found")
    void executeReportWithRun_ShouldThrow_WhenReportNotFound() {
        // Given
        when(currentUserService.getCurrentUserOrThrow()).thenReturn(makerUser);
        when(reportService.getReportById(99L)).thenReturn(null);

        // When/Then
        assertThatThrownBy(() -> reportRunService.executeReportWithRun(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("报表不存在");
    }

    @Test
    @DisplayName("submitRun should submit generated run")
    void submitRun_ShouldSubmitGeneratedRun() {
        // Given
        when(currentUserService.getCurrentUserOrThrow()).thenReturn(makerUser);
        when(reportRunRepository.findById(100L)).thenReturn(Optional.of(testRun));
        when(reportRunRepository.save(any(ReportRun.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        ReportRun result = reportRunService.submitRun(100L);

        // Then
        assertThat(result.getStatus()).isEqualTo("Submitted");
        assertThat(result.getSubmittedAt()).isNotNull();
        verify(auditService).recordEvent(eq(100L), eq(10L), eq("maker1"), eq("MAKER"), eq("Submitted"), isNull());
    }

    @Test
    @DisplayName("submitRun should throw when run not in Generated status")
    void submitRun_ShouldThrow_WhenNotGeneratedStatus() {
        // Given
        testRun.setStatus("Submitted");
        when(currentUserService.getCurrentUserOrThrow()).thenReturn(makerUser);
        when(reportRunRepository.findById(100L)).thenReturn(Optional.of(testRun));

        // When/Then
        assertThatThrownBy(() -> reportRunService.submitRun(100L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("只能提交 Generated 状态的报表运行实例");
    }

    @Test
    @DisplayName("decideRun should approve submitted run")
    void decideRun_ShouldApproveSubmittedRun() {
        // Given
        testRun.setStatus("Submitted");
        testRun.setSubmittedAt(LocalDateTime.now().minusHours(1));

        when(currentUserService.getCurrentUserOrThrow()).thenReturn(checkerUser);
        when(reportRunRepository.findById(100L)).thenReturn(Optional.of(testRun));
        when(reportRunRepository.save(any(ReportRun.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        ReportRun result = reportRunService.decideRun(100L, true, "Approved after review");

        // Then
        assertThat(result.getStatus()).isEqualTo("Approved");
        assertThat(result.getCheckerUsername()).isEqualTo("checker1");
        assertThat(result.getDecidedAt()).isNotNull();
        verify(auditService).recordEvent(eq(100L), eq(10L), eq("checker1"), eq("CHECKER"), eq("Approved"), eq("Approved after review"));
    }

    @Test
    @DisplayName("decideRun should reject submitted run with comment")
    void decideRun_ShouldRejectSubmittedRun() {
        // Given
        testRun.setStatus("Submitted");
        testRun.setSubmittedAt(LocalDateTime.now().minusHours(1));
        testRun.setGeneratedAt(LocalDateTime.now().minusHours(2));

        when(currentUserService.getCurrentUserOrThrow()).thenReturn(checkerUser);
        when(reportRunRepository.findById(100L)).thenReturn(Optional.of(testRun));
        when(reportRunRepository.save(any(ReportRun.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        ReportRun result = reportRunService.decideRun(100L, false, "Data validation failed");

        // Then
        assertThat(result.getStatus()).isEqualTo("Rejected");
        assertThat(result.getCheckerUsername()).isEqualTo("checker1");
        verify(auditService).recordEvent(eq(100L), eq(10L), eq("checker1"), eq("CHECKER"), eq("Rejected"), eq("Data validation failed"));
    }

    @Test
    @DisplayName("decideRun should throw when rejecting without comment")
    void decideRun_ShouldThrow_WhenRejectWithoutComment() {
        // Given
        testRun.setStatus("Submitted");
        when(currentUserService.getCurrentUserOrThrow()).thenReturn(checkerUser);
        when(reportRunRepository.findById(100L)).thenReturn(Optional.of(testRun));

        // When/Then
        assertThatThrownBy(() -> reportRunService.decideRun(100L, false, null))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("拒绝审批时必须填写 comment");
    }

    @Test
    @DisplayName("decideRun should throw when run not in Submitted status")
    void decideRun_ShouldThrow_WhenNotSubmittedStatus() {
        // Given
        testRun.setStatus("Generated");
        when(currentUserService.getCurrentUserOrThrow()).thenReturn(checkerUser);
        when(reportRunRepository.findById(100L)).thenReturn(Optional.of(testRun));

        // When/Then
        assertThatThrownBy(() -> reportRunService.decideRun(100L, true, null))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("只能对 Submitted 状态的报表运行实例进行审批");
    }

    @Test
    @DisplayName("getLatestRunForCurrentMaker should return latest run")
    void getLatestRunForCurrentMaker_ShouldReturnLatestRun() {
        // Given
        when(currentUserService.getCurrentUserOrThrow()).thenReturn(makerUser);
        when(reportRunRepository.findByMakerUsernameAndReportIdOrderByGeneratedAtDesc("maker1", 10L))
                .thenReturn(List.of(testRun));

        // When
        ReportRun result = reportRunService.getLatestRunForCurrentMaker(10L);

        // Then
        assertThat(result).isEqualTo(testRun);
    }

    @Test
    @DisplayName("getLatestRunForCurrentMaker should throw when no runs found")
    void getLatestRunForCurrentMaker_ShouldThrow_WhenNoRunsFound() {
        // Given
        when(currentUserService.getCurrentUserOrThrow()).thenReturn(makerUser);
        when(reportRunRepository.findByMakerUsernameAndReportIdOrderByGeneratedAtDesc("maker1", 99L))
                .thenReturn(List.of());

        // When/Then
        assertThatThrownBy(() -> reportRunService.getLatestRunForCurrentMaker(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("当前用户在该报表下没有执行记录");
    }

    @Test
    @DisplayName("getSubmittedRunsForChecker should return submitted runs")
    void getSubmittedRunsForChecker_ShouldReturnSubmittedRuns() {
        // Given
        when(currentUserService.getCurrentUserOrThrow()).thenReturn(checkerUser);
        when(reportRunRepository.findByStatusOrderBySubmittedAtAsc("Submitted"))
                .thenReturn(List.of(testRun));

        // When
        List<ReportRun> result = reportRunService.getSubmittedRunsForChecker();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(testRun);
    }

    @Test
    @DisplayName("getAuditEventsForRun should return audit events")
    void getAuditEventsForRun_ShouldReturnAuditEvents() {
        // Given
        ReportAuditEvent event = new ReportAuditEvent();
        event.setId(1L);
        event.setReportRunId(100L);
        event.setEventType("Generated");

        when(currentUserService.getCurrentUserOrThrow()).thenReturn(makerUser);
        when(reportAuditEventRepository.findByReportRunIdOrderByEventTimeAsc(100L))
                .thenReturn(List.of(event));

        // When
        List<ReportAuditEvent> result = reportRunService.getAuditEventsForRun(100L);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEventType()).isEqualTo("Generated");
    }
}
