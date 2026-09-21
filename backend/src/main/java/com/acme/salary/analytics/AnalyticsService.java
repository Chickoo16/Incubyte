package com.acme.salary.analytics;

import com.acme.salary.employee.EmployeeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnalyticsService {

    private final EmployeeRepository employeeRepository;

    public AnalyticsService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public CurrencyAwareStats summary(String department, String country, String jobTitle) {
        SalaryStats stats = SalaryStatsCalculator.compute(
                employeeRepository.grossAmounts(department, country, jobTitle));
        return new CurrencyAwareStats(stats, currencyLabel(department, country, jobTitle));
    }

    private String currencyLabel(String department, String country, String jobTitle) {
        List<String> currencies = employeeRepository.distinctCurrencies(department, country, jobTitle);
        return switch (currencies.size()) {
            case 0 -> null;
            case 1 -> currencies.get(0);
            default -> CurrencyAwareStats.MIXED;
        };
    }

    /**
     * One {@link CurrencyAwareStats} per distinct value of the given
     * dimension. A handful of extra queries (one per distinct
     * department/country/job title) rather than a single grouped one -
     * simpler to read, and cheap given the low cardinality of these
     * dimensions.
     */
    public List<GroupedSalaryStats> breakdownBy(GroupDimension dimension) {
        return switch (dimension) {
            case DEPARTMENT -> employeeRepository.distinctDepartments().stream()
                    .map(department -> new GroupedSalaryStats(department, summary(department, null, null)))
                    .toList();
            case COUNTRY -> employeeRepository.distinctCountries().stream()
                    .map(country -> new GroupedSalaryStats(country, summary(null, country, null)))
                    .toList();
            case JOB_TITLE -> employeeRepository.distinctJobTitles().stream()
                    .map(jobTitle -> new GroupedSalaryStats(jobTitle, summary(null, null, jobTitle)))
                    .toList();
        };
    }
}
