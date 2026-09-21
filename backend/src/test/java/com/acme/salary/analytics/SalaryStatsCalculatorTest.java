package com.acme.salary.analytics;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SalaryStatsCalculatorTest {

    @Test
    void computesCountMinMaxAndAverage() {
        SalaryStats stats = SalaryStatsCalculator.compute(List.of(
                new BigDecimal("30000"), new BigDecimal("50000"), new BigDecimal("70000")));

        assertThat(stats.count()).isEqualTo(3);
        assertThat(stats.min()).isEqualByComparingTo("30000");
        assertThat(stats.max()).isEqualByComparingTo("70000");
        assertThat(stats.average()).isEqualByComparingTo("50000");
    }

    @Test
    void medianOfAnOddCountIsTheMiddleValueRegardlessOfInputOrder() {
        SalaryStats stats = SalaryStatsCalculator.compute(List.of(
                new BigDecimal("70000"), new BigDecimal("30000"), new BigDecimal("50000")));

        assertThat(stats.median()).isEqualByComparingTo("50000");
    }

    @Test
    void medianOfAnEvenCountIsTheAverageOfTheTwoMiddleValues() {
        SalaryStats stats = SalaryStatsCalculator.compute(List.of(
                new BigDecimal("10000"), new BigDecimal("20000"),
                new BigDecimal("30000"), new BigDecimal("40000")));

        assertThat(stats.median()).isEqualByComparingTo("25000");
    }

    @Test
    void singleValueIsItsOwnStatsInEveryDimension() {
        SalaryStats stats = SalaryStatsCalculator.compute(List.of(new BigDecimal("45000")));

        assertThat(stats.count()).isEqualTo(1);
        assertThat(stats.min()).isEqualByComparingTo("45000");
        assertThat(stats.max()).isEqualByComparingTo("45000");
        assertThat(stats.average()).isEqualByComparingTo("45000");
        assertThat(stats.median()).isEqualByComparingTo("45000");
    }

    @Test
    void emptyInputYieldsZeroedStatsRatherThanDividingByZero() {
        SalaryStats stats = SalaryStatsCalculator.compute(List.of());

        assertThat(stats.count()).isZero();
        assertThat(stats.min()).isEqualByComparingTo("0");
        assertThat(stats.max()).isEqualByComparingTo("0");
        assertThat(stats.average()).isEqualByComparingTo("0");
        assertThat(stats.median()).isEqualByComparingTo("0");
    }

    @Test
    void averageRoundsToTwoDecimalPlaces() {
        SalaryStats stats = SalaryStatsCalculator.compute(List.of(
                new BigDecimal("10000"), new BigDecimal("10000"), new BigDecimal("10001")));

        assertThat(stats.average()).isEqualByComparingTo("10000.33");
    }
}
