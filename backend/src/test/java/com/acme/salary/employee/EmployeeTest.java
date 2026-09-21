package com.acme.salary.employee;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

class EmployeeTest {

    private Employee newEmployee() {
        return new Employee(
                "ACME-000001", "Ada", "Lovelace", "ada@acme.example",
                "Engineering", "Software Engineer", "IN", "INR",
                LocalDate.of(2020, 1, 15),
                new BigDecimal("50000"), new BigDecimal("12000"), new BigDecimal("3000"));
    }

    @Test
    void computesGrossAndNetFromSalaryComponentsOnCreation() {
        Employee employee = newEmployee();

        assertThat(employee.getGrossAmount()).isEqualByComparingTo("62000");
        assertThat(employee.getNetAmount()).isEqualByComparingTo("59000");
    }

    @Test
    void startsActive() {
        assertThat(newEmployee().getStatus()).isEqualTo(EmployeeStatus.ACTIVE);
    }

    @Test
    void deactivateSetsStatusToInactive() {
        Employee employee = newEmployee();

        employee.deactivate();

        assertThat(employee.getStatus()).isEqualTo(EmployeeStatus.INACTIVE);
    }

    @Test
    void reactivateSetsStatusBackToActive() {
        Employee employee = newEmployee();
        employee.deactivate();

        employee.reactivate();

        assertThat(employee.getStatus()).isEqualTo(EmployeeStatus.ACTIVE);
    }

    @Test
    void updateSalaryRecomputesGrossAndNet() {
        Employee employee = newEmployee();

        employee.updateSalary(new BigDecimal("60000"), new BigDecimal("10000"), new BigDecimal("5000"));

        assertThat(employee.getBasicAmount()).isEqualByComparingTo("60000");
        assertThat(employee.getAllowancesAmount()).isEqualByComparingTo("10000");
        assertThat(employee.getDeductionsAmount()).isEqualByComparingTo("5000");
        assertThat(employee.getGrossAmount()).isEqualByComparingTo("70000");
        assertThat(employee.getNetAmount()).isEqualByComparingTo("65000");
    }

    @Test
    void updateSalaryRejectsInvalidComponentsWithoutChangingState() {
        Employee employee = newEmployee();

        assertThatIllegalArgumentException()
                .isThrownBy(() -> employee.updateSalary(new BigDecimal("-1"), BigDecimal.ZERO, BigDecimal.ZERO));

        assertThat(employee.getBasicAmount()).isEqualByComparingTo("50000");
    }
}
