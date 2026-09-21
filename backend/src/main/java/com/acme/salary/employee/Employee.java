package com.acme.salary.employee;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * An employee and their current salary. There is deliberately no salary
 * history: {@link #updateSalary} overwrites the previous figures, and gross
 * and net are always derived via {@link SalaryCalculator} rather than
 * accepted as direct input, so they can never drift from the components.
 */
@Entity
@Table(name = "employees")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String employeeCode;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String department;

    @Column(nullable = false)
    private String jobTitle;

    @Column(nullable = false)
    private String country;

    @Column(nullable = false)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EmployeeStatus status;

    @Column(nullable = false)
    private LocalDate dateOfJoining;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal basicAmount;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal allowancesAmount;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal deductionsAmount;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal grossAmount;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal netAmount;

    protected Employee() {
        // JPA
    }

    public Employee(String employeeCode, String firstName, String lastName, String email,
                     String department, String jobTitle, String country, String currency,
                     LocalDate dateOfJoining, BigDecimal basic, BigDecimal allowances, BigDecimal deductions) {
        this.employeeCode = employeeCode;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.department = department;
        this.jobTitle = jobTitle;
        this.country = country;
        this.currency = currency;
        this.dateOfJoining = dateOfJoining;
        this.status = EmployeeStatus.ACTIVE;
        applySalary(SalaryCalculator.compute(basic, allowances, deductions));
    }

    public void updateSalary(BigDecimal basic, BigDecimal allowances, BigDecimal deductions) {
        applySalary(SalaryCalculator.compute(basic, allowances, deductions));
    }

    private void applySalary(SalaryBreakdown breakdown) {
        this.basicAmount = breakdown.basic();
        this.allowancesAmount = breakdown.allowances();
        this.deductionsAmount = breakdown.deductions();
        this.grossAmount = breakdown.gross();
        this.netAmount = breakdown.net();
    }

    public void deactivate() {
        this.status = EmployeeStatus.INACTIVE;
    }

    public void reactivate() {
        this.status = EmployeeStatus.ACTIVE;
    }

    public Long getId() {
        return id;
    }

    public String getEmployeeCode() {
        return employeeCode;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getDepartment() {
        return department;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public String getCountry() {
        return country;
    }

    public String getCurrency() {
        return currency;
    }

    public EmployeeStatus getStatus() {
        return status;
    }

    public LocalDate getDateOfJoining() {
        return dateOfJoining;
    }

    public BigDecimal getBasicAmount() {
        return basicAmount;
    }

    public BigDecimal getAllowancesAmount() {
        return allowancesAmount;
    }

    public BigDecimal getDeductionsAmount() {
        return deductionsAmount;
    }

    public BigDecimal getGrossAmount() {
        return grossAmount;
    }

    public BigDecimal getNetAmount() {
        return netAmount;
    }
}
