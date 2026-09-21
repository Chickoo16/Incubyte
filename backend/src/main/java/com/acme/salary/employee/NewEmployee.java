package com.acme.salary.employee;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Everything needed to onboard an employee, shared by the HR-facing create
 * endpoint and the seed script so both build an {@link Employee} the same way.
 */
public record NewEmployee(
        String firstName,
        String lastName,
        String email,
        String department,
        String jobTitle,
        String country,
        String currency,
        LocalDate dateOfJoining,
        BigDecimal basic,
        BigDecimal allowances,
        BigDecimal deductions) {
}
