package com.acme.salary.employee;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

class SalaryCalculatorTest {

    @Test
    void grossIsBasicPlusAllowances() {
        SalaryBreakdown breakdown = SalaryCalculator.compute(
                new BigDecimal("50000"), new BigDecimal("12000"), new BigDecimal("3000"));

        assertThat(breakdown.gross()).isEqualByComparingTo("62000");
    }

    @Test
    void netIsGrossMinusDeductions() {
        SalaryBreakdown breakdown = SalaryCalculator.compute(
                new BigDecimal("50000"), new BigDecimal("12000"), new BigDecimal("3000"));

        assertThat(breakdown.net()).isEqualByComparingTo("59000");
    }

    @Test
    void retainsTheInputComponents() {
        SalaryBreakdown breakdown = SalaryCalculator.compute(
                new BigDecimal("50000"), new BigDecimal("12000"), new BigDecimal("3000"));

        assertThat(breakdown.basic()).isEqualByComparingTo("50000");
        assertThat(breakdown.allowances()).isEqualByComparingTo("12000");
        assertThat(breakdown.deductions()).isEqualByComparingTo("3000");
    }

    @Test
    void allowsZeroAllowancesAndDeductions() {
        SalaryBreakdown breakdown = SalaryCalculator.compute(
                new BigDecimal("50000"), BigDecimal.ZERO, BigDecimal.ZERO);

        assertThat(breakdown.gross()).isEqualByComparingTo("50000");
        assertThat(breakdown.net()).isEqualByComparingTo("50000");
    }

    @ParameterizedTest
    @CsvSource({
            "-1, 0, 0",   // negative basic
            "50000, -1, 0", // negative allowances
            "50000, 0, -1", // negative deductions
    })
    void rejectsNegativeComponents(BigDecimal basic, BigDecimal allowances, BigDecimal deductions) {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> SalaryCalculator.compute(basic, allowances, deductions));
    }

    @Test
    void rejectsDeductionsThatWouldMakeNetNegative() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> SalaryCalculator.compute(
                        new BigDecimal("50000"), BigDecimal.ZERO, new BigDecimal("50001")))
                .withMessageContaining("deductions");
    }

    @Test
    void rejectsNullComponents() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> SalaryCalculator.compute(null, BigDecimal.ZERO, BigDecimal.ZERO));
    }
}
