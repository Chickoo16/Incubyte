package com.acme.salary.employee;

import java.util.List;

public record FilterOptions(List<String> departments, List<String> countries, List<String> jobTitles) {
}
