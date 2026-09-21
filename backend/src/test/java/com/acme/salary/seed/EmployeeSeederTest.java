package com.acme.salary.seed;

import com.acme.salary.employee.Employee;
import com.acme.salary.employee.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmployeeSeederTest {

    @Mock
    private EmployeeRepository repository;

    @Test
    void doesNothingWhenEmployeesAlreadyExist() {
        when(repository.count()).thenReturn(1L);
        EmployeeSeeder seeder = new EmployeeSeeder(repository, 10, 3, 42L);

        seeder.run();

        verify(repository, never()).saveAll(anyList());
    }

    @Test
    void seedsTheConfiguredCountInBatches() {
        when(repository.count()).thenReturn(0L);
        EmployeeSeeder seeder = new EmployeeSeeder(repository, 7, 3, 42L);

        seeder.run();

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<Employee>> captor = ArgumentCaptor.forClass(List.class);
        verify(repository, org.mockito.Mockito.times(3)).saveAll(captor.capture());

        List<List<Employee>> batches = captor.getAllValues();
        assertThat(batches).extracting(List::size).containsExactly(3, 3, 1);
        assertThat(batches.stream().mapToInt(List::size).sum()).isEqualTo(7);
    }

    @Test
    void assignsSequentialEmployeeCodesAcrossBatches() {
        when(repository.count()).thenReturn(0L);
        EmployeeSeeder seeder = new EmployeeSeeder(repository, 5, 2, 42L);

        seeder.run();

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<Employee>> captor = ArgumentCaptor.forClass(List.class);
        verify(repository, org.mockito.Mockito.times(3)).saveAll(captor.capture());

        List<String> codes = captor.getAllValues().stream()
                .flatMap(List::stream)
                .map(Employee::getEmployeeCode)
                .toList();

        assertThat(codes).containsExactly(
                "ACME-000001", "ACME-000002", "ACME-000003", "ACME-000004", "ACME-000005");
    }
}
