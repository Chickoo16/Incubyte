package com.acme.salary.seed;

import com.acme.salary.employee.NewEmployee;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Produces realistic-looking (but entirely synthetic) employee records for
 * seeding: five countries in their own currency, seven departments each with
 * a five-level job ladder, and salaries that scale with level and country so
 * the analytics dashboard shows believable variance instead of noise.
 * Deterministic for a given seed, so seeding is reproducible.
 */
public class EmployeeDataGenerator {

    private record Country(String code, String currency, BigDecimal levelZeroBasic, BigDecimal levelStep) {
    }

    private record Department(String name, List<String> jobTitlesByLevel) {
    }

    private static final List<Country> COUNTRIES = List.of(
            new Country("IN", "INR", new BigDecimal("450000"), new BigDecimal("1.6")),
            new Country("US", "USD", new BigDecimal("55000"), new BigDecimal("1.55")),
            new Country("UK", "GBP", new BigDecimal("30000"), new BigDecimal("1.5")),
            new Country("DE", "EUR", new BigDecimal("42000"), new BigDecimal("1.5")),
            new Country("SG", "SGD", new BigDecimal("48000"), new BigDecimal("1.55")));

    private static final List<Department> DEPARTMENTS = List.of(
            new Department("Engineering", List.of(
                    "Associate Engineer", "Software Engineer", "Senior Software Engineer",
                    "Engineering Manager", "Director of Engineering")),
            new Department("Sales", List.of(
                    "Sales Associate", "Account Executive", "Senior Account Executive",
                    "Sales Manager", "VP of Sales")),
            new Department("Marketing", List.of(
                    "Marketing Associate", "Marketing Specialist", "Senior Marketing Specialist",
                    "Marketing Manager", "Director of Marketing")),
            new Department("Human Resources", List.of(
                    "HR Associate", "HR Generalist", "Senior HR Generalist",
                    "HR Manager", "Director of HR")),
            new Department("Finance", List.of(
                    "Finance Associate", "Financial Analyst", "Senior Financial Analyst",
                    "Finance Manager", "Director of Finance")),
            new Department("Operations", List.of(
                    "Operations Associate", "Operations Analyst", "Senior Operations Analyst",
                    "Operations Manager", "Director of Operations")),
            new Department("Customer Support", List.of(
                    "Support Associate", "Support Specialist", "Senior Support Specialist",
                    "Support Manager", "Director of Support")));

    /** Weighted so the org looks like a pyramid rather than evenly split levels. */
    private static final double[] LEVEL_WEIGHTS = {0.40, 0.30, 0.18, 0.09, 0.03};

    private static final List<String> FIRST_NAMES = List.of(
            "Ada", "Grace", "Alan", "Linus", "Margaret", "Katherine", "Dennis", "Barbara",
            "Tim", "Radia", "Guido", "Anita", "Ken", "Frances", "John", "Edsger",
            "Donald", "Shafi", "Vint", "Sophie", "Brian", "Karen", "James", "Susan",
            "Robert", "Marissa", "David", "Elena", "Michael", "Priya");

    private static final List<String> LAST_NAMES = List.of(
            "Lovelace", "Hopper", "Turing", "Torvalds", "Hamilton", "Johnson", "Ritchie", "Liskov",
            "Berners-Lee", "Perlman", "Van Rossum", "Borg", "Thompson", "Allen", "Carmack", "Dijkstra",
            "Knuth", "Goldwasser", "Cerf", "Wilson", "Kernighan", "Spärck Jones", "Gosling", "Wojcicki",
            "Cocke", "Mayer", "Patterson", "Ivanova", "Stonebraker", "Sharma");

    private static final LocalDate EARLIEST_JOINING = LocalDate.of(2015, 1, 1);
    private static final LocalDate LATEST_JOINING = LocalDate.now().minusDays(7);

    public List<NewEmployee> generate(int count, long seed) {
        Random random = new Random(seed);
        List<NewEmployee> employees = new ArrayList<>(count);

        for (int i = 0; i < count; i++) {
            Country country = COUNTRIES.get(random.nextInt(COUNTRIES.size()));
            Department department = DEPARTMENTS.get(random.nextInt(DEPARTMENTS.size()));
            int level = pickLevel(random);

            BigDecimal basic = country.levelZeroBasic()
                    .multiply(country.levelStep().pow(level))
                    .multiply(randomFactor(random, 0.9, 1.1))
                    .setScale(2, RoundingMode.HALF_UP);
            BigDecimal allowances = basic.multiply(randomFactor(random, 0.15, 0.30))
                    .setScale(2, RoundingMode.HALF_UP);
            BigDecimal deductions = basic.add(allowances).multiply(randomFactor(random, 0.05, 0.15))
                    .setScale(2, RoundingMode.HALF_UP);

            String firstName = FIRST_NAMES.get(random.nextInt(FIRST_NAMES.size()));
            String lastName = LAST_NAMES.get(random.nextInt(LAST_NAMES.size()));
            String email = "%s.%s%d@acme.example".formatted(
                    firstName.toLowerCase(), lastName.toLowerCase().replace(" ", "").replace("'", ""), i + 1);

            employees.add(new NewEmployee(
                    firstName, lastName, email, department.name(), department.jobTitlesByLevel().get(level),
                    country.code(), country.currency(), randomJoiningDate(random),
                    basic, allowances, deductions));
        }

        return employees;
    }

    private int pickLevel(Random random) {
        double draw = random.nextDouble();
        double cumulative = 0.0;
        for (int level = 0; level < LEVEL_WEIGHTS.length; level++) {
            cumulative += LEVEL_WEIGHTS[level];
            if (draw < cumulative) {
                return level;
            }
        }
        return LEVEL_WEIGHTS.length - 1;
    }

    private BigDecimal randomFactor(Random random, double min, double max) {
        double value = min + (max - min) * random.nextDouble();
        return BigDecimal.valueOf(value);
    }

    private LocalDate randomJoiningDate(Random random) {
        long dayRange = EARLIEST_JOINING.until(LATEST_JOINING, java.time.temporal.ChronoUnit.DAYS);
        long offset = (long) (random.nextDouble() * dayRange);
        return EARLIEST_JOINING.plusDays(offset);
    }
}
