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

    /**
     * Which currencies the matching active employees are paid in. Salaries
     * are never converted across currencies (see requirements), so callers
     * use this to tell a meaningful single-currency figure apart from one
     * that silently blends incompatible currencies.
     */
    @Query("""
            SELECT DISTINCT e.currency FROM Employee e
            WHERE e.status = com.acme.salary.employee.EmployeeStatus.ACTIVE
              AND (:department IS NULL OR e.department = :department)
              AND (:country IS NULL OR e.country = :country)
              AND (:jobTitle IS NULL OR e.jobTitle = :jobTitle)
            """)
    List<String> distinctCurrencies(@Param("department") String department,
                                     @Param("country") String country,
                                     @Param("jobTitle") String jobTitle);

    @Query("""
            SELECT DISTINCT e.department FROM Employee e
            WHERE e.status = com.acme.salary.employee.EmployeeStatus.ACTIVE
            ORDER BY e.department
            """)
    List<String> distinctDepartments();

    @Query("""
            SELECT DISTINCT e.country FROM Employee e
            WHERE e.status = com.acme.salary.employee.EmployeeStatus.ACTIVE
            ORDER BY e.country
            """)
    List<String> distinctCountries();

    @Query("""
            SELECT DISTINCT e.jobTitle FROM Employee e
            WHERE e.status = com.acme.salary.employee.EmployeeStatus.ACTIVE
            ORDER BY e.jobTitle
            """)
    List<String> distinctJobTitles();
}
