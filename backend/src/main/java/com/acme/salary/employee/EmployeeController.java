package com.acme.salary.employee;

import com.acme.salary.common.PageResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService service;

    public EmployeeController(EmployeeService service) {
        this.service = service;
    }

    @GetMapping
    public PageResponse<EmployeeResponse> search(
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String jobTitle,
            @RequestParam(required = false) EmployeeStatus status,
            @RequestParam(required = false) String query,
            Pageable pageable) {
        EmployeeSearchCriteria criteria = new EmployeeSearchCriteria(department, country, jobTitle, status, query);
        return PageResponse.of(service.search(criteria, pageable), EmployeeResponse::from);
    }

    @GetMapping("/filters")
    public FilterOptions filters() {
        return service.filterOptions();
    }

    @GetMapping("/{id}")
    public EmployeeResponse getById(@PathVariable Long id) {
        return EmployeeResponse.from(service.getById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EmployeeResponse create(@Valid @RequestBody CreateEmployeeRequest request) {
        return EmployeeResponse.from(service.create(request.toNewEmployee()));
    }

    @PutMapping("/{id}/salary")
    public EmployeeResponse updateSalary(@PathVariable Long id, @Valid @RequestBody UpdateSalaryRequest request) {
        return EmployeeResponse.from(
                service.updateSalary(id, request.basic(), request.allowances(), request.deductions()));
    }

    @PostMapping("/{id}/deactivate")
    public EmployeeResponse deactivate(@PathVariable Long id) {
        return EmployeeResponse.from(service.deactivate(id));
    }

    @PostMapping("/{id}/reactivate")
    public EmployeeResponse reactivate(@PathVariable Long id) {
        return EmployeeResponse.from(service.reactivate(id));
    }
}
