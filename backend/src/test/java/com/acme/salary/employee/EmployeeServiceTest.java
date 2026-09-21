package com.acme.salary.employee;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository repository;

    private EmployeeService service;

    @BeforeEach
    void setUp() {
        service = new EmployeeService(repository);
    }

    private Employee existingEmployee() {
        return new Employee(
                "ACME-000001", "Ada", "Lovelace", "ada@acme.example",
                "Engineering", "Software Engineer", "IN", "INR",
                LocalDate.of(2020, 1, 15),
                new BigDecimal("50000"), new BigDecimal("12000"), new BigDecimal("3000"));
    }

    @Test
    void createGeneratesTheNextSequentialEmployeeCodeAndSaves() {
        when(repository.count()).thenReturn(41L);
        when(repository.save(any(Employee.class))).thenAnswer(invocation -> invocation.getArgument(0));

        NewEmployee request = new NewEmployee(
                "Grace", "Hopper", "grace@acme.example", "Engineering", "Principal Engineer",
                "US", "USD", LocalDate.of(2019, 3, 1),
                new BigDecimal("90000"), new BigDecimal("5000"), new BigDecimal("10000"));

        Employee created = service.create(request);

        assertThat(created.getEmployeeCode()).isEqualTo("ACME-000042");
        assertThat(created.getFirstName()).isEqualTo("Grace");
        assertThat(created.getGrossAmount()).isEqualByComparingTo("95000");
    }

    @Test
    void getByIdReturnsTheEmployeeWhenFound() {
        Employee employee = existingEmployee();
        when(repository.findById(1L)).thenReturn(Optional.of(employee));

        assertThat(service.getById(1L)).isSameAs(employee);
    }

    @Test
    void getByIdThrowsWhenEmployeeDoesNotExist() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById(99L)).isInstanceOf(EmployeeNotFoundException.class);
    }

    @Test
    void updateSalaryLoadsRecomputesAndSaves() {
        Employee employee = existingEmployee();
        when(repository.findById(1L)).thenReturn(Optional.of(employee));
        when(repository.save(employee)).thenReturn(employee);

        Employee updated = service.updateSalary(1L, new BigDecimal("60000"), new BigDecimal("10000"), new BigDecimal("5000"));

        assertThat(updated.getGrossAmount()).isEqualByComparingTo("70000");
        assertThat(updated.getNetAmount()).isEqualByComparingTo("65000");
        verify(repository).save(employee);
    }

    @Test
    void updateSalaryThrowsWhenEmployeeDoesNotExist() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateSalary(99L, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO))
                .isInstanceOf(EmployeeNotFoundException.class);
    }

    @Test
    void deactivateLoadsMutatesAndSaves() {
        Employee employee = existingEmployee();
        when(repository.findById(1L)).thenReturn(Optional.of(employee));
        when(repository.save(employee)).thenReturn(employee);

        Employee result = service.deactivate(1L);

        assertThat(result.getStatus()).isEqualTo(EmployeeStatus.INACTIVE);
        verify(repository).save(employee);
    }

    @Test
    void reactivateLoadsMutatesAndSaves() {
        Employee employee = existingEmployee();
        employee.deactivate();
        when(repository.findById(1L)).thenReturn(Optional.of(employee));
        when(repository.save(employee)).thenReturn(employee);

        Employee result = service.reactivate(1L);

        assertThat(result.getStatus()).isEqualTo(EmployeeStatus.ACTIVE);
        verify(repository).save(employee);
    }

    @Test
    void searchDelegatesToRepositoryWithGivenCriteriaAndPageable() {
        EmployeeSearchCriteria criteria = new EmployeeSearchCriteria(
                "Engineering", "IN", null, EmployeeStatus.ACTIVE, "ada");
        Pageable pageable = PageRequest.of(0, 20);
        Page<Employee> page = new PageImpl<>(java.util.List.of(existingEmployee()));
        when(repository.search("Engineering", "IN", null, EmployeeStatus.ACTIVE, "ada", pageable))
                .thenReturn(page);

        Page<Employee> result = service.search(criteria, pageable);

        assertThat(result).isSameAs(page);
    }

    @Test
    void filterOptionsCollectsDistinctValuesFromTheRepository() {
        when(repository.distinctDepartments()).thenReturn(java.util.List.of("Engineering"));
        when(repository.distinctCountries()).thenReturn(java.util.List.of("IN", "US"));
        when(repository.distinctJobTitles()).thenReturn(java.util.List.of("Engineer"));

        FilterOptions options = service.filterOptions();

        assertThat(options.departments()).containsExactly("Engineering");
        assertThat(options.countries()).containsExactly("IN", "US");
        assertThat(options.jobTitles()).containsExactly("Engineer");
    }

    @Test
    void createPassesAllFieldsThroughToTheNewEmployee() {
        when(repository.count()).thenReturn(0L);
        ArgumentCaptor<Employee> captor = ArgumentCaptor.forClass(Employee.class);
        when(repository.save(captor.capture())).thenAnswer(invocation -> invocation.getArgument(0));

        NewEmployee request = new NewEmployee(
                "Grace", "Hopper", "grace@acme.example", "Engineering", "Principal Engineer",
                "US", "USD", LocalDate.of(2019, 3, 1),
                new BigDecimal("90000"), new BigDecimal("5000"), new BigDecimal("10000"));

        service.create(request);

        Employee saved = captor.getValue();
        assertThat(saved.getLastName()).isEqualTo("Hopper");
        assertThat(saved.getEmail()).isEqualTo("grace@acme.example");
        assertThat(saved.getCountry()).isEqualTo("US");
        assertThat(saved.getCurrency()).isEqualTo("USD");
        assertThat(saved.getDateOfJoining()).isEqualTo(LocalDate.of(2019, 3, 1));
    }
}
