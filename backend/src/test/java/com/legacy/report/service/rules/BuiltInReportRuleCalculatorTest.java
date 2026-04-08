package com.legacy.report.service.rules;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BuiltInReportRuleCalculatorTest {

    private final BuiltInReportRuleCalculator calculator = new BuiltInReportRuleCalculator();

    @Test
    void reportNameForSqlReturnsBuiltInNameForKnownSql() {
        String reportName = BuiltInReportRuleCalculator.reportNameForSql(
                BuiltInReportRuleCalculator.CUSTOMER_TRANSACTION_ANALYSIS_SQL);

        assertEquals(BuiltInReportRuleCalculator.CUSTOMER_TRANSACTION_ANALYSIS, reportName);
    }

    @Test
    void customerTransactionAnalysisMatchesExpectedAggregates() {
        ReportDataContext context = new ReportDataContext(
                List.of(
                        row("id", 1L, "name", "Customer A", "type", "VIP", "credit_score", 750L),
                        row("id", 2L, "name", "Customer B", "type", "NORMAL", "credit_score", 680L)
                ),
                List.of(
                        row("id", 1L, "customer_id", 1L, "amount", decimal("100.00"), "status", "SUCCESS", "type", "INCOME"),
                        row("id", 2L, "customer_id", 1L, "amount", decimal("50.00"), "status", "SUCCESS", "type", "EXPENSE"),
                        row("id", 3L, "customer_id", 2L, "amount", decimal("30.00"), "status", "PENDING", "type", "INCOME")
                ),
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                List.of()
        );

        List<Map<String, Object>> rows = calculator.calculate(
                BuiltInReportRuleCalculator.CUSTOMER_TRANSACTION_ANALYSIS,
                context);

        assertEquals(1, rows.size());
        assertEquals("Customer A", rows.get(0).get("name"));
        assertEquals(2L, ReportRuleSupport.longValue(rows.get(0), "tx_count"));
        assertEquals(0, new BigDecimal("150.00").compareTo(ReportRuleSupport.decimalValue(rows.get(0), "total_amount")));
        assertEquals(0, new BigDecimal("75.00").compareTo(ReportRuleSupport.decimalValue(rows.get(0), "avg_transaction")));
    }

    @Test
    void financialHealthScorecardReturnsExpectedMetrics() {
        ReportDataContext context = new ReportDataContext(
                List.of(),
                List.of(
                        row("id", 1L, "customer_id", 1L, "amount", decimal("100.00"), "status", "SUCCESS", "type", "INCOME"),
                        row("id", 2L, "customer_id", 1L, "amount", decimal("25.00"), "status", "SUCCESS", "type", "EXPENSE"),
                        row("id", 3L, "customer_id", 2L, "amount", decimal("50.00"), "status", "SUCCESS", "type", "INCOME")
                ),
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                List.of()
        );

        List<Map<String, Object>> rows = calculator.calculate(
                BuiltInReportRuleCalculator.FINANCIAL_HEALTH_SCORECARD,
                context);

        assertEquals(5, rows.size());
        assertMetric(rows, "Total Revenue", "150.00");
        assertMetric(rows, "Total Expenses", "25.00");
        assertMetric(rows, "Net Profit", "125.00");
        assertTrue(rows.stream().anyMatch(row -> "Active Customers".equals(row.get("metric")) && Integer.valueOf(2).equals(row.get("value"))));
        assertMetric(rows, "Average Transaction Value", "58.33");
    }

    private static void assertMetric(List<Map<String, Object>> rows, String metric, String expected) {
        Map<String, Object> row = rows.stream()
                .filter(candidate -> metric.equals(candidate.get("metric")))
                .findFirst()
                .orElseThrow();

        assertEquals(0, new BigDecimal(expected).compareTo(ReportRuleSupport.decimalValue(row, "value")));
    }

    private static Map<String, Object> row(Object... values) {
        return ReportRuleSupport.row(values);
    }

    private static BigDecimal decimal(String value) {
        return new BigDecimal(value);
    }
}
