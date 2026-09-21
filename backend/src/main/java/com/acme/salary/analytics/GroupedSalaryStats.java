package com.acme.salary.analytics;

public record GroupedSalaryStats(String group, CurrencyAwareStats stats) {
}
