package com.legacy.report.unit.repository;

import com.legacy.report.model.ReportRun;
import com.legacy.report.repository.ReportRunRepository;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Repository tests for {@link ReportRunRepository} using {@link DataJpaTest}.
 */
@Tag("unit")
@DataJpaTest
@TestPropertySource(locations = "classpath:application-test.properties")
@DisplayName("ReportRunRepository Tests")
class ReportRunRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ReportRunRepository reportRunRepository;

    private ReportRun run1;
    private ReportRun run2;
    private ReportRun run3;
    private ReportRun submittedRun;

    @BeforeEach
    void setUp() {
        // Create runs for maker1
        run1 = createReportRun(1L, "Report 1", "maker1", "Generated", LocalDateTime.now().minusHours(2));
        run2 = createReportRun(1L, "Report 1", "maker1", "Generated", LocalDateTime.now().minusHours(1));
        run3 = createReportRun(2L, "Report 2", "maker1", "Generated", LocalDateTime.now());

        // Create submitted run
        submittedRun = createReportRun(1L, "Report 1", "maker2", "Submitted", LocalDateTime.now());
        submittedRun.setSubmittedAt(LocalDateTime.now());

        // Create approved run for checker
        ReportRun approvedRun = createReportRun(1L, "Report 1", "maker2", "Approved", LocalDateTime.now().minusDays(1));
        approvedRun.setCheckerUsername("checker1");
        approvedRun.setDecidedAt(LocalDateTime.now().minusHours(12));

        entityManager.persist(run1);
        entityManager.persist(run2);
        entityManager.persist(run3);
        entityManager.persist(submittedRun);
        entityManager.persist(approvedRun);
        entityManager.flush();
    }

    private ReportRun createReportRun(Long reportId, String reportName, String makerUsername, 
                                       String status, LocalDateTime generatedAt) {
        ReportRun run = new ReportRun();
        run.setReportId(reportId);
        run.setReportName(reportName);
        run.setStatus(status);
        run.setMakerUsername(makerUsername);
        run.setGeneratedAt(generatedAt);
        return run;
    }

    @Test
    @DisplayName("findByMakerUsernameOrderByGeneratedAtDesc should return runs ordered by generated time")
    void findByMakerUsernameOrderByGeneratedAtDesc_ShouldReturnOrderedRuns() {
        // When
        List<ReportRun> result = reportRunRepository.findByMakerUsernameOrderByGeneratedAtDesc("maker1");

        // Then
        assertThat(result).hasSize(3);
        // Should be ordered from newest to oldest
        assertThat(result.get(0).getGeneratedAt()).isAfterOrEqualTo(result.get(1).getGeneratedAt());
        assertThat(result.get(1).getGeneratedAt()).isAfterOrEqualTo(result.get(2).getGeneratedAt());
    }

    @Test
    @DisplayName("findByMakerUsernameOrderByGeneratedAtDesc should return empty for unknown maker")
    void findByMakerUsernameOrderByGeneratedAtDesc_ShouldReturnEmpty_ForUnknownMaker() {
        // When
        List<ReportRun> result = reportRunRepository.findByMakerUsernameOrderByGeneratedAtDesc("unknown");

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findByMakerUsernameAndReportIdOrderByGeneratedAtDesc should return runs for specific report")
    void findByMakerUsernameAndReportIdOrderByGeneratedAtDesc_ShouldReturnRunsForReport() {
        // When
        List<ReportRun> result = reportRunRepository
                .findByMakerUsernameAndReportIdOrderByGeneratedAtDesc("maker1", 1L);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(ReportRun::getReportName).containsOnly("Report 1");
    }

    @Test
    @DisplayName("findByMakerUsernameAndReportIdOrderByGeneratedAtDesc should be ordered by time")
    void findByMakerUsernameAndReportIdOrderByGeneratedAtDesc_ShouldBeOrdered() {
        // When
        List<ReportRun> result = reportRunRepository
                .findByMakerUsernameAndReportIdOrderByGeneratedAtDesc("maker1", 1L);

        // Then - newest first
        assertThat(result.get(0).getGeneratedAt()).isAfter(result.get(1).getGeneratedAt());
    }

    @Test
    @DisplayName("findByStatusOrderBySubmittedAtAsc should return submitted runs ordered by submit time")
    void findByStatusOrderBySubmittedAtAsc_ShouldReturnSubmittedRuns() {
        // Create another submitted run with later time
        ReportRun laterSubmitted = createReportRun(2L, "Report 2", "maker3", "Submitted", LocalDateTime.now());
        laterSubmitted.setSubmittedAt(LocalDateTime.now().plusHours(1));
        entityManager.persist(laterSubmitted);
        entityManager.flush();

        // When
        List<ReportRun> result = reportRunRepository.findByStatusOrderBySubmittedAtAsc("Submitted");

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getSubmittedAt()).isBeforeOrEqualTo(result.get(1).getSubmittedAt());
    }

    @Test
    @DisplayName("findByCheckerUsernameOrderByDecidedAtDesc should return runs decided by checker")
    void findByCheckerUsernameOrderByDecidedAtDesc_ShouldReturnDecidedRuns() {
        // When
        List<ReportRun> result = reportRunRepository.findByCheckerUsernameOrderByDecidedAtDesc("checker1");

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo("Approved");
        assertThat(result.get(0).getCheckerUsername()).isEqualTo("checker1");
    }

    @Test
    @DisplayName("save should persist new report run")
    void save_ShouldPersistNewReportRun() {
        // Given
        ReportRun newRun = new ReportRun();
        newRun.setReportId(3L);
        newRun.setReportName("New Report");
        newRun.setStatus("Generated");
        newRun.setMakerUsername("newmaker");
        newRun.setGeneratedAt(LocalDateTime.now());

        // When
        ReportRun saved = reportRunRepository.save(newRun);

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getStatus()).isEqualTo("Generated");

        // Verify it can be retrieved
        ReportRun retrieved = entityManager.find(ReportRun.class, saved.getId());
        assertThat(retrieved).isNotNull();
        assertThat(retrieved.getReportName()).isEqualTo("New Report");
    }

    @Test
    @DisplayName("findById should return run when exists")
    void findById_ShouldReturnRun_WhenExists() {
        // When
        Optional<ReportRun> result = reportRunRepository.findById(run1.getId());

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getMakerUsername()).isEqualTo("maker1");
    }

    @Test
    @DisplayName("findById should return empty when run not found")
    void findById_ShouldReturnEmpty_WhenNotFound() {
        // When
        Optional<ReportRun> result = reportRunRepository.findById(999L);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("delete should remove run from database")
    void delete_ShouldRemoveRun() {
        // Given
        Long idToDelete = run1.getId();

        // When
        reportRunRepository.deleteById(idToDelete);

        // Then
        Optional<ReportRun> result = reportRunRepository.findById(idToDelete);
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("update should modify existing run")
    void update_ShouldModifyExistingRun() {
        // Given
        ReportRun existing = reportRunRepository.findById(run1.getId()).orElseThrow();
        existing.setStatus("Submitted");
        existing.setSubmittedAt(LocalDateTime.now());

        // When
        ReportRun updated = reportRunRepository.save(existing);

        // Then
        assertThat(updated.getStatus()).isEqualTo("Submitted");

        // Verify in database
        entityManager.flush();
        entityManager.clear();
        ReportRun retrieved = entityManager.find(ReportRun.class, run1.getId());
        assertThat(retrieved.getStatus()).isEqualTo("Submitted");
    }
}
