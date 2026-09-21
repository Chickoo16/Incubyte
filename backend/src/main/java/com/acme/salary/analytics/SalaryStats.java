package com.acme.salary.analytics;

import java.math.BigDecimal;

public record SalaryStats(long count, BigDecimal min, BigDecimal max, BigDecimal average, BigDecimal median) {
}
