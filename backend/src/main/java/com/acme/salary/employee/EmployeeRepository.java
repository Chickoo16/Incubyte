package com.acme.salary.employee;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    @Query("""
            SELECT e FROM Employee e
            WHERE (:department IS NULL OR e.department = :department)
              AND (:country IS NULL OR e.country = :country)
              AND (:jobTitle IS NULL OR e.jobTitle = :jobTitle)
              AND (:status IS NULL OR e.status = :status)
              AND (:query IS NULL
                   OR LOWER(e.firstName) LIKE LOWER(CONCAT('%', :query, '%'))
                   OR LOWER(e.lastName) LIKE LOWER(CONCAT('%', :query, '%'))
                   OR LOWER(e.employeeCode) LIKE LOWER(CONCAT('%', :query, '%')))
            """)
    Page<Employee> search(@Param("department") String department,
                           @Param("country") String country,
                           @Param("jobTitle") String jobTitle,
                           @Param("status") EmployeeStatus status,
                           @Param("query") String query,
                           Pageable pageable);

    /**
     * Gross salary of active employees matching the given filters, as a bare
     * projection — the dashboard needs only this column, not whole entities,
     * to compute avg/median/min/max over up to 10,000 rows.
     */
    @Query("""
            SELECT e.grossAmount FROM Employee e
            WHERE e.status = com.acme.salary.employee.EmployeeStatus.ACTIVE
              AND (:department IS NULL OR e.department = :department)
              AND (:country IS NULL OR e.country = :country)
              AND (:jobTitle IS NULL OR e.jobTitle = :jobTitle)
            """)
    List<BigDecimal> grossAmounts(@Param("department") String department,
                                   @Param("country") String country,
                                   @Param("jobTitle") String jobTitle);
}
