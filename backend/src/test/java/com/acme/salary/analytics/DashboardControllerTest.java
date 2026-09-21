package com.acme.salary.analytics;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DashboardController.class)
class DashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AnalyticsService analyticsService;

    @Test
    void summaryFlagsMixedCurrenciesWhenNoFiltersGiven() throws Exception {
        when(analyticsService.summary(isNull(), isNull(), isNull()))
                .thenReturn(new CurrencyAwareStats(new SalaryStats(3, new BigDecimal("30000"),
                        new BigDecimal("70000"), new BigDecimal("50000"), new BigDecimal("50000")), "MIXED"));

        mockMvc.perform(get("/api/dashboard/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stats.count").value(3))
                .andExpect(jsonPath("$.currency").value("MIXED"));
    }

    @Test
    void summaryPassesFiltersThroughAndReportsTheSingleCurrency() throws Exception {
        when(analyticsService.summary(eq("Engineering"), eq("IN"), isNull()))
                .thenReturn(new CurrencyAwareStats(new SalaryStats(1, new BigDecimal("50000"),
                        new BigDecimal("50000"), new BigDecimal("50000"), new BigDecimal("50000")), "INR"));

        mockMvc.perform(get("/api/dashboard/summary")
                        .param("department", "Engineering")
                        .param("country", "IN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stats.count").value(1))
                .andExpect(jsonPath("$.currency").value("INR"));
    }

    @Test
    void breakdownReturnsStatsPerGroupWithCurrency() throws Exception {
        when(analyticsService.breakdownBy(GroupDimension.DEPARTMENT)).thenReturn(List.of(
                new GroupedSalaryStats("Engineering", new CurrencyAwareStats(new SalaryStats(2,
                        new BigDecimal("50000"), new BigDecimal("70000"), new BigDecimal("60000"),
                        new BigDecimal("60000")), "INR"))));

        mockMvc.perform(get("/api/dashboard/breakdown").param("by", "DEPARTMENT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].group").value("Engineering"))
                .andExpect(jsonPath("$[0].stats.stats.average").value(60000.0))
                .andExpect(jsonPath("$[0].stats.currency").value("INR"));
    }
}
