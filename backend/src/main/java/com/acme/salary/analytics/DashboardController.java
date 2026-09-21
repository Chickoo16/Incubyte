package com.acme.salary.analytics;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final AnalyticsService service;

    public DashboardController(AnalyticsService service) {
        this.service = service;
    }

    @GetMapping("/summary")
    public CurrencyAwareStats summary(
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String jobTitle) {
        return service.summary(department, country, jobTitle);
    }

    @GetMapping("/breakdown")
    public List<GroupedSalaryStats> breakdown(@RequestParam GroupDimension by) {
        return service.breakdownBy(by);
    }
}
