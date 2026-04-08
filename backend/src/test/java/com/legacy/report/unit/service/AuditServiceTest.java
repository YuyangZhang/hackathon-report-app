package com.legacy.report.unit.service;

import com.legacy.report.model.ReportAuditEvent;
import com.legacy.report.repository.ReportAuditEventRepository;
import com.legacy.report.service.AuditService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link AuditService}.
 * Tests audit event recording functionality.
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("AuditService Unit Tests")
class AuditServiceTest {

    @Mock
    private ReportAuditEventRepository reportAuditEventRepository;

    @InjectMocks
    private AuditService auditService;

    private ReportAuditEvent savedEvent;

    @BeforeEach
    void setUp() {
        savedEvent = new ReportAuditEvent();
        savedEvent.setId(1L);
        savedEvent.setReportRunId(100L);
        savedEvent.setReportId(10L);
        savedEvent.setActorUsername("testuser");
        savedEvent.setActorRole("MAKER");
        savedEvent.setEventType("Generated");

        when(reportAuditEventRepository.save(any(ReportAuditEvent.class))).thenAnswer(invocation -> {
            ReportAuditEvent event = invocation.getArgument(0);
            event.setId(1L);
            return event;
        });
    }

    @Test
    @DisplayName("recordEvent should save audit event with all fields")
    void recordEvent_ShouldSaveAuditEventWithAllFields() {
        // Given
        Long reportRunId = 100L;
        Long reportId = 10L;
        String actorUsername = "maker1";
        String actorRole = "MAKER";
        String eventType = "Generated";
        String comment = "Report generated successfully";

        // When
        auditService.recordEvent(reportRunId, reportId, actorUsername, actorRole, eventType, comment);

        // Then
        ArgumentCaptor<ReportAuditEvent> captor = ArgumentCaptor.forClass(ReportAuditEvent.class);
        verify(reportAuditEventRepository).save(captor.capture());

        ReportAuditEvent captured = captor.getValue();
        assertThat(captured.getReportRunId()).isEqualTo(reportRunId);
        assertThat(captured.getReportId()).isEqualTo(reportId);
        assertThat(captured.getActorUsername()).isEqualTo(actorUsername);
        assertThat(captured.getActorRole()).isEqualTo(actorRole);
        assertThat(captured.getEventType()).isEqualTo(eventType);
        assertThat(captured.getComment()).isEqualTo(comment);
        assertThat(captured.getEventTime()).isNotNull();
    }

    @Test
    @DisplayName("recordEvent should handle null comment")
    void recordEvent_ShouldHandleNullComment() {
        // Given
        Long reportRunId = 100L;
        Long reportId = 10L;
        String actorUsername = "maker1";
        String actorRole = "MAKER";
        String eventType = "Submitted";
        String comment = null;

        // When
        auditService.recordEvent(reportRunId, reportId, actorUsername, actorRole, eventType, comment);

        // Then
        ArgumentCaptor<ReportAuditEvent> captor = ArgumentCaptor.forClass(ReportAuditEvent.class);
        verify(reportAuditEventRepository).save(captor.capture());

        ReportAuditEvent captured = captor.getValue();
        assertThat(captured.getComment()).isNull();
        assertThat(captured.getEventType()).isEqualTo("Submitted");
    }

    @Test
    @DisplayName("recordEvent should handle empty comment")
    void recordEvent_ShouldHandleEmptyComment() {
        // Given
        Long reportRunId = 100L;
        Long reportId = 10L;
        String actorUsername = "checker1";
        String actorRole = "CHECKER";
        String eventType = "Approved";
        String comment = "";

        // When
        auditService.recordEvent(reportRunId, reportId, actorUsername, actorRole, eventType, comment);

        // Then
        ArgumentCaptor<ReportAuditEvent> captor = ArgumentCaptor.forClass(ReportAuditEvent.class);
        verify(reportAuditEventRepository).save(captor.capture());

        ReportAuditEvent captured = captor.getValue();
        assertThat(captured.getComment()).isEmpty();
        assertThat(captured.getEventType()).isEqualTo("Approved");
    }

    @Test
    @DisplayName("recordEvent should handle rejected event with comment")
    void recordEvent_ShouldHandleRejectedEventWithComment() {
        // Given
        Long reportRunId = 100L;
        Long reportId = 10L;
        String actorUsername = "checker1";
        String actorRole = "CHECKER";
        String eventType = "Rejected";
        String comment = "Data discrepancy found in row 5";

        // When
        auditService.recordEvent(reportRunId, reportId, actorUsername, actorRole, eventType, comment);

        // Then
        ArgumentCaptor<ReportAuditEvent> captor = ArgumentCaptor.forClass(ReportAuditEvent.class);
        verify(reportAuditEventRepository).save(captor.capture());

        ReportAuditEvent captured = captor.getValue();
        assertThat(captured.getEventType()).isEqualTo("Rejected");
        assertThat(captured.getComment()).isEqualTo("Data discrepancy found in row 5");
    }

    @Test
    @DisplayName("recordEvent should set event time automatically")
    void recordEvent_ShouldSetEventTimeAutomatically() {
        // Given
        Long reportRunId = 100L;
        Long reportId = 10L;
        String actorUsername = "maker1";
        String actorRole = "MAKER";
        String eventType = "Generated";
        String comment = null;

        // When
        auditService.recordEvent(reportRunId, reportId, actorUsername, actorRole, eventType, comment);

        // Then
        ArgumentCaptor<ReportAuditEvent> captor = ArgumentCaptor.forClass(ReportAuditEvent.class);
        verify(reportAuditEventRepository).save(captor.capture());

        ReportAuditEvent captured = captor.getValue();
        assertThat(captured.getEventTime()).isNotNull();
        // Verify the event time is recent (within last minute)
        assertThat(captured.getEventTime()).isAfter(java.time.LocalDateTime.now().minusMinutes(1));
    }
}
