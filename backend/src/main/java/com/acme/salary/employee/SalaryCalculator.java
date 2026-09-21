package com.acme.salary.employee;

import java.math.BigDecimal;

/**
 * Derives gross and net pay from the components HR actually edits. Gross and
 * net are never accepted as direct input, so a stored salary can't drift
 * from basic + allowances - deductions.
 */
public final class SalaryCalculator {

    private SalaryCalculator() {
    }

    public static SalaryBreakdown compute(BigDecimal basic, BigDecimal allowances, BigDecimal deductions) {
        requireNonNegative(basic, "basic");
        requireNonNegative(allowances, "allowances");
        requireNonNegative(deductions, "deductions");

        BigDecimal gross = basic.add(allowances);
        if (deductions.compareTo(gross) > 0) {
            throw new IllegalArgumentException(
                    "deductions (%s) cannot exceed gross salary (%s)".formatted(deductions, gross));
        }
        BigDecimal net = gross.subtract(deductions);

        return new SalaryBreakdown(basic, allowances, deductions, gross, net);
    }

    private static void requireNonNegative(BigDecimal value, String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException(fieldName + " must not be null");
        }
        if (value.signum() < 0) {
            throw new IllegalArgumentException(fieldName + " must not be negative: " + value);
        }
    }
}
