package com.acme.salary.seed;

import com.acme.salary.employee.Employee;
import com.acme.salary.employee.NewEmployee;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class EmployeeDataGeneratorTest {

    private final EmployeeDataGenerator generator = new EmployeeDataGenerator();

    @Test
    void generatesExactlyTheRequestedCount() {
        List<NewEmployee> employees = generator.generate(250, 42L);

        assertThat(employees).hasSize(250);
    }

    @Test
    void everyEmailIsUnique() {
        List<NewEmployee> employees = generator.generate(2000, 42L);

        Set<String> emails = employees.stream().map(NewEmployee::email).collect(Collectors.toSet());

        assertThat(emails).hasSize(2000);
    }

    @Test
    void coversAllFiveCountriesGivenEnoughSamples() {
        List<NewEmployee> employees = generator.generate(1000, 42L);

        Set<String> countries = employees.stream().map(NewEmployee::country).collect(Collectors.toSet());

        assertThat(countries).containsExactlyInAnyOrder("IN", "US", "UK", "DE", "SG");
    }

    @Test
    void coversAllSevenDepartmentsGivenEnoughSamples() {
        List<NewEmployee> employees = generator.generate(1000, 42L);

        Set<String> departments = employees.stream().map(NewEmployee::department).collect(Collectors.toSet());

        assertThat(departments).hasSize(7);
    }

    @Test
    void everyGeneratedEmployeeHasAValidSalaryStructure() {
        List<NewEmployee> employees = generator.generate(500, 42L);

        assertThatCode(() -> employees.forEach(e -> new Employee(
                "ACME-000001", e.firstName(), e.lastName(), e.email(), e.department(), e.jobTitle(),
                e.country(), e.currency(), e.dateOfJoining(), e.basic(), e.allowances(), e.deductions())))
                .doesNotThrowAnyException();
    }

    @Test
    void dateOfJoiningIsAPlausiblePastDate() {
        List<NewEmployee> employees = generator.generate(500, 42L);

        assertThat(employees).allSatisfy(e -> {
            assertThat(e.dateOfJoining()).isAfter(LocalDate.of(2014, 12, 31));
            assertThat(e.dateOfJoining()).isBefore(LocalDate.now());
        });
    }

    @Test
    void isDeterministicForTheSameSeed() {
        List<NewEmployee> first = generator.generate(300, 7L);
        List<NewEmployee> second = generator.generate(300, 7L);

        assertThat(first).isEqualTo(second);
    }

    @Test
    void differentSeedsProduceDifferentData() {
        List<NewEmployee> first = generator.generate(300, 7L);
        List<NewEmployee> second = generator.generate(300, 8L);

        assertThat(first).isNotEqualTo(second);
    }
}
