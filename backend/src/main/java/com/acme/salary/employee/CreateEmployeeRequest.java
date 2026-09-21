package com.acme.salary.employee;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateEmployeeRequest(
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotBlank @Email String email,
        @NotBlank String department,
        @NotBlank String jobTitle,
        @NotBlank String country,
        @NotBlank String currency,
        @NotNull LocalDate dateOfJoining,
        @NotNull @PositiveOrZero BigDecimal basic,
        @NotNull @PositiveOrZero BigDecimal allowances,
        @NotNull @PositiveOrZero BigDecimal deductions) {

    public NewEmployee toNewEmployee() {
        return new NewEmployee(firstName, lastName, email, department, jobTitle, country, currency,
                dateOfJoining, basic, allowances, deductions);
    }
}
