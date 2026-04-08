package com.legacy.report.unit.service;

import com.legacy.report.service.SqlQueryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link SqlQueryService}.
 * Tests SQL validation and safe query execution.
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("SqlQueryService Unit Tests")
class SqlQueryServiceTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private SqlQueryService sqlQueryService;


    @Test
    @DisplayName("validateSql should accept valid SELECT query")
    void validateSql_ShouldAcceptValidSelectQuery() {
        // Given
        String validSql = "SELECT * FROM users";

        // When/Then - should not throw
        sqlQueryService.validateSql(validSql);
    }

    @Test
    @DisplayName("validateSql should accept SELECT with specific columns")
    void validateSql_ShouldAcceptSelectWithSpecificColumns() {
        // Given
        String validSql = "SELECT id, name, email FROM customers WHERE status = 'active'";

        // When/Then - should not throw
        sqlQueryService.validateSql(validSql);
    }

    @Test
    @DisplayName("validateSql should accept SELECT with JOIN")
    void validateSql_ShouldAcceptSelectWithJoin() {
        // Given
        String validSql = "SELECT u.id, u.name, o.order_date FROM users u JOIN orders o ON u.id = o.user_id";

        // When/Then - should not throw
        sqlQueryService.validateSql(validSql);
    }

    @Test
    @DisplayName("validateSql should reject empty SQL")
    void validateSql_ShouldRejectEmptySql() {
        // Given
        String emptySql = "";

        // When/Then
        assertThatThrownBy(() -> sqlQueryService.validateSql(emptySql))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("SQL cannot be empty");
    }

    @Test
    @DisplayName("validateSql should reject null SQL")
    void validateSql_ShouldRejectNullSql() {
        // When/Then
        assertThatThrownBy(() -> sqlQueryService.validateSql(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("SQL cannot be empty");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "DROP TABLE users",
            "DELETE FROM users WHERE id = 1",
            "UPDATE users SET name = 'test'",
            "INSERT INTO users VALUES (1, 'test')",
            "ALTER TABLE users ADD COLUMN age INT",
            "CREATE TABLE test (id INT)",
            "TRUNCATE TABLE users",
            "EXEC sp_dropuser 'test'",
            "EXECUTE sp_dropuser 'test'"
    })
    @DisplayName("validateSql should reject dangerous keywords")
    void validateSql_ShouldRejectDangerousKeywords(String dangerousSql) {
        // When/Then
        assertThatThrownBy(() -> sqlQueryService.validateSql(dangerousSql))
                .isInstanceOf(SecurityException.class)
                .hasMessageContaining("dangerous keyword");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "SELECT * FROM users; DROP TABLE users",
            "SELECT * FROM users -- comment",
            "SELECT * FROM users /* comment */",
            "SELECT * FROM users UNION SELECT * FROM passwords"
    })
    @DisplayName("validateSql should reject SQL injection patterns")
    void validateSql_ShouldRejectInjectionPatterns(String injectionSql) {
        // When/Then
        assertThatThrownBy(() -> sqlQueryService.validateSql(injectionSql))
                .isInstanceOf(SecurityException.class);
    }

    @Test
    @DisplayName("validateSql should reject non-SELECT queries")
    void validateSql_ShouldRejectNonSelectQueries() {
        // Given
        String nonSelectSql = "SHOW TABLES";

        // When/Then
        assertThatThrownBy(() -> sqlQueryService.validateSql(nonSelectSql))
                .isInstanceOf(SecurityException.class)
                .hasMessageContaining("Only SELECT queries are allowed");
    }

    @Test
    @DisplayName("executeSafeQuery should execute valid SQL")
    void executeSafeQuery_ShouldExecuteValidSql() {
        // Given
        String validSql = "SELECT * FROM users";
        List<Map<String, Object>> expectedData = List.of(
                Map.of("id", 1, "name", "Alice"),
                Map.of("id", 2, "name", "Bob")
        );
        when(jdbcTemplate.queryForList(validSql)).thenReturn(expectedData);

        // When
        List<Map<String, Object>> result = sqlQueryService.executeSafeQuery(validSql, null);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0)).containsEntry("name", "Alice");
    }

    @Test
    @DisplayName("executeSafeQuery should append safe parameters")
    void executeSafeQuery_ShouldAppendSafeParameters() {
        // Given
        String sql = "SELECT * FROM users";
        String params = "status = 'active'";
        String expectedFinalSql = "SELECT * FROM users WHERE status = 'active'";

        List<Map<String, Object>> expectedData = List.of(Map.of("id", 1));
        when(jdbcTemplate.queryForList(expectedFinalSql)).thenReturn(expectedData);

        // When
        List<Map<String, Object>> result = sqlQueryService.executeSafeQuery(sql, params);

        // Then
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("executeSafeQuery should reject dangerous parameters")
    void executeSafeQuery_ShouldRejectDangerousParameters() {
        // Given
        String sql = "SELECT * FROM users";
        String dangerousParams = "1=1 OR 1=1"; // OR-based injection

        // When/Then
        assertThatThrownBy(() -> sqlQueryService.executeSafeQuery(sql, dangerousParams))
                .isInstanceOf(SecurityException.class)
                .hasMessageContaining("Invalid parameters detected");
    }

    @Test
    @DisplayName("executeSafeQuery should reject UNION in parameters")
    void executeSafeQuery_ShouldRejectUnionInParameters() {
        // Given
        String sql = "SELECT * FROM users";
        String unionParams = "1=1 UNION SELECT * FROM passwords";

        // When/Then
        assertThatThrownBy(() -> sqlQueryService.executeSafeQuery(sql, unionParams))
                .isInstanceOf(SecurityException.class)
                .hasMessageContaining("UNION");
    }

    @Test
    @DisplayName("runReport should delegate to executeSafeQuery")
    void runReport_ShouldDelegateToExecuteSafeQuery() {
        // Given
        String sql = "SELECT * FROM reports";
        List<Map<String, Object>> expectedData = List.of(Map.of("id", 1, "name", "Report 1"));
        when(jdbcTemplate.queryForList(sql)).thenReturn(expectedData);

        // When
        List<Map<String, Object>> result = sqlQueryService.runReport(sql);

        // Then
        assertThat(result).isEqualTo(expectedData);
    }

    @Test
    @DisplayName("executeSafeQuery should handle empty result")
    void executeSafeQuery_ShouldHandleEmptyResult() {
        // Given
        String sql = "SELECT * FROM empty_table";
        when(jdbcTemplate.queryForList(sql)).thenReturn(List.of());

        // When
        List<Map<String, Object>> result = sqlQueryService.executeSafeQuery(sql, null);

        // Then
        assertThat(result).isEmpty();
    }
}
