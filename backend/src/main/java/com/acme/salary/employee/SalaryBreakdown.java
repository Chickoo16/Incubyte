package com.acme.salary.employee;

import java.math.BigDecimal;

/**
 * The components of an employee's current salary, plus the derived gross and
 * net figures. Only {@link SalaryCalculator} constructs one, so any instance
 * in circulation is guaranteed internally consistent.
 */
public record SalaryBreakdown(
        BigDecimal basic,
        BigDecimal allowances,
        BigDecimal deductions,
        BigDecimal gross,
        BigDecimal net) {
}
