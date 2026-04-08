package com.legacy.report.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Service
public class SqlQueryService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Dangerous SQL keywords that should not be allowed
    private static final List<String> DANGEROUS_KEYWORDS = Arrays.asList(
            "DROP", "DELETE", "UPDATE", "INSERT", "ALTER", "CREATE", "TRUNCATE",
            "EXEC", "EXECUTE", "UNION", "--", ";", "/*", "*/"
    );

    private static final Pattern SELECT_ONLY_PATTERN = Pattern.compile(
            "^\\s*SELECT\\s+.*\\s+FROM\\s+.*$",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL
    );

    /**
     * Validates that the SQL is a safe SELECT query only.
     * Business logic: SQL validation in Java layer, not database.
     */
    public void validateSql(String sql) {
        if (sql == null || sql.trim().isEmpty()) {
            throw new IllegalArgumentException("SQL cannot be empty");
        }

        String upperSql = sql.toUpperCase();

        // Check for dangerous keywords
        for (String keyword : DANGEROUS_KEYWORDS) {
            if (upperSql.contains(keyword)) {
                throw new SecurityException("SQL contains dangerous keyword: " + keyword);
            }
        }

        // Ensure it's a SELECT query
        if (!SELECT_ONLY_PATTERN.matcher(sql.trim()).matches()) {
            throw new SecurityException("Only SELECT queries are allowed");
        }
    }

    /**
     * Executes a validated SQL query safely.
     * Business logic: Safe execution with parameter validation in Java.
     */
    public List<Map<String, Object>> executeSafeQuery(String sql, String params) {
        validateSql(sql);

        String finalSql = sql;

        // If params are provided, safely append them with validation
        if (params != null && !params.trim().isEmpty()) {
            validateParams(params);
            // Only allow simple WHERE clause additions
            if (!params.toUpperCase().contains("OR") && !params.toUpperCase().contains("UNION")) {
                finalSql = sql + " WHERE " + params;
            } else {
                throw new SecurityException("Invalid parameters detected");
            }
        }

        return jdbcTemplate.queryForList(finalSql);
    }

    /**
     * Execute SQL by report ID - delegates to executeSafeQuery.
     * This method provides backward compatibility for other services.
     */
    public List<Map<String, Object>> runReport(String sql) {
        return executeSafeQuery(sql, null);
    }

    /**
     * Validates that params don't contain dangerous keywords.
     * Business logic: Parameter validation in Java.
     */
    private void validateParams(String params) {
        if (params == null || params.trim().isEmpty()) {
            return;
        }

        String upperParams = params.toUpperCase();
        for (String keyword : DANGEROUS_KEYWORDS) {
            if (upperParams.contains(keyword)) {
                throw new SecurityException("Parameters contain dangerous keyword: " + keyword);
            }
        }
    }
}
