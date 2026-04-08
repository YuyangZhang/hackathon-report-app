package com.legacy.report.unit.repository;

import com.legacy.report.model.Report;
import com.legacy.report.repository.ReportRepository;
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
 * Repository tests for {@link ReportRepository} using {@link DataJpaTest}.
 * Tests CRUD operations and custom queries.
 */
@Tag("unit")
@DataJpaTest
@TestPropertySource(locations = "classpath:application-test.properties")
@DisplayName("ReportRepository Tests")
class ReportRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ReportRepository reportRepository;

    private Report activeReport1;
    private Report activeReport2;
    private Report deletedReport;

    @BeforeEach
    void setUp() {
        activeReport1 = createReport("Active Report 1", "SELECT * FROM users", 0);
        activeReport2 = createReport("Active Report 2", "SELECT * FROM orders", 0);
        deletedReport = createReport("Deleted Report", "SELECT * FROM old_table", 1);

        entityManager.persist(activeReport1);
        entityManager.persist(activeReport2);
        entityManager.persist(deletedReport);
        entityManager.flush();
    }

    private Report createReport(String name, String sql, int isDeleted) {
        Report report = new Report();
        report.setName(name);
        report.setSql(sql);
        report.setDescription("Test description for " + name);
        report.setIsDeleted(isDeleted);
        report.setCreatedAt(LocalDateTime.now());
        report.setUpdatedAt(LocalDateTime.now());
        return report;
    }

    @Test
    @DisplayName("findByIsDeleted should return only non-deleted reports")
    void findByIsDeleted_ShouldReturnOnlyNonDeletedReports() {
        // When
        List<Report> result = reportRepository.findByIsDeleted(0);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(Report::getName)
                .containsExactlyInAnyOrder("Active Report 1", "Active Report 2");
    }

    @Test
    @DisplayName("findByIsDeleted should return only deleted reports when isDeleted=1")
    void findByIsDeleted_ShouldReturnOnlyDeletedReports() {
        // When
        List<Report> result = reportRepository.findByIsDeleted(1);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Deleted Report");
    }

    @Test
    @DisplayName("findByIdAndIsDeleted should return report when found and not deleted")
    void findByIdAndIsDeleted_ShouldReturnReportWhenFound() {
        // When
        Optional<Report> result = reportRepository.findByIdAndIsDeleted(activeReport1.getId(), 0);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Active Report 1");
    }

    @Test
    @DisplayName("findByIdAndIsDeleted should return empty when report is deleted")
    void findByIdAndIsDeleted_ShouldReturnEmptyWhenDeleted() {
        // When
        Optional<Report> result = reportRepository.findByIdAndIsDeleted(deletedReport.getId(), 0);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findByIdAndIsDeleted should return empty when report not found")
    void findByIdAndIsDeleted_ShouldReturnEmptyWhenNotFound() {
        // When
        Optional<Report> result = reportRepository.findByIdAndIsDeleted(999L, 0);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("save should persist new report")
    void save_ShouldPersistNewReport() {
        // Given
        Report newReport = new Report();
        newReport.setName("New Report");
        newReport.setSql("SELECT * FROM new_table");
        newReport.setDescription("New description");
        newReport.setIsDeleted(0);

        // When
        Report saved = reportRepository.save(newReport);

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("New Report");

        // Verify it can be retrieved
        Report retrieved = entityManager.find(Report.class, saved.getId());
        assertThat(retrieved).isNotNull();
        assertThat(retrieved.getName()).isEqualTo("New Report");
    }

    @Test
    @DisplayName("save should update existing report")
    void save_ShouldUpdateExistingReport() {
        // Given
        Report existing = reportRepository.findById(activeReport1.getId()).orElseThrow();
        existing.setName("Updated Name");

        // When
        Report updated = reportRepository.save(existing);

        // Then
        assertThat(updated.getName()).isEqualTo("Updated Name");

        // Verify in database
        entityManager.flush();
        entityManager.clear();
        Report retrieved = entityManager.find(Report.class, activeReport1.getId());
        assertThat(retrieved.getName()).isEqualTo("Updated Name");
    }

    @Test
    @DisplayName("delete should remove report from database")
    void delete_ShouldRemoveReport() {
        // Given
        Long idToDelete = activeReport1.getId();

        // When
        reportRepository.deleteById(idToDelete);

        // Then
        Optional<Report> result = reportRepository.findById(idToDelete);
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findAll should return all reports")
    void findAll_ShouldReturnAllReports() {
        // When
        List<Report> result = reportRepository.findAll();

        // Then
        assertThat(result).hasSize(3);
    }

    @Test
    @DisplayName("existsById should return true for existing report")
    void existsById_ShouldReturnTrueForExisting() {
        // When
        boolean exists = reportRepository.existsById(activeReport1.getId());

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("existsById should return false for non-existing report")
    void existsById_ShouldReturnFalseForNonExisting() {
        // When
        boolean exists = reportRepository.existsById(999L);

        // Then
        assertThat(exists).isFalse();
    }
}
