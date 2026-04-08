package com.legacy.report.unit.exception;

import com.legacy.report.exception.ReportExportException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link ReportExportException}.
 */
@Tag("unit")
@DisplayName("ReportExportException Unit Tests")
class ReportExportExceptionTest {

    @Test
    @DisplayName("Constructor with message should set message correctly")
    void constructorWithMessage_ShouldSetMessage() {
        // Given
        String message = "Failed to export report to Excel";

        // When
        ReportExportException exception = new ReportExportException(message);

        // Then
        assertThat(exception.getMessage()).isEqualTo(message);
    }

    @Test
    @DisplayName("Constructor with message and cause should set both")
    void constructorWithMessageAndCause_ShouldSetBoth() {
        // Given
        String message = "Export failed due to IO error";
        Throwable cause = new java.io.IOException("Disk full");

        // When
        ReportExportException exception = new ReportExportException(message, cause);

        // Then
        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getCause()).isEqualTo(cause);
    }

    @Test
    @DisplayName("Exception should be instance of RuntimeException")
    void exception_ShouldBeRuntimeException() {
        // Given
        ReportExportException exception = new ReportExportException("Test");

        // Then
        assertThat(exception).isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("Exception should preserve nested cause message")
    void exception_ShouldPreserveNestedCauseMessage() {
        // Given
        String innerMessage = "Null pointer occurred";
        NullPointerException innerException = new NullPointerException(innerMessage);

        // When
        ReportExportException exception = new ReportExportException("Export failed", innerException);

        // Then
        assertThat(exception.getCause()).isInstanceOf(NullPointerException.class);
        assertThat(exception.getCause().getMessage()).isEqualTo(innerMessage);
    }

    @Test
    @DisplayName("Exception message can be empty string")
    void exceptionMessage_CanBeEmpty() {
        // When
        ReportExportException exception = new ReportExportException("");

        // Then
        assertThat(exception.getMessage()).isEmpty();
    }

    @Test
    @DisplayName("Exception can have null cause")
    void exception_CanHaveNullCause() {
        // When
        ReportExportException exception = new ReportExportException("Message", null);

        // Then
        assertThat(exception.getCause()).isNull();
    }

    @Test
    @DisplayName("Exception stack trace should be accessible")
    void exceptionStackTrace_ShouldBeAccessible() {
        // Given
        ReportExportException exception = new ReportExportException("Test message");

        // Then
        assertThat(exception.getStackTrace()).isNotNull();
        assertThat(exception.getStackTrace()).isNotEmpty();
    }

    @Test
    @DisplayName("Exception should be throwable")
    void exception_ShouldBeThrowable() {
        // Given
        ReportExportException exception = new ReportExportException("Test");

        // Then
        assertThat(exception).isInstanceOf(Throwable.class);
    }
}
