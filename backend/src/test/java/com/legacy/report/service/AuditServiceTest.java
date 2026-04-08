package com.legacy.report.service;

import com.legacy.report.model.ReportAuditEvent;
import com.legacy.report.repository.ReportAuditEventRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuditService 单元测试")
class AuditServiceTest {

    @Mock
    private ReportAuditEventRepository reportAuditEventRepository;

    @InjectMocks
    private AuditService auditService;

    @BeforeEach
    void setUp() {
        // 测试前初始化
    }

    @AfterEach
    void tearDown() {
        // 测试后清理
    }

    @Test
    @DisplayName("应该成功记录审计事件")
    void should_recordEvent_successfully_when_all_parameters_valid() {
        // Arrange
        Long reportRunId = 1L;
        Long reportId = 2L;
        String actorUsername = "testUser";
        String actorRole = "MAKER";
        String eventType = "Generated";
        String comment = "测试评论";

        // Act
        auditService.recordEvent(reportRunId, reportId, actorUsername, actorRole, eventType, comment);

        // Assert
        verify(reportAuditEventRepository, times(1)).save(any(ReportAuditEvent.class));
    }

    @Test
    @DisplayName("应该成功记录审计事件当评论为空时")
    void should_recordEvent_successfully_when_comment_is_null() {
        // Arrange
        Long reportRunId = 1L;
        Long reportId = 2L;
        String actorUsername = "testUser";
        String actorRole = "MAKER";
        String eventType = "Generated";
        String comment = null;

        // Act
        auditService.recordEvent(reportRunId, reportId, actorUsername, actorRole, eventType, comment);

        // Assert
        verify(reportAuditEventRepository, times(1)).save(any(ReportAuditEvent.class));
    }

    @Test
    @DisplayName("应该成功记录审计事件当评论为空字符串时")
    void should_recordEvent_successfully_when_comment_is_empty() {
        // Arrange
        Long reportRunId = 1L;
        Long reportId = 2L;
        String actorUsername = "testUser";
        String actorRole = "MAKER";
        String eventType = "Generated";
        String comment = "";

        // Act
        auditService.recordEvent(reportRunId, reportId, actorUsername, actorRole, eventType, comment);

        // Assert
        verify(reportAuditEventRepository, times(1)).save(any(ReportAuditEvent.class));
    }

    @Test
    @DisplayName("应该成功记录审计事件当评论只有空格时")
    void should_recordEvent_successfully_when_comment_is_whitespace() {
        // Arrange
        Long reportRunId = 1L;
        Long reportId = 2L;
        String actorUsername = "testUser";
        String actorRole = "MAKER";
        String eventType = "Generated";
        String comment = "   ";

        // Act
        auditService.recordEvent(reportRunId, reportId, actorUsername, actorRole, eventType, comment);

        // Assert
        verify(reportAuditEventRepository, times(1)).save(any(ReportAuditEvent.class));
    }

    @Test
    @DisplayName("应该成功记录审计事件当所有参数为极值时")
    void should_recordEvent_successfully_when_parameters_are_boundary_values() {
        // Arrange
        Long reportRunId = Long.MAX_VALUE;
        Long reportId = Long.MIN_VALUE;
        String actorUsername = "a";
        String actorRole = "b";
        String eventType = "c";
        String comment = "x".repeat(1000);

        // Act
        auditService.recordEvent(reportRunId, reportId, actorUsername, actorRole, eventType, comment);

        // Assert
        verify(reportAuditEventRepository, times(1)).save(any(ReportAuditEvent.class));
    }

    @Test
    @DisplayName("应该正确设置审计事件的所有字段")
    void should_set_all_fields_correctly_when_recording_event() {
        // Arrange
        Long reportRunId = 1L;
        Long reportId = 2L;
        String actorUsername = "testUser";
        String actorRole = "MAKER";
        String eventType = "Generated";
        String comment = "测试评论";

        // Act
        auditService.recordEvent(reportRunId, reportId, actorUsername, actorRole, eventType, comment);

        // Assert
        verify(reportAuditEventRepository, times(1)).save(argThat(event -> {
            assertThat(event.getReportRunId()).isEqualTo(reportRunId);
            assertThat(event.getReportId()).isEqualTo(reportId);
            assertThat(event.getActorUsername()).isEqualTo(actorUsername);
            assertThat(event.getActorRole()).isEqualTo(actorRole);
            assertThat(event.getEventType()).isEqualTo(eventType);
            assertThat(event.getComment()).isEqualTo(comment);
            assertThat(event.getEventTime()).isNotNull();
            return true;
        }));
    }
}
