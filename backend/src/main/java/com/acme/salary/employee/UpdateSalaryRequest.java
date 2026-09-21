package com.acme.salary.employee;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record UpdateSalaryRequest(
        @NotNull @PositiveOrZero BigDecimal basic,
        @NotNull @PositiveOrZero BigDecimal allowances,
        @NotNull @PositiveOrZero BigDecimal deductions) {
}
