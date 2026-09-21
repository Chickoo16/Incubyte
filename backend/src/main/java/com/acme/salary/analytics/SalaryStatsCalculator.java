package com.acme.salary.analytics;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * Turns a raw list of gross salaries into the headline dashboard numbers.
 * SQL aggregate functions cover count/min/max/avg, but SQLite (like standard
 * SQL) has no MEDIAN, so it's computed here in Java instead - cheap at up to
 * 10,000 values.
 */
public final class SalaryStatsCalculator {

    private static final BigDecimal ZERO = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);

    private SalaryStatsCalculator() {
    }

    public static SalaryStats compute(List<BigDecimal> grossAmounts) {
        if (grossAmounts.isEmpty()) {
            return new SalaryStats(0, ZERO, ZERO, ZERO, ZERO);
        }

        List<BigDecimal> sorted = new ArrayList<>(grossAmounts);
        sorted.sort(BigDecimal::compareTo);

        BigDecimal sum = BigDecimal.ZERO;
        for (BigDecimal amount : sorted) {
            sum = sum.add(amount);
        }
        BigDecimal average = sum.divide(BigDecimal.valueOf(sorted.size()), 2, RoundingMode.HALF_UP);

        return new SalaryStats(sorted.size(), sorted.get(0), sorted.get(sorted.size() - 1), average, median(sorted));
    }

    private static BigDecimal median(List<BigDecimal> sorted) {
        int size = sorted.size();
        int middle = size / 2;
        if (size % 2 == 1) {
            return sorted.get(middle).setScale(2, RoundingMode.HALF_UP);
        }
        BigDecimal sum = sorted.get(middle - 1).add(sorted.get(middle));
        return sum.divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP);
    }
}
