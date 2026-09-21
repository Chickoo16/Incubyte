package com.acme.salary.employee;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class EmployeeRepositoryTest {

    @Autowired
    private EmployeeRepository repository;

    private Employee employee(String code, String department, String country, String jobTitle,
                               EmployeeStatus status, BigDecimal basic) {
        Employee employee = new Employee(
                code, "First" + code, "Last" + code, code + "@acme.example",
                department, jobTitle, country, "INR",
                LocalDate.of(2021, 1, 1), basic, BigDecimal.ZERO, BigDecimal.ZERO);
        if (status == EmployeeStatus.INACTIVE) {
            employee.deactivate();
        }
        return employee;
    }

    @Test
    void findsAllEmployeesPageByPage() {
        for (int i = 1; i <= 5; i++) {
            repository.save(employee("E00" + i, "Engineering", "IN", "Engineer",
                    EmployeeStatus.ACTIVE, new BigDecimal("50000")));
        }

        var firstPage = repository.findAll(PageRequest.of(0, 2, Sort.by("employeeCode")));

        assertThat(firstPage.getTotalElements()).isEqualTo(5);
        assertThat(firstPage.getContent()).hasSize(2);
        assertThat(firstPage.getContent().get(0).getEmployeeCode()).isEqualTo("E001");
    }

    @Test
    void searchFiltersByDepartmentCountryJobTitleAndStatus() {
        repository.save(employee("E001", "Engineering", "IN", "Engineer",
                EmployeeStatus.ACTIVE, new BigDecimal("50000")));
        repository.save(employee("E002", "Sales", "IN", "Account Executive",
                EmployeeStatus.ACTIVE, new BigDecimal("40000")));
        repository.save(employee("E003", "Engineering", "US", "Engineer",
                EmployeeStatus.ACTIVE, new BigDecimal("90000")));
        repository.save(employee("E004", "Engineering", "IN", "Engineer",
                EmployeeStatus.INACTIVE, new BigDecimal("50000")));

        var result = repository.search("Engineering", "IN", null, EmployeeStatus.ACTIVE, null,
                PageRequest.of(0, 10));

        assertThat(result.getContent())
                .extracting(Employee::getEmployeeCode)
                .containsExactly("E001");
    }

    @Test
    void searchMatchesNameOrEmployeeCodeCaseInsensitively() {
        repository.save(employee("E001", "Engineering", "IN", "Engineer",
                EmployeeStatus.ACTIVE, new BigDecimal("50000")));

        var result = repository.search(null, null, null, null, "firste001", PageRequest.of(0, 10));

        assertThat(result.getContent()).extracting(Employee::getEmployeeCode).containsExactly("E001");
    }

    @Test
    void grossAmountsProjectsOnlyTheGrossColumnForActiveEmployeesMatchingFilters() {
        repository.save(employee("E001", "Engineering", "IN", "Engineer",
                EmployeeStatus.ACTIVE, new BigDecimal("50000")));
        repository.save(employee("E002", "Engineering", "IN", "Engineer",
                EmployeeStatus.ACTIVE, new BigDecimal("70000")));
        repository.save(employee("E003", "Sales", "IN", "Account Executive",
                EmployeeStatus.ACTIVE, new BigDecimal("40000")));
        repository.save(employee("E004", "Engineering", "IN", "Engineer",
                EmployeeStatus.INACTIVE, new BigDecimal("999999")));

        List<BigDecimal> grossAmounts = repository.grossAmounts("Engineering", null, null);

        assertThat(grossAmounts)
                .usingElementComparator(BigDecimal::compareTo)
                .containsExactlyInAnyOrder(new BigDecimal("50000"), new BigDecimal("70000"));
    }
}
