package com.legacy.report.service.rules;

import java.util.List;
import java.util.Map;

public interface ReportRuleCalculator {
    boolean supports(String reportName);

    List<Map<String, Object>> calculate(String reportName, ReportDataContext context);
}
