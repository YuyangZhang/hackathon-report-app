package com.legacy.report.service.rules;

import java.util.List;
import java.util.Map;

public class ReportDataContext {

    private final List<Map<String, Object>> customers;
    private final List<Map<String, Object>> transactions;
    private final List<Map<String, Object>> merchants;
    private final List<Map<String, Object>> departments;
    private final List<Map<String, Object>> employees;
    private final List<Map<String, Object>> products;
    private final List<Map<String, Object>> orders;
    private final List<Map<String, Object>> orderItems;

    public ReportDataContext(List<Map<String, Object>> customers,
                             List<Map<String, Object>> transactions,
                             List<Map<String, Object>> merchants,
                             List<Map<String, Object>> departments,
                             List<Map<String, Object>> employees,
                             List<Map<String, Object>> products,
                             List<Map<String, Object>> orders,
                             List<Map<String, Object>> orderItems) {
        this.customers = customers;
        this.transactions = transactions;
        this.merchants = merchants;
        this.departments = departments;
        this.employees = employees;
        this.products = products;
        this.orders = orders;
        this.orderItems = orderItems;
    }

    public List<Map<String, Object>> getCustomers() {
        return customers;
    }

    public List<Map<String, Object>> getTransactions() {
        return transactions;
    }

    public List<Map<String, Object>> getMerchants() {
        return merchants;
    }

    public List<Map<String, Object>> getDepartments() {
        return departments;
    }

    public List<Map<String, Object>> getEmployees() {
        return employees;
    }

    public List<Map<String, Object>> getProducts() {
        return products;
    }

    public List<Map<String, Object>> getOrders() {
        return orders;
    }

    public List<Map<String, Object>> getOrderItems() {
        return orderItems;
    }
}
