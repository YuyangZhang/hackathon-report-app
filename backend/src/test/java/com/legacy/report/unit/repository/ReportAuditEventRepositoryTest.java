package com.legacy.report.unit.repository;

import com.legacy.report.model.ReportAuditEvent;
import com.legacy.report.repository.ReportAuditEventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Repository tests for {@link ReportAuditEventRepository} using {@link DataJpaTest}.
 */
@Tag("unit")
@DataJpaTest
@TestPropertySource(locations = "classpath:application-test.properties")
@DisplayName("ReportAuditEventRepository Tests")
class ReportAuditEventRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ReportAuditEventRepository reportAuditEventRepository;

    private Long reportRunId1;
    private Long reportRunId2;

    @BeforeEach
    void setUp() {
        reportRunId1 = 100L;
        reportRunId2 = 200L;

        // Create audit events for run 1
        ReportAuditEvent event1 = createAuditEvent(reportRunId1, 10L, "maker1", "MAKER", "Generated", 
                LocalDateTime.now().minusHours(2), null);
        ReportAuditEvent event2 = createAuditEvent(reportRunId1, 10L, "maker1", "MAKER", "Submitted", 
                LocalDateTime.now().minusHours(1), null);
        ReportAuditEvent event3 = createAuditEvent(reportRunId1, 10L, "checker1", "CHECKER", "Approved", 
                LocalDateTime.now(), "Approved after review");

        // Create audit events for run 2
        ReportAuditEvent event4 = createAuditEvent(reportRunId2, 20L, "maker2", "MAKER", "Generated", 
                LocalDateTime.now().minusMinutes(30), null);

        entityManager.persist(event1);
        entityManager.persist(event2);
        entityManager.persist(event3);
        entityManager.persist(event4);
        entityManager.flush();
    }

    private ReportAuditEvent createAuditEvent(Long reportRunId, Long reportId, String actorUsername, 
                                               String actorRole, String eventType, 
                                               LocalDateTime eventTime, String comment) {
        ReportAuditEvent event = new ReportAuditEvent();
        event.setReportRunId(reportRunId);
        event.setReportId(reportId);
        event.setActorUsername(actorUsername);
        event.setActorRole(actorRole);
        event.setEventType(eventType);
        event.setEventTime(eventTime);
        event.setComment(comment);
        return event;
    }

    @Test
    @DisplayName("findByReportRunIdOrderByEventTimeAsc should return events ordered by time")
    void findByReportRunIdOrderByEventTimeAsc_ShouldReturnOrderedEvents() {
        // When
        List<ReportAuditEvent> result = reportAuditEventRepository
                .findByReportRunIdOrderByEventTimeAsc(reportRunId1);

        // Then
        assertThat(result).hasSize(3);
        // Should be ordered from oldest to newest (ascending)
        assertThat(result.get(0).getEventTime()).isBeforeOrEqualTo(result.get(1).getEventTime());
        assertThat(result.get(1).getEventTime()).isBeforeOrEqualTo(result.get(2).getEventTime());
    }

    @Test
    @DisplayName("findByReportRunIdOrderByEventTimeAsc should show correct event sequence")
    void findByReportRunIdOrderByEventTimeAsc_ShouldShowCorrectSequence() {
        // When
        List<ReportAuditEvent> result = reportAuditEventRepository
                .findByReportRunIdOrderByEventTimeAsc(reportRunId1);

        // Then
        assertThat(result).extracting(ReportAuditEvent::getEventType)
                .containsExactly("Generated", "Submitted", "Approved");
    }

    @Test
    @DisplayName("findByReportRunIdOrderByEventTimeAsc should return empty for unknown run")
    void findByReportRunIdOrderByEventTimeAsc_ShouldReturnEmpty_ForUnknownRun() {
        // When
        List<ReportAuditEvent> result = reportAuditEventRepository
                .findByReportRunIdOrderByEventTimeAsc(999L);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("save should persist new audit event")
    void save_ShouldPersistNewAuditEvent() {
        // Given
        ReportAuditEvent newEvent = new ReportAuditEvent();
        newEvent.setReportRunId(300L);
        newEvent.setReportId(30L);
        newEvent.setActorUsername("maker3");
        newEvent.setActorRole("MAKER");
        newEvent.setEventType("Generated");
        newEvent.setEventTime(LocalDateTime.now());
        newEvent.setComment(null);

        // When
        ReportAuditEvent saved = reportAuditEventRepository.save(newEvent);

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getEventType()).isEqualTo("Generated");

        // Verify it can be retrieved
        ReportAuditEvent retrieved = entityManager.find(ReportAuditEvent.class, saved.getId());
        assertThat(retrieved).isNotNull();
        assertThat(retrieved.getActorUsername()).isEqualTo("maker3");
    }

    @Test
    @DisplayName("save should persist event with comment")
    void save_ShouldPersistEventWithComment() {
        // Given
        ReportAuditEvent newEvent = new ReportAuditEvent();
        newEvent.setReportRunId(300L);
        newEvent.setReportId(30L);
        newEvent.setActorUsername("checker2");
        newEvent.setActorRole("CHECKER");
        newEvent.setEventType("Rejected");
        newEvent.setEventTime(LocalDateTime.now());
        newEvent.setComment("Data validation failed in row 10");

        // When
        ReportAuditEvent saved = reportAuditEventRepository.save(newEvent);

        // Then
        assertThat(saved.getComment()).isEqualTo("Data validation failed in row 10");
    }

    @Test
    @DisplayName("findByReportRunIdOrderByEventTimeAsc should handle multiple runs independently")
    void findByReportRunIdOrderByEventTimeAsc_ShouldHandleMultipleRuns() {
        // When
        List<ReportAuditEvent> run1Events = reportAuditEventRepository
                .findByReportRunIdOrderByEventTimeAsc(reportRunId1);
        List<ReportAuditEvent> run2Events = reportAuditEventRepository
                .findByReportRunIdOrderByEventTimeAsc(reportRunId2);

        // Then
        assertThat(run1Events).hasSize(3);
        assertThat(run2Events).hasSize(1);
        assertThat(run2Events.get(0).getEventType()).isEqualTo("Generated");
    }

    @Test
    @DisplayName("event should store all fields correctly")
    void event_ShouldStoreAllFields() {
        // Given - Get the first event from the database
        List<ReportAuditEvent> events = reportAuditEventRepository
                .findByReportRunIdOrderByEventTimeAsc(reportRunId1);

        // Then
        ReportAuditEvent event = events.get(0);
        assertThat(event.getReportRunId()).isEqualTo(reportRunId1);
        assertThat(event.getReportId()).isEqualTo(10L);
        assertThat(event.getActorUsername()).isEqualTo("maker1");
        assertThat(event.getActorRole()).isEqualTo("MAKER");
        assertThat(event.getEventType()).isEqualTo("Generated");
        assertThat(event.getEventTime()).isNotNull();
    }
}
