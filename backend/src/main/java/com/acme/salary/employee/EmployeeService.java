package com.acme.salary.employee;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class EmployeeService {

    private final EmployeeRepository repository;

    public EmployeeService(EmployeeRepository repository) {
        this.repository = repository;
    }

    public Employee create(NewEmployee request) {
        String employeeCode = "ACME-%06d".formatted(repository.count() + 1);
        Employee employee = new Employee(
                employeeCode, request.firstName(), request.lastName(), request.email(),
                request.department(), request.jobTitle(), request.country(), request.currency(),
                request.dateOfJoining(), request.basic(), request.allowances(), request.deductions());
        return repository.save(employee);
    }

    public Employee getById(Long id) {
        return repository.findById(id).orElseThrow(() -> new EmployeeNotFoundException(id));
    }

    public Employee updateSalary(Long id, BigDecimal basic, BigDecimal allowances, BigDecimal deductions) {
        Employee employee = getById(id);
        employee.updateSalary(basic, allowances, deductions);
        return repository.save(employee);
    }

    public Employee deactivate(Long id) {
        Employee employee = getById(id);
        employee.deactivate();
        return repository.save(employee);
    }

    public Employee reactivate(Long id) {
        Employee employee = getById(id);
        employee.reactivate();
        return repository.save(employee);
    }

    public Page<Employee> search(EmployeeSearchCriteria criteria, Pageable pageable) {
        return repository.search(
                criteria.department(), criteria.country(), criteria.jobTitle(),
                criteria.status(), criteria.query(), pageable);
    }
}
