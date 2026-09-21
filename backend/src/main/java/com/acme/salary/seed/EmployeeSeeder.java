package com.acme.salary.seed;

import com.acme.salary.employee.Employee;
import com.acme.salary.employee.EmployeeRepository;
import com.acme.salary.employee.NewEmployee;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Populates the database with synthetic employees on first run only - if
 * any employee already exists, seeding is skipped so restarts don't pile up
 * duplicates. Saves in batches rather than one list of 10,000, so a single
 * SQLite transaction/statement never has to hold the whole dataset.
 */
@Component
public class EmployeeSeeder implements CommandLineRunner {

    private final EmployeeRepository repository;
    private final EmployeeDataGenerator generator;
    private final int totalEmployees;
    private final int batchSize;
    private final long randomSeed;

    public EmployeeSeeder(
            EmployeeRepository repository,
            @Value("${salary.seed.count:10000}") int totalEmployees,
            @Value("${salary.seed.batch-size:500}") int batchSize,
            @Value("${salary.seed.random-seed:20260101}") long randomSeed) {
        this.repository = repository;
        this.generator = new EmployeeDataGenerator();
        this.totalEmployees = totalEmployees;
        this.batchSize = batchSize;
        this.randomSeed = randomSeed;
    }

    @Override
    public void run(String... args) {
        if (repository.count() > 0) {
            return;
        }

        List<NewEmployee> data = generator.generate(totalEmployees, randomSeed);

        for (int start = 0; start < data.size(); start += batchSize) {
            int end = Math.min(start + batchSize, data.size());
            List<Employee> batch = new ArrayList<>(end - start);
            for (int i = start; i < end; i++) {
                batch.add(toEmployee(i, data.get(i)));
            }
            repository.saveAll(batch);
        }
    }

    private Employee toEmployee(int index, NewEmployee data) {
        String employeeCode = "ACME-%06d".formatted(index + 1);
        return new Employee(
                employeeCode, data.firstName(), data.lastName(), data.email(),
                data.department(), data.jobTitle(), data.country(), data.currency(),
                data.dateOfJoining(), data.basic(), data.allowances(), data.deductions());
    }
}
