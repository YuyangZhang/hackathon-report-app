package com.legacy.report.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.legacy.report.model.Report;
import com.legacy.report.model.ReportAuditEvent;
import com.legacy.report.model.ReportRun;
import com.legacy.report.model.User;
import com.legacy.report.repository.ReportAuditEventRepository;
import com.legacy.report.repository.ReportRunRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportRunServiceTest {

    @Mock
    private ReportService reportService;

    @Mock
    private ReportExecutionService reportExecutionService;

    @Mock
    private ReportRunRepository reportRunRepository;

    @Mock
    private AuditService auditService;

    @Mock
    private CurrentUserService currentUserService;

    @Mock
    private ReportAuditEventRepository reportAuditEventRepository;

    @InjectMocks
    private ReportRunService reportRunService;

    @BeforeEach
    void setUp() {
        reportRunService.setMeterRegistry(null);
        reportRunService.getClass();
        injectObjectMapper();
    }

    @Test
    void executeReportWithRunCreatesGeneratedRunForMaker() {
        User maker = user("maker1", "MAKER");
        Report report = report(1L, "Customer Transaction Analysis");
        List<Map<String, Object>> result = List.of(Map.of("name", "Customer A", "total", 100));

        when(currentUserService.getCurrentUserOrThrow()).thenReturn(maker);
        when(reportService.getReportById(1L)).thenReturn(report);
        when(reportExecutionService.executeReport(report)).thenReturn(result);
        when(reportRunRepository.save(any(ReportRun.class))).thenAnswer(invocation -> {
            ReportRun saved = invocation.getArgument(0);
            saved.setId(10L);
            return saved;
        });

        List<Map<String, Object>> actual = reportRunService.executeReportWithRun(1L);

        assertEquals(result, actual);
        verify(currentUserService).requireRole(maker, "MAKER");
        verify(auditService).recordEvent(10L, 1L, "maker1", "MAKER", "Generated", null);
    }

    @Test
    void submitRunRejectsNonOwnerMaker() {
        User maker = user("maker1", "MAKER");
        ReportRun run = generatedRun(20L, "maker2", "Generated");

        when(currentUserService.getCurrentUserOrThrow()).thenReturn(maker);
        when(reportRunRepository.findById(20L)).thenReturn(Optional.of(run));

        RuntimeException error = assertThrows(RuntimeException.class, () -> reportRunService.submitRun(20L));

        assertTrue(error.getMessage().contains("当前 Maker 自己生成"));
        verify(reportRunRepository, never()).save(any(ReportRun.class));
    }

    @Test
    void decideRunRequiresCommentWhenRejected() {
        User checker = user("checker1", "CHECKER");
        ReportRun run = generatedRun(30L, "maker1", "Submitted");

        when(currentUserService.getCurrentUserOrThrow()).thenReturn(checker);
        when(reportRunRepository.findById(30L)).thenReturn(Optional.of(run));

        RuntimeException error = assertThrows(RuntimeException.class, () -> reportRunService.decideRun(30L, false, "  "));

        assertTrue(error.getMessage().contains("必须填写 comment"));
        verify(reportRunRepository, never()).save(any(ReportRun.class));
    }

    @Test
    void getAuditEventsForRunReturnsOrderedEventsForAuthenticatedUser() {
        User checker = user("checker1", "CHECKER");
        List<ReportAuditEvent> events = List.of(new ReportAuditEvent(), new ReportAuditEvent());

        when(currentUserService.getCurrentUserOrThrow()).thenReturn(checker);
        when(reportAuditEventRepository.findByReportRunIdOrderByEventTimeAsc(40L)).thenReturn(events);

        List<ReportAuditEvent> actual = reportRunService.getAuditEventsForRun(40L);

        assertEquals(events, actual);
        verify(currentUserService).getCurrentUserOrThrow();
    }

    private void injectObjectMapper() {
        try {
            var field = ReportRunService.class.getDeclaredField("objectMapper");
            field.setAccessible(true);
            field.set(reportRunService, new ObjectMapper());
        } catch (ReflectiveOperationException ex) {
            throw new RuntimeException(ex);
        }
    }

    private static User user(String username, String role) {
        User user = new User();
        user.setUsername(username);
        user.setRole(role);
        return user;
    }

    private static Report report(Long id, String name) {
        Report report = new Report();
        report.setId(id);
        report.setName(name);
        report.setSql("SELECT 1");
        return report;
    }

    private static ReportRun generatedRun(Long id, String makerUsername, String status) {
        ReportRun run = new ReportRun();
        run.setId(id);
        run.setReportId(1L);
        run.setReportName("Customer Transaction Analysis");
        run.setMakerUsername(makerUsername);
        run.setStatus(status);
        run.setGeneratedAt(LocalDateTime.now().minusMinutes(1));
        return run;
    }
}
