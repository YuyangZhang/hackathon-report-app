package com.legacy.report.service;

import com.legacy.report.dao.ReportDao;
import com.legacy.report.model.Report;
import com.legacy.report.service.rules.BuiltInReportRuleCalculator;
import com.legacy.report.service.rules.ReportDataContext;
import com.legacy.report.service.rules.ReportRuleCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportExecutionService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ReportDao reportDao;

    @Autowired
    private List<ReportRuleCalculator> calculators;

    public List<Map<String, Object>> executeReport(Long reportId) {
        Report report = reportDao.findById(reportId);
        if (report == null) {
            throw new RuntimeException("报表不存在");
        }
        return executeReport(report);
    }

    public List<Map<String, Object>> executeReport(Report report) {
        ReportRuleCalculator calculator = findCalculator(report.getName());
        if (calculator == null) {
            return reportDao.executeSql(report.getSql());
        }
        return calculator.calculate(report.getName(), loadContext());
    }

    public List<Map<String, Object>> executeSqlBackedReport(String sql) {
        String reportName = BuiltInReportRuleCalculator.reportNameForSql(sql);
        if (reportName == null) {
            return reportDao.executeSql(sql);
        }

        ReportRuleCalculator calculator = findCalculator(reportName);
        if (calculator == null) {
            return reportDao.executeSql(sql);
        }

        return calculator.calculate(reportName, loadContext());
    }

    public Map<String, Object> generateReport(Report report, String params) {
        List<Map<String, Object>> data;
        if (findCalculator(report.getName()) != null) {
            data = executeReport(report);
        } else {
            String sql = report.getSql();
            if (params != null && !params.isBlank()) {
                sql = sql + " WHERE " + params;
            }
            data = reportDao.executeSql(sql);
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("reportName", report.getName());
        response.put("data", data);
        response.put("count", data.size());
        if (params != null && !params.isBlank()) {
            response.put("params", params);
        }
        return response;
    }

    private ReportRuleCalculator findCalculator(String reportName) {
        if (calculators == null) {
            return null;
        }
        for (ReportRuleCalculator calculator : calculators) {
            if (calculator.supports(reportName)) {
                return calculator;
            }
        }
        return null;
    }

    private ReportDataContext loadContext() {
        return new ReportDataContext(
                jdbcTemplate.queryForList("SELECT id, name, type, status, email, phone, address, registration_date, credit_score, account_balance FROM customer ORDER BY id"),
                jdbcTemplate.queryForList("SELECT id, customer_id, amount, type, status, category, description, transaction_date, reference_number, merchant_id FROM transaction ORDER BY id"),
                jdbcTemplate.queryForList("SELECT id, name, category, status, commission_rate FROM merchant ORDER BY id"),
                jdbcTemplate.queryForList("SELECT id, name, manager, budget, location FROM department ORDER BY id"),
                jdbcTemplate.queryForList("SELECT id, name, email, department_id, position, salary, hire_date, status FROM employee ORDER BY id"),
                jdbcTemplate.queryForList("SELECT id, name, category, price, cost, stock_quantity, supplier_id FROM product ORDER BY id"),
                jdbcTemplate.queryForList("SELECT id, customer_id, order_date, total_amount, status, shipping_address FROM orders ORDER BY id"),
                jdbcTemplate.queryForList("SELECT id, order_id, product_id, quantity, unit_price, total_price FROM order_items ORDER BY id")
        );
    }
}
