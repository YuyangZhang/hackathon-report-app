package com.legacy.report.util;

import com.legacy.report.model.*;
import com.legacy.report.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Utility class for building test data.
 * Provides methods to create and persist test entities.
 */
@Component
public class TestDataBuilder {

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReportRunRepository reportRunRepository;

    @Autowired
    private ReportAuditEventRepository auditEventRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Create a test report with default values.
     */
    public Report createReport(String name, String sql) {
        Report report = new Report();
        report.setName(name != null ? name : "Test Report");
        report.setSql(sql != null ? sql : "SELECT * FROM test_table");
        report.setDescription("Test report description");
        report.setIsDeleted(0);
        return reportRepository.save(report);
    }

    /**
     * Create a test report with minimal defaults.
     */
    public Report createReport() {
        return createReport("Test Report", "SELECT 1 as id, 'test' as name");
    }

    /**
     * Create a test user with specified role.
     */
    public User createUser(String username, String password, String role) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);
        return userRepository.save(user);
    }

    /**
     * Create a maker user.
     */
    public User createMaker(String username, String password) {
        return createUser(username, password, "MAKER");
    }

    /**
     * Create a checker user.
     */
    public User createChecker(String username, String password) {
        return createUser(username, password, "CHECKER");
    }

    /**
     * Create a user with both MAKER and CHECKER roles.
     */
    public User createAdminUser(String username, String password) {
        return createUser(username, password, "MAKER,CHECKER");
    }

    /**
     * Create a report run with Generated status.
     */
    public ReportRun createReportRun(Long reportId, String reportName, String makerUsername) {
        ReportRun run = new ReportRun();
        run.setReportId(reportId);
        run.setReportName(reportName);
        run.setStatus("Generated");
        run.setMakerUsername(makerUsername);
        run.setGeneratedAt(LocalDateTime.now());
        return reportRunRepository.save(run);
    }

    /**
     * Create a submitted report run.
     */
    public ReportRun createSubmittedReportRun(Long reportId, String reportName, String makerUsername) {
        ReportRun run = createReportRun(reportId, reportName, makerUsername);
        run.setStatus("Submitted");
        run.setSubmittedAt(LocalDateTime.now());
        return reportRunRepository.save(run);
    }

    /**
     * Create an approved report run.
     */
    public ReportRun createApprovedReportRun(Long reportId, String reportName, 
                                             String makerUsername, String checkerUsername) {
        ReportRun run = createSubmittedReportRun(reportId, reportName, makerUsername);
        run.setStatus("Approved");
        run.setCheckerUsername(checkerUsername);
        run.setDecidedAt(LocalDateTime.now());
        return reportRunRepository.save(run);
    }

    /**
     * Create an audit event for a report run.
     */
    public ReportAuditEvent createAuditEvent(Long reportRunId, Long reportId,
                                              String actorUsername, String actorRole,
                                              String eventType, String comment) {
        ReportAuditEvent event = new ReportAuditEvent();
        event.setReportRunId(reportRunId);
        event.setReportId(reportId);
        event.setActorUsername(actorUsername);
        event.setActorRole(actorRole);
        event.setEventType(eventType);
        event.setEventTime(LocalDateTime.now());
        event.setComment(comment);
        return auditEventRepository.save(event);
    }

    /**
     * Clean up all test data.
     */
    public void cleanAll() {
        auditEventRepository.deleteAll();
        reportRunRepository.deleteAll();
        reportRepository.deleteAll();
        userRepository.deleteAll();
    }
}
