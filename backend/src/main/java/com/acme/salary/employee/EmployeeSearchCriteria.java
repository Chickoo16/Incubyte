package com.acme.salary.employee;

/**
 * Every field is optional (null = "don't filter on this"); {@link EmployeeService#search}
 * passes them straight through to {@link EmployeeRepository#search}.
 */
public record EmployeeSearchCriteria(
        String department,
        String country,
        String jobTitle,
        EmployeeStatus status,
        String query) {
}
