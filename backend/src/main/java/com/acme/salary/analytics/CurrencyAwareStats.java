package com.acme.salary.analytics;

/**
 * Salaries are never converted across currencies, so a raw average/median
 * across employees paid in different currencies would be meaningless. This
 * pairs {@link SalaryStats} with the currency they're actually in: a single
 * code when the queried employees share one, "MIXED" when they don't (so a
 * caller can refuse to render it as a plain number), or null when there's
 * no data at all.
 */
public record CurrencyAwareStats(SalaryStats stats, String currency) {

    public static final String MIXED = "MIXED";
}
