package com.acme.salary.analytics;

import com.acme.salary.employee.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AnalyticsServiceTest {

    @Mock
    private EmployeeRepository repository;

    private AnalyticsService service;

    @BeforeEach
    void setUp() {
        service = new AnalyticsService(repository);
    }

    @Test
    void summaryComputesStatsFromFilteredGrossAmounts() {
        when(repository.grossAmounts("Engineering", "IN", null))
                .thenReturn(List.of(new BigDecimal("50000"), new BigDecimal("70000")));
        when(repository.distinctCurrencies("Engineering", "IN", null)).thenReturn(List.of("INR"));

        CurrencyAwareStats result = service.summary("Engineering", "IN", null);

        assertThat(result.stats().count()).isEqualTo(2);
        assertThat(result.stats().average()).isEqualByComparingTo("60000");
        assertThat(result.currency()).isEqualTo("INR");
    }

    @Test
    void summaryFlagsMixedCurrenciesInsteadOfBlendingThem() {
        when(repository.grossAmounts(null, null, null))
                .thenReturn(List.of(new BigDecimal("50000"), new BigDecimal("70000")));
        when(repository.distinctCurrencies(null, null, null)).thenReturn(List.of("INR", "USD"));

        CurrencyAwareStats result = service.summary(null, null, null);

        assertThat(result.currency()).isEqualTo("MIXED");
    }

    @Test
    void summaryReturnsNullCurrencyWhenThereIsNoData() {
        when(repository.grossAmounts(null, "FR", null)).thenReturn(List.of());
        when(repository.distinctCurrencies(null, "FR", null)).thenReturn(List.of());

        CurrencyAwareStats result = service.summary(null, "FR", null);

        assertThat(result.currency()).isNull();
        assertThat(result.stats().count()).isZero();
    }

    @Test
    void breakdownByDepartmentReturnsOneStatsEntryPerDistinctDepartment() {
        when(repository.distinctDepartments()).thenReturn(List.of("Engineering", "Sales"));
        when(repository.grossAmounts(eq("Engineering"), isNull(), isNull()))
                .thenReturn(List.of(new BigDecimal("50000"), new BigDecimal("70000")));
        when(repository.distinctCurrencies(eq("Engineering"), isNull(), isNull())).thenReturn(List.of("INR"));
        when(repository.grossAmounts(eq("Sales"), isNull(), isNull()))
                .thenReturn(List.of(new BigDecimal("40000")));
        when(repository.distinctCurrencies(eq("Sales"), isNull(), isNull())).thenReturn(List.of("INR"));

        List<GroupedSalaryStats> breakdown = service.breakdownBy(GroupDimension.DEPARTMENT);

        assertThat(breakdown).extracting(GroupedSalaryStats::group).containsExactly("Engineering", "Sales");
        assertThat(breakdown.get(0).stats().stats().average()).isEqualByComparingTo("60000");
        assertThat(breakdown.get(1).stats().stats().average()).isEqualByComparingTo("40000");
    }

    @Test
    void breakdownByCountryUsesDistinctCountriesAndIsSingleCurrencyPerRow() {
        when(repository.distinctCountries()).thenReturn(List.of("IN"));
        when(repository.grossAmounts(isNull(), eq("IN"), isNull()))
                .thenReturn(List.of(new BigDecimal("55000")));
        when(repository.distinctCurrencies(isNull(), eq("IN"), isNull())).thenReturn(List.of("INR"));

        List<GroupedSalaryStats> breakdown = service.breakdownBy(GroupDimension.COUNTRY);

        assertThat(breakdown).extracting(GroupedSalaryStats::group).containsExactly("IN");
        assertThat(breakdown.get(0).stats().currency()).isEqualTo("INR");
    }

    @Test
    void breakdownByJobTitleUsesDistinctJobTitles() {
        when(repository.distinctJobTitles()).thenReturn(List.of("Engineer"));
        when(repository.grossAmounts(isNull(), isNull(), eq("Engineer")))
                .thenReturn(List.of(new BigDecimal("65000")));
        when(repository.distinctCurrencies(isNull(), isNull(), eq("Engineer"))).thenReturn(List.of("USD", "INR"));

        List<GroupedSalaryStats> breakdown = service.breakdownBy(GroupDimension.JOB_TITLE);

        assertThat(breakdown).extracting(GroupedSalaryStats::group).containsExactly("Engineer");
        assertThat(breakdown.get(0).stats().currency()).isEqualTo("MIXED");
    }
}
