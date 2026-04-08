package com.legacy.report.service.rules;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@Service
public class BuiltInReportRuleCalculator implements ReportRuleCalculator {

    public static final String CUSTOMER_TRANSACTION_ANALYSIS = "Customer Transaction Analysis";
    public static final String VIP_CUSTOMER_REVENUE_REPORT = "VIP Customer Revenue Report";
    public static final String MERCHANT_PERFORMANCE_ANALYSIS = "Merchant Performance Analysis";
    public static final String DEPARTMENT_BUDGET_ANALYSIS = "Department Budget Analysis";
    public static final String PRODUCT_PROFITABILITY_REPORT = "Product Profitability Report";
    public static final String CUSTOMER_SEGMENTATION_ANALYSIS = "Customer Segmentation Analysis";
    public static final String MONTHLY_REVENUE_TREND_ANALYSIS = "Monthly Revenue Trend Analysis";
    public static final String ORDER_FULFILLMENT_ANALYSIS = "Order Fulfillment Analysis";
    public static final String EMPLOYEE_PERFORMANCE_METRICS = "Employee Performance Metrics";
    public static final String CUSTOMER_MERCHANT_REVENUE_MATRIX = "Customer-Merchant Revenue Matrix";
    public static final String INVENTORY_VELOCITY_ANALYSIS = "Inventory Velocity Analysis";
    public static final String FINANCIAL_HEALTH_SCORECARD = "Financial Health Scorecard";

    public static final String CUSTOMER_TRANSACTION_ANALYSIS_SQL =
            "SELECT c.name, c.type, c.credit_score, SUM(t.amount) as total_amount, COUNT(t.id) as tx_count, AVG(t.amount) as avg_transaction FROM customer c LEFT JOIN transaction t ON c.id = t.customer_id WHERE t.status = 'SUCCESS' GROUP BY c.id, c.name, c.type, c.credit_score ORDER BY total_amount DESC";
    public static final String VIP_CUSTOMER_REVENUE_REPORT_SQL =
            "SELECT c.name, c.email, c.account_balance, SUM(CASE WHEN t.type = 'INCOME' THEN t.amount ELSE 0 END) as income, SUM(CASE WHEN t.type = 'EXPENSE' THEN t.amount ELSE 0 END) as expense, (SUM(CASE WHEN t.type = 'INCOME' THEN t.amount ELSE 0 END) - SUM(CASE WHEN t.type = 'EXPENSE' THEN t.amount ELSE 0 END)) as net_profit FROM customer c LEFT JOIN transaction t ON c.id = t.customer_id WHERE c.type = 'VIP' GROUP BY c.id, c.name, c.email, c.account_balance ORDER BY net_profit DESC";
    public static final String MERCHANT_PERFORMANCE_ANALYSIS_SQL =
            "SELECT m.name as merchant_name, m.category, COUNT(t.id) as transaction_count, SUM(t.amount) as total_volume, AVG(t.amount) as avg_transaction_amount, (SUM(t.amount) * m.commission_rate) as estimated_commission FROM merchant m LEFT JOIN transaction t ON m.id = t.merchant_id WHERE t.status = 'SUCCESS' GROUP BY m.id, m.name, m.category, m.commission_rate ORDER BY total_volume DESC";
    public static final String DEPARTMENT_BUDGET_ANALYSIS_SQL =
            "SELECT d.name as department, d.manager, d.budget, d.location, COUNT(e.id) as employee_count, SUM(e.salary) as total_salary_cost, (d.budget - SUM(e.salary)) as budget_variance, ROUND((SUM(e.salary) / d.budget) * 100, 2) as budget_utilization_percent FROM department d LEFT JOIN employee e ON d.id = e.department_id WHERE e.status = 'ACTIVE' GROUP BY d.id, d.name, d.manager, d.budget, d.location ORDER BY budget_utilization_percent DESC";
    public static final String PRODUCT_PROFITABILITY_REPORT_SQL =
            "SELECT p.name, p.category, p.price, p.cost, p.stock_quantity, SUM(oi.quantity) as total_sold, SUM(oi.total_price) as total_revenue, (p.cost * SUM(oi.quantity)) as total_cost, (SUM(oi.total_price) - (p.cost * SUM(oi.quantity))) as total_profit, ROUND(((SUM(oi.total_price) - (p.cost * SUM(oi.quantity))) / SUM(oi.total_price)) * 100, 2) as profit_margin_percent FROM product p LEFT JOIN order_items oi ON p.id = oi.product_id GROUP BY p.id, p.name, p.category, p.price, p.cost, p.stock_quantity HAVING total_sold > 0 ORDER BY total_profit DESC";
    public static final String CUSTOMER_SEGMENTATION_ANALYSIS_SQL =
            "SELECT c.name, c.type, c.credit_score, c.account_balance, COUNT(CASE WHEN t.type = 'INCOME' THEN 1 END) as income_transactions, COUNT(CASE WHEN t.type = 'EXPENSE' THEN 1 END) as expense_transactions, SUM(CASE WHEN t.type = 'INCOME' THEN t.amount ELSE 0 END) as total_income, SUM(CASE WHEN t.type = 'EXPENSE' THEN t.amount ELSE 0 END) as total_expense, CASE WHEN SUM(CASE WHEN t.type = 'INCOME' THEN t.amount ELSE 0 END) > 15000 THEN 'High Value' WHEN SUM(CASE WHEN t.type = 'INCOME' THEN t.amount ELSE 0 END) > 8000 THEN 'Medium Value' ELSE 'Low Value' END as value_segment FROM customer c LEFT JOIN transaction t ON c.id = t.customer_id WHERE t.status = 'SUCCESS' GROUP BY c.id, c.name, c.type, c.credit_score, c.account_balance ORDER BY total_income DESC";
    public static final String MONTHLY_REVENUE_TREND_ANALYSIS_SQL =
            "SELECT t.transaction_date as month, COUNT(t.id) as transaction_count, SUM(CASE WHEN t.type = 'INCOME' THEN t.amount ELSE 0 END) as total_income, SUM(CASE WHEN t.type = 'EXPENSE' THEN t.amount ELSE 0 END) as total_expense FROM transaction t WHERE t.status = 'SUCCESS' GROUP BY t.transaction_date ORDER BY t.transaction_date";
    public static final String ORDER_FULFILLMENT_ANALYSIS_SQL =
            "SELECT o.order_date as order_month, COUNT(o.id) as total_orders, SUM(o.total_amount) as total_order_value, COUNT(CASE WHEN o.status = 'COMPLETED' THEN 1 END) as completed_orders, COUNT(CASE WHEN o.status = 'PROCESSING' THEN 1 END) as processing_orders, COUNT(CASE WHEN o.status = 'PENDING' THEN 1 END) as pending_orders FROM orders o GROUP BY o.order_date ORDER BY o.order_date";
    public static final String EMPLOYEE_PERFORMANCE_METRICS_SQL =
            "SELECT d.name as department, e.name as employee_name, e.position, e.salary, e.hire_date, d.budget, ROUND((e.salary / d.budget) * 100, 2) as budget_percentage, CASE WHEN e.salary > 80000 THEN 'High' WHEN e.salary > 60000 THEN 'Medium' ELSE 'Standard' END as salary_tier FROM employee e JOIN department d ON e.department_id = d.id WHERE e.status = 'ACTIVE' ORDER BY d.name, e.salary DESC";
    public static final String CUSTOMER_MERCHANT_REVENUE_MATRIX_SQL =
            "SELECT c.name as customer_name, m.name as merchant_name, m.category as merchant_category, COUNT(t.id) as transaction_count, SUM(t.amount) as total_amount, AVG(t.amount) as avg_transaction, ROW_NUMBER() OVER (PARTITION BY c.name ORDER BY SUM(t.amount) DESC) as merchant_rank_by_customer FROM customer c JOIN transaction t ON c.id = t.customer_id JOIN merchant m ON t.merchant_id = m.id WHERE t.status = 'SUCCESS' GROUP BY c.id, c.name, m.id, m.name, m.category ORDER BY c.name, total_amount DESC";
    public static final String INVENTORY_VELOCITY_ANALYSIS_SQL =
            "SELECT p.name, p.category, p.stock_quantity as current_stock, COALESCE(SUM(oi.quantity), 0) as total_sold, p.price, p.cost, (p.price - p.cost) as unit_profit, ROUND(((p.price - p.cost) / p.price) * 100, 2) as profit_margin_percent FROM product p LEFT JOIN order_items oi ON p.id = oi.product_id LEFT JOIN orders o ON oi.order_id = o.id GROUP BY p.id, p.name, p.category, p.stock_quantity, p.price, p.cost ORDER BY total_sold DESC";
    public static final String FINANCIAL_HEALTH_SCORECARD_SQL =
            "SELECT 'Total Revenue' as metric, SUM(CASE WHEN t.type = 'INCOME' THEN t.amount ELSE 0 END) as value FROM transaction t WHERE t.status = 'SUCCESS' UNION ALL SELECT 'Total Expenses', SUM(CASE WHEN t.type = 'EXPENSE' THEN t.amount ELSE 0 END) FROM transaction t WHERE t.status = 'SUCCESS' UNION ALL SELECT 'Net Profit', (SUM(CASE WHEN t.type = 'INCOME' THEN t.amount ELSE 0 END) - SUM(CASE WHEN t.type = 'EXPENSE' THEN t.amount ELSE 0 END)) FROM transaction t WHERE t.status = 'SUCCESS' UNION ALL SELECT 'Active Customers', COUNT(DISTINCT customer_id) FROM transaction t WHERE t.status = 'SUCCESS' UNION ALL SELECT 'Average Transaction Value', AVG(amount) FROM transaction t WHERE t.status = 'SUCCESS'";

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE;

    @Override
    public boolean supports(String reportName) {
        return CUSTOMER_TRANSACTION_ANALYSIS.equals(reportName)
                || VIP_CUSTOMER_REVENUE_REPORT.equals(reportName)
                || MERCHANT_PERFORMANCE_ANALYSIS.equals(reportName)
                || DEPARTMENT_BUDGET_ANALYSIS.equals(reportName)
                || PRODUCT_PROFITABILITY_REPORT.equals(reportName)
                || CUSTOMER_SEGMENTATION_ANALYSIS.equals(reportName)
                || MONTHLY_REVENUE_TREND_ANALYSIS.equals(reportName)
                || ORDER_FULFILLMENT_ANALYSIS.equals(reportName)
                || EMPLOYEE_PERFORMANCE_METRICS.equals(reportName)
                || CUSTOMER_MERCHANT_REVENUE_MATRIX.equals(reportName)
                || INVENTORY_VELOCITY_ANALYSIS.equals(reportName)
                || FINANCIAL_HEALTH_SCORECARD.equals(reportName);
    }

    @Override
    public List<Map<String, Object>> calculate(String reportName, ReportDataContext context) {
        if (CUSTOMER_TRANSACTION_ANALYSIS.equals(reportName)) {
            return customerTransactionAnalysis(context);
        }
        if (VIP_CUSTOMER_REVENUE_REPORT.equals(reportName)) {
            return vipCustomerRevenueReport(context);
        }
        if (MERCHANT_PERFORMANCE_ANALYSIS.equals(reportName)) {
            return merchantPerformanceAnalysis(context);
        }
        if (DEPARTMENT_BUDGET_ANALYSIS.equals(reportName)) {
            return departmentBudgetAnalysis(context);
        }
        if (PRODUCT_PROFITABILITY_REPORT.equals(reportName)) {
            return productProfitabilityReport(context);
        }
        if (CUSTOMER_SEGMENTATION_ANALYSIS.equals(reportName)) {
            return customerSegmentationAnalysis(context);
        }
        if (MONTHLY_REVENUE_TREND_ANALYSIS.equals(reportName)) {
            return monthlyRevenueTrendAnalysis(context);
        }
        if (ORDER_FULFILLMENT_ANALYSIS.equals(reportName)) {
            return orderFulfillmentAnalysis(context);
        }
        if (EMPLOYEE_PERFORMANCE_METRICS.equals(reportName)) {
            return employeePerformanceMetrics(context);
        }
        if (CUSTOMER_MERCHANT_REVENUE_MATRIX.equals(reportName)) {
            return customerMerchantRevenueMatrix(context);
        }
        if (INVENTORY_VELOCITY_ANALYSIS.equals(reportName)) {
            return inventoryVelocityAnalysis(context);
        }
        if (FINANCIAL_HEALTH_SCORECARD.equals(reportName)) {
            return financialHealthScorecard(context);
        }
        throw new IllegalArgumentException("Unsupported report: " + reportName);
    }

    public static String reportNameForSql(String sql) {
        String normalized = normalizeSql(sql);
        if (normalized.equals(normalizeSql(CUSTOMER_TRANSACTION_ANALYSIS_SQL))) {
            return CUSTOMER_TRANSACTION_ANALYSIS;
        }
        if (normalized.equals(normalizeSql(VIP_CUSTOMER_REVENUE_REPORT_SQL))) {
            return VIP_CUSTOMER_REVENUE_REPORT;
        }
        if (normalized.equals(normalizeSql(MERCHANT_PERFORMANCE_ANALYSIS_SQL))) {
            return MERCHANT_PERFORMANCE_ANALYSIS;
        }
        if (normalized.equals(normalizeSql(DEPARTMENT_BUDGET_ANALYSIS_SQL))) {
            return DEPARTMENT_BUDGET_ANALYSIS;
        }
        if (normalized.equals(normalizeSql(PRODUCT_PROFITABILITY_REPORT_SQL))) {
            return PRODUCT_PROFITABILITY_REPORT;
        }
        if (normalized.equals(normalizeSql(CUSTOMER_SEGMENTATION_ANALYSIS_SQL))) {
            return CUSTOMER_SEGMENTATION_ANALYSIS;
        }
        if (normalized.equals(normalizeSql(MONTHLY_REVENUE_TREND_ANALYSIS_SQL))) {
            return MONTHLY_REVENUE_TREND_ANALYSIS;
        }
        if (normalized.equals(normalizeSql(ORDER_FULFILLMENT_ANALYSIS_SQL))) {
            return ORDER_FULFILLMENT_ANALYSIS;
        }
        if (normalized.equals(normalizeSql(EMPLOYEE_PERFORMANCE_METRICS_SQL))) {
            return EMPLOYEE_PERFORMANCE_METRICS;
        }
        if (normalized.equals(normalizeSql(CUSTOMER_MERCHANT_REVENUE_MATRIX_SQL))) {
            return CUSTOMER_MERCHANT_REVENUE_MATRIX;
        }
        if (normalized.equals(normalizeSql(INVENTORY_VELOCITY_ANALYSIS_SQL))) {
            return INVENTORY_VELOCITY_ANALYSIS;
        }
        if (normalized.equals(normalizeSql(FINANCIAL_HEALTH_SCORECARD_SQL))) {
            return FINANCIAL_HEALTH_SCORECARD;
        }
        return null;
    }

    private static String normalizeSql(String sql) {
        if (sql == null) {
            return "";
        }
        return sql.replaceAll("\\s+", " ")
                .replace(";", "")
                .trim()
                .toLowerCase(Locale.ROOT);
    }

    private List<Map<String, Object>> customerTransactionAnalysis(ReportDataContext context) {
        List<Map<String, Object>> rows = new ArrayList<>();
        for (Map<String, Object> customer : context.getCustomers()) {
            long customerId = ReportRuleSupport.longValue(customer, "id");
            BigDecimal totalAmount = BigDecimal.ZERO;
            long txCount = 0L;
            for (Map<String, Object> tx : context.getTransactions()) {
                if (ReportRuleSupport.longValue(tx, "customer_id") == customerId
                        && "SUCCESS".equalsIgnoreCase(ReportRuleSupport.stringValue(tx, "status"))) {
                    totalAmount = totalAmount.add(ReportRuleSupport.decimalValue(tx, "amount"));
                    txCount++;
                }
            }
            if (txCount > 0) {
                rows.add(ReportRuleSupport.row(
                        "name", ReportRuleSupport.stringValue(customer, "name"),
                        "type", ReportRuleSupport.stringValue(customer, "type"),
                        "credit_score", ReportRuleSupport.longValue(customer, "credit_score"),
                        "total_amount", totalAmount,
                        "tx_count", txCount,
                        "avg_transaction", ReportRuleSupport.divide(totalAmount, BigDecimal.valueOf(txCount), 2)
                ));
            }
        }
        rows.sort((left, right) -> ReportRuleSupport.decimalValue(right, "total_amount")
                .compareTo(ReportRuleSupport.decimalValue(left, "total_amount")));
        return rows;
    }

    private List<Map<String, Object>> vipCustomerRevenueReport(ReportDataContext context) {
        List<Map<String, Object>> rows = new ArrayList<>();
        for (Map<String, Object> customer : context.getCustomers()) {
            if (!"VIP".equalsIgnoreCase(ReportRuleSupport.stringValue(customer, "type"))) {
                continue;
            }
            long customerId = ReportRuleSupport.longValue(customer, "id");
            BigDecimal income = BigDecimal.ZERO;
            BigDecimal expense = BigDecimal.ZERO;
            for (Map<String, Object> tx : context.getTransactions()) {
                if (ReportRuleSupport.longValue(tx, "customer_id") == customerId) {
                    if ("INCOME".equalsIgnoreCase(ReportRuleSupport.stringValue(tx, "type"))) {
                        income = income.add(ReportRuleSupport.decimalValue(tx, "amount"));
                    } else if ("EXPENSE".equalsIgnoreCase(ReportRuleSupport.stringValue(tx, "type"))) {
                        expense = expense.add(ReportRuleSupport.decimalValue(tx, "amount"));
                    }
                }
            }
            rows.add(ReportRuleSupport.row(
                    "name", ReportRuleSupport.stringValue(customer, "name"),
                    "email", ReportRuleSupport.stringValue(customer, "email"),
                    "account_balance", ReportRuleSupport.decimalValue(customer, "account_balance"),
                    "income", income,
                    "expense", expense,
                    "net_profit", income.subtract(expense)
            ));
        }
        rows.sort((left, right) -> ReportRuleSupport.decimalValue(right, "net_profit")
                .compareTo(ReportRuleSupport.decimalValue(left, "net_profit")));
        return rows;
    }

    private List<Map<String, Object>> merchantPerformanceAnalysis(ReportDataContext context) {
        List<Map<String, Object>> rows = new ArrayList<>();
        for (Map<String, Object> merchant : context.getMerchants()) {
            long merchantId = ReportRuleSupport.longValue(merchant, "id");
            BigDecimal totalVolume = BigDecimal.ZERO;
            long transactionCount = 0L;
            for (Map<String, Object> tx : context.getTransactions()) {
                if (ReportRuleSupport.longValue(tx, "merchant_id") == merchantId
                        && "SUCCESS".equalsIgnoreCase(ReportRuleSupport.stringValue(tx, "status"))) {
                    totalVolume = totalVolume.add(ReportRuleSupport.decimalValue(tx, "amount"));
                    transactionCount++;
                }
            }
            if (transactionCount > 0) {
                BigDecimal commissionRate = ReportRuleSupport.decimalValue(merchant, "commission_rate");
                rows.add(ReportRuleSupport.row(
                        "merchant_name", ReportRuleSupport.stringValue(merchant, "name"),
                        "category", ReportRuleSupport.stringValue(merchant, "category"),
                        "transaction_count", transactionCount,
                        "total_volume", totalVolume,
                        "avg_transaction_amount", ReportRuleSupport.divide(totalVolume, BigDecimal.valueOf(transactionCount), 2),
                        "estimated_commission", totalVolume.multiply(commissionRate)
                ));
            }
        }
        rows.sort((left, right) -> ReportRuleSupport.decimalValue(right, "total_volume")
                .compareTo(ReportRuleSupport.decimalValue(left, "total_volume")));
        return rows;
    }

    private List<Map<String, Object>> departmentBudgetAnalysis(ReportDataContext context) {
        List<Map<String, Object>> rows = new ArrayList<>();
        for (Map<String, Object> department : context.getDepartments()) {
            long departmentId = ReportRuleSupport.longValue(department, "id");
            BigDecimal totalSalary = BigDecimal.ZERO;
            long employeeCount = 0L;
            for (Map<String, Object> employee : context.getEmployees()) {
                if (ReportRuleSupport.longValue(employee, "department_id") == departmentId
                        && "ACTIVE".equalsIgnoreCase(ReportRuleSupport.stringValue(employee, "status"))) {
                    totalSalary = totalSalary.add(ReportRuleSupport.decimalValue(employee, "salary"));
                    employeeCount++;
                }
            }
            BigDecimal budget = ReportRuleSupport.decimalValue(department, "budget");
            BigDecimal variance = budget.subtract(totalSalary);
            rows.add(ReportRuleSupport.row(
                    "department", ReportRuleSupport.stringValue(department, "name"),
                    "manager", ReportRuleSupport.stringValue(department, "manager"),
                    "budget", budget,
                    "location", ReportRuleSupport.stringValue(department, "location"),
                    "employee_count", employeeCount,
                    "total_salary_cost", totalSalary,
                    "budget_variance", variance,
                    "budget_utilization_percent", ReportRuleSupport.divide(totalSalary.multiply(BigDecimal.valueOf(100)), budget, 2)
            ));
        }
        rows.sort((left, right) -> ReportRuleSupport.decimalValue(right, "budget_utilization_percent")
                .compareTo(ReportRuleSupport.decimalValue(left, "budget_utilization_percent")));
        return rows;
    }

    private List<Map<String, Object>> productProfitabilityReport(ReportDataContext context) {
        List<Map<String, Object>> rows = new ArrayList<>();
        for (Map<String, Object> product : context.getProducts()) {
            long productId = ReportRuleSupport.longValue(product, "id");
            long totalSold = 0L;
            BigDecimal totalRevenue = BigDecimal.ZERO;
            for (Map<String, Object> item : context.getOrderItems()) {
                if (ReportRuleSupport.longValue(item, "product_id") == productId) {
                    totalSold += ReportRuleSupport.longValue(item, "quantity");
                    totalRevenue = totalRevenue.add(ReportRuleSupport.decimalValue(item, "total_price"));
                }
            }
            if (totalSold > 0) {
                BigDecimal cost = ReportRuleSupport.decimalValue(product, "cost");
                BigDecimal totalCost = cost.multiply(BigDecimal.valueOf(totalSold));
                BigDecimal totalProfit = totalRevenue.subtract(totalCost);
                rows.add(ReportRuleSupport.row(
                        "name", ReportRuleSupport.stringValue(product, "name"),
                        "category", ReportRuleSupport.stringValue(product, "category"),
                        "price", ReportRuleSupport.decimalValue(product, "price"),
                        "cost", cost,
                        "stock_quantity", ReportRuleSupport.longValue(product, "stock_quantity"),
                        "total_sold", totalSold,
                        "total_revenue", totalRevenue,
                        "total_cost", totalCost,
                        "total_profit", totalProfit,
                        "profit_margin_percent", ReportRuleSupport.divide(totalProfit.multiply(BigDecimal.valueOf(100)), totalRevenue, 2)
                ));
            }
        }
        rows.sort((left, right) -> ReportRuleSupport.decimalValue(right, "total_profit")
                .compareTo(ReportRuleSupport.decimalValue(left, "total_profit")));
        return rows;
    }

    private List<Map<String, Object>> customerSegmentationAnalysis(ReportDataContext context) {
        List<Map<String, Object>> rows = new ArrayList<>();
        for (Map<String, Object> customer : context.getCustomers()) {
            long customerId = ReportRuleSupport.longValue(customer, "id");
            BigDecimal totalIncome = BigDecimal.ZERO;
            BigDecimal totalExpense = BigDecimal.ZERO;
            long incomeTransactions = 0L;
            long expenseTransactions = 0L;
            boolean hasSuccessfulTransaction = false;
            for (Map<String, Object> tx : context.getTransactions()) {
                if (ReportRuleSupport.longValue(tx, "customer_id") == customerId
                        && "SUCCESS".equalsIgnoreCase(ReportRuleSupport.stringValue(tx, "status"))) {
                    hasSuccessfulTransaction = true;
                    if ("INCOME".equalsIgnoreCase(ReportRuleSupport.stringValue(tx, "type"))) {
                        totalIncome = totalIncome.add(ReportRuleSupport.decimalValue(tx, "amount"));
                        incomeTransactions++;
                    } else if ("EXPENSE".equalsIgnoreCase(ReportRuleSupport.stringValue(tx, "type"))) {
                        totalExpense = totalExpense.add(ReportRuleSupport.decimalValue(tx, "amount"));
                        expenseTransactions++;
                    }
                }
            }
            if (hasSuccessfulTransaction) {
                String valueSegment;
                if (totalIncome.compareTo(BigDecimal.valueOf(15000)) > 0) {
                    valueSegment = "High Value";
                } else if (totalIncome.compareTo(BigDecimal.valueOf(8000)) > 0) {
                    valueSegment = "Medium Value";
                } else {
                    valueSegment = "Low Value";
                }
                rows.add(ReportRuleSupport.row(
                        "name", ReportRuleSupport.stringValue(customer, "name"),
                        "type", ReportRuleSupport.stringValue(customer, "type"),
                        "credit_score", ReportRuleSupport.longValue(customer, "credit_score"),
                        "account_balance", ReportRuleSupport.decimalValue(customer, "account_balance"),
                        "income_transactions", incomeTransactions,
                        "expense_transactions", expenseTransactions,
                        "total_income", totalIncome,
                        "total_expense", totalExpense,
                        "value_segment", valueSegment
                ));
            }
        }
        rows.sort((left, right) -> ReportRuleSupport.decimalValue(right, "total_income")
                .compareTo(ReportRuleSupport.decimalValue(left, "total_income")));
        return rows;
    }

    private List<Map<String, Object>> monthlyRevenueTrendAnalysis(ReportDataContext context) {
        Map<LocalDate, List<Map<String, Object>>> grouped = new LinkedHashMap<>();
        for (Map<String, Object> transaction : context.getTransactions()) {
            if (!"SUCCESS".equalsIgnoreCase(ReportRuleSupport.stringValue(transaction, "status"))) {
                continue;
            }
            LocalDate date = ReportRuleSupport.dateValue(transaction, "transaction_date");
            grouped.computeIfAbsent(date, ignored -> new ArrayList<>()).add(transaction);
        }

        List<Map<String, Object>> rows = new ArrayList<>();
        List<LocalDate> dates = new ArrayList<>(grouped.keySet());
        dates.sort(LocalDate::compareTo);
        for (LocalDate date : dates) {
            List<Map<String, Object>> transactions = grouped.get(date);
            BigDecimal income = BigDecimal.ZERO;
            BigDecimal expense = BigDecimal.ZERO;
            for (Map<String, Object> tx : transactions) {
                if ("INCOME".equalsIgnoreCase(ReportRuleSupport.stringValue(tx, "type"))) {
                    income = income.add(ReportRuleSupport.decimalValue(tx, "amount"));
                } else if ("EXPENSE".equalsIgnoreCase(ReportRuleSupport.stringValue(tx, "type"))) {
                    expense = expense.add(ReportRuleSupport.decimalValue(tx, "amount"));
                }
            }
            rows.add(ReportRuleSupport.row(
                    "month", DATE_FORMAT.format(date),
                    "transaction_count", transactions.size(),
                    "total_income", income,
                    "total_expense", expense
            ));
        }
        return rows;
    }

    private List<Map<String, Object>> orderFulfillmentAnalysis(ReportDataContext context) {
        Map<LocalDate, List<Map<String, Object>>> grouped = new LinkedHashMap<>();
        for (Map<String, Object> order : context.getOrders()) {
            LocalDate date = ReportRuleSupport.dateValue(order, "order_date");
            grouped.computeIfAbsent(date, ignored -> new ArrayList<>()).add(order);
        }

        List<Map<String, Object>> rows = new ArrayList<>();
        List<LocalDate> dates = new ArrayList<>(grouped.keySet());
        dates.sort(LocalDate::compareTo);
        for (LocalDate date : dates) {
            List<Map<String, Object>> orders = grouped.get(date);
            long completed = 0L;
            long processing = 0L;
            long pending = 0L;
            BigDecimal totalValue = BigDecimal.ZERO;
            for (Map<String, Object> order : orders) {
                totalValue = totalValue.add(ReportRuleSupport.decimalValue(order, "total_amount"));
                String status = ReportRuleSupport.stringValue(order, "status");
                if ("COMPLETED".equalsIgnoreCase(status)) {
                    completed++;
                } else if ("PROCESSING".equalsIgnoreCase(status)) {
                    processing++;
                } else if ("PENDING".equalsIgnoreCase(status)) {
                    pending++;
                }
            }
            rows.add(ReportRuleSupport.row(
                    "order_month", DATE_FORMAT.format(date),
                    "total_orders", orders.size(),
                    "total_order_value", totalValue,
                    "completed_orders", completed,
                    "processing_orders", processing,
                    "pending_orders", pending
            ));
        }
        return rows;
    }

    private List<Map<String, Object>> employeePerformanceMetrics(ReportDataContext context) {
        List<Map<String, Object>> rows = new ArrayList<>();
        for (Map<String, Object> employee : context.getEmployees()) {
            if (!"ACTIVE".equalsIgnoreCase(ReportRuleSupport.stringValue(employee, "status"))) {
                continue;
            }
            Map<String, Object> department = findDepartment(context, ReportRuleSupport.longValue(employee, "department_id"));
            if (department == null) {
                continue;
            }
            BigDecimal salary = ReportRuleSupport.decimalValue(employee, "salary");
            BigDecimal budget = ReportRuleSupport.decimalValue(department, "budget");
            String tier;
            if (salary.compareTo(BigDecimal.valueOf(80000)) > 0) {
                tier = "High";
            } else if (salary.compareTo(BigDecimal.valueOf(60000)) > 0) {
                tier = "Medium";
            } else {
                tier = "Standard";
            }
            rows.add(ReportRuleSupport.row(
                    "department", ReportRuleSupport.stringValue(department, "name"),
                    "employee_name", ReportRuleSupport.stringValue(employee, "name"),
                    "position", ReportRuleSupport.stringValue(employee, "position"),
                    "salary", salary,
                    "hire_date", ReportRuleSupport.dateValue(employee, "hire_date"),
                    "budget", budget,
                    "budget_percentage", ReportRuleSupport.divide(salary.multiply(BigDecimal.valueOf(100)), budget, 2),
                    "salary_tier", tier
            ));
        }
        rows.sort((left, right) -> {
            int departmentCompare = ReportRuleSupport.stringValue(left, "department")
                    .compareTo(ReportRuleSupport.stringValue(right, "department"));
            if (departmentCompare != 0) {
                return departmentCompare;
            }
            return ReportRuleSupport.decimalValue(right, "salary")
                    .compareTo(ReportRuleSupport.decimalValue(left, "salary"));
        });
        return rows;
    }

    private List<Map<String, Object>> customerMerchantRevenueMatrix(ReportDataContext context) {
        List<Map<String, Object>> rows = new ArrayList<>();
        for (Map<String, Object> customer : context.getCustomers()) {
            String customerName = ReportRuleSupport.stringValue(customer, "name");
            Set<String> merchantKeys = new LinkedHashSet<>();
            for (Map<String, Object> transaction : context.getTransactions()) {
                if (!"SUCCESS".equalsIgnoreCase(ReportRuleSupport.stringValue(transaction, "status"))) {
                    continue;
                }
                if (ReportRuleSupport.longValue(transaction, "customer_id") != ReportRuleSupport.longValue(customer, "id")) {
                    continue;
                }
                Map<String, Object> merchant = findMerchant(context, ReportRuleSupport.longValue(transaction, "merchant_id"));
                if (merchant == null) {
                    continue;
                }
                merchantKeys.add(ReportRuleSupport.stringValue(merchant, "name"));
            }
            List<Map<String, Object>> customerRows = new ArrayList<>();
            for (String merchantName : merchantKeys) {
                Map<String, Object> merchant = findMerchantByName(context, merchantName);
                if (merchant == null) {
                    continue;
                }
                BigDecimal totalAmount = BigDecimal.ZERO;
                long transactionCount = 0L;
                for (Map<String, Object> transaction : context.getTransactions()) {
                    if (ReportRuleSupport.longValue(transaction, "customer_id") == ReportRuleSupport.longValue(customer, "id")
                            && ReportRuleSupport.longValue(transaction, "merchant_id") == ReportRuleSupport.longValue(merchant, "id")
                            && "SUCCESS".equalsIgnoreCase(ReportRuleSupport.stringValue(transaction, "status"))) {
                        totalAmount = totalAmount.add(ReportRuleSupport.decimalValue(transaction, "amount"));
                        transactionCount++;
                    }
                }
                customerRows.add(ReportRuleSupport.row(
                        "customer_name", customerName,
                        "merchant_name", ReportRuleSupport.stringValue(merchant, "name"),
                        "merchant_category", ReportRuleSupport.stringValue(merchant, "category"),
                        "transaction_count", transactionCount,
                        "total_amount", totalAmount,
                        "avg_transaction", ReportRuleSupport.divide(totalAmount, BigDecimal.valueOf(transactionCount), 2)
                ));
            }
            customerRows.sort((left, right) -> ReportRuleSupport.decimalValue(right, "total_amount")
                    .compareTo(ReportRuleSupport.decimalValue(left, "total_amount")));
            int rank = 1;
            for (Map<String, Object> row : customerRows) {
                row.put("merchant_rank_by_customer", rank++);
                rows.add(row);
            }
        }

        rows.sort((left, right) -> {
            int customerCompare = ReportRuleSupport.stringValue(left, "customer_name")
                    .compareTo(ReportRuleSupport.stringValue(right, "customer_name"));
            if (customerCompare != 0) {
                return customerCompare;
            }
            return ReportRuleSupport.decimalValue(right, "total_amount")
                    .compareTo(ReportRuleSupport.decimalValue(left, "total_amount"));
        });
        return rows;
    }

    private List<Map<String, Object>> inventoryVelocityAnalysis(ReportDataContext context) {
        List<Map<String, Object>> rows = new ArrayList<>();
        for (Map<String, Object> product : context.getProducts()) {
            long productId = ReportRuleSupport.longValue(product, "id");
            long totalSold = 0L;
            for (Map<String, Object> item : context.getOrderItems()) {
                if (ReportRuleSupport.longValue(item, "product_id") == productId) {
                    totalSold += ReportRuleSupport.longValue(item, "quantity");
                }
            }
            BigDecimal price = ReportRuleSupport.decimalValue(product, "price");
            BigDecimal cost = ReportRuleSupport.decimalValue(product, "cost");
            BigDecimal unitProfit = price.subtract(cost);
            rows.add(ReportRuleSupport.row(
                    "name", ReportRuleSupport.stringValue(product, "name"),
                    "category", ReportRuleSupport.stringValue(product, "category"),
                    "current_stock", ReportRuleSupport.longValue(product, "stock_quantity"),
                    "total_sold", totalSold,
                    "price", price,
                    "cost", cost,
                    "unit_profit", unitProfit,
                    "profit_margin_percent", ReportRuleSupport.divide(unitProfit.multiply(BigDecimal.valueOf(100)), price, 2)
            ));
        }
        rows.sort((left, right) -> Long.compare(
                ReportRuleSupport.longValue(right, "total_sold"),
                ReportRuleSupport.longValue(left, "total_sold")));
        return rows;
    }

    private List<Map<String, Object>> financialHealthScorecard(ReportDataContext context) {
        BigDecimal totalRevenue = BigDecimal.ZERO;
        BigDecimal totalExpenses = BigDecimal.ZERO;
        Set<Long> activeCustomers = new LinkedHashSet<>();
        long transactionCount = 0L;
        for (Map<String, Object> transaction : context.getTransactions()) {
            if (!"SUCCESS".equalsIgnoreCase(ReportRuleSupport.stringValue(transaction, "status"))) {
                continue;
            }
            transactionCount++;
            Long customerId = ReportRuleSupport.longValue(transaction, "customer_id");
            activeCustomers.add(customerId);
            if ("INCOME".equalsIgnoreCase(ReportRuleSupport.stringValue(transaction, "type"))) {
                totalRevenue = totalRevenue.add(ReportRuleSupport.decimalValue(transaction, "amount"));
            } else if ("EXPENSE".equalsIgnoreCase(ReportRuleSupport.stringValue(transaction, "type"))) {
                totalExpenses = totalExpenses.add(ReportRuleSupport.decimalValue(transaction, "amount"));
            }
        }

        BigDecimal netProfit = totalRevenue.subtract(totalExpenses);
        BigDecimal averageTransactionValue = transactionCount == 0L
                ? BigDecimal.ZERO
                : ReportRuleSupport.divide(totalRevenue.add(totalExpenses), BigDecimal.valueOf(transactionCount), 2);

        List<Map<String, Object>> rows = new ArrayList<>();
        rows.add(ReportRuleSupport.row("metric", "Total Revenue", "value", totalRevenue));
        rows.add(ReportRuleSupport.row("metric", "Total Expenses", "value", totalExpenses));
        rows.add(ReportRuleSupport.row("metric", "Net Profit", "value", netProfit));
        rows.add(ReportRuleSupport.row("metric", "Active Customers", "value", activeCustomers.size()));
        rows.add(ReportRuleSupport.row("metric", "Average Transaction Value", "value", averageTransactionValue));
        return rows;
    }

    private Map<String, Object> findDepartment(ReportDataContext context, long departmentId) {
        for (Map<String, Object> department : context.getDepartments()) {
            if (ReportRuleSupport.longValue(department, "id") == departmentId) {
                return department;
            }
        }
        return null;
    }

    private Map<String, Object> findCustomer(ReportDataContext context, long customerId) {
        for (Map<String, Object> customer : context.getCustomers()) {
            if (ReportRuleSupport.longValue(customer, "id") == customerId) {
                return customer;
            }
        }
        return null;
    }

    private Map<String, Object> findMerchant(ReportDataContext context, long merchantId) {
        for (Map<String, Object> merchant : context.getMerchants()) {
            if (ReportRuleSupport.longValue(merchant, "id") == merchantId) {
                return merchant;
            }
        }
        return null;
    }

    private Map<String, Object> findMerchantByName(ReportDataContext context, String merchantName) {
        for (Map<String, Object> merchant : context.getMerchants()) {
            if (merchantName.equals(ReportRuleSupport.stringValue(merchant, "name"))) {
                return merchant;
            }
        }
        return null;
    }
}
