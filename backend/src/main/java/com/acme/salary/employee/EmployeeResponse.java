package com.acme.salary.employee;

import java.math.BigDecimal;
import java.time.LocalDate;

public record EmployeeResponse(
        Long id,
        String employeeCode,
        String firstName,
        String lastName,
        String email,
        String department,
        String jobTitle,
        String country,
        String currency,
        EmployeeStatus status,
        LocalDate dateOfJoining,
        BigDecimal basicAmount,
        BigDecimal allowancesAmount,
        BigDecimal deductionsAmount,
        BigDecimal grossAmount,
        BigDecimal netAmount) {

    public static EmployeeResponse from(Employee employee) {
        return new EmployeeResponse(
                employee.getId(),
                employee.getEmployeeCode(),
                employee.getFirstName(),
                employee.getLastName(),
                employee.getEmail(),
                employee.getDepartment(),
                employee.getJobTitle(),
                employee.getCountry(),
                employee.getCurrency(),
                employee.getStatus(),
                employee.getDateOfJoining(),
                employee.getBasicAmount(),
                employee.getAllowancesAmount(),
                employee.getDeductionsAmount(),
                employee.getGrossAmount(),
                employee.getNetAmount());
    }
}
