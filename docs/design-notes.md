# Design Notes & Tradeoffs

Decisions made while building, and why — beyond what's already in
[requirements.md](requirements.md). Roughly chronological.

## Gross/net are always computed, never stored as direct input
`Employee.updateSalary()` and the create constructor both route through `SalaryCalculator`,
which derives gross (`basic + allowances`) and net (`gross - deductions`) and rejects
deductions that would push net negative. There is no code path that lets gross/net be set
independently of the components — a stored salary can't silently drift from the numbers that
produced it.

## Salary history is not versioned
Editing a salary overwrites the previous figures. Chosen deliberately (see requirements.md) to
keep the data model and queries simple; noted there as natural follow-up work rather than
something quietly skipped.

## Dashboard math: SQL for aggregates it's good at, Java for the one it isn't
`AVG`/`MIN`/`MAX`/count are computed in SQL via a single filtered projection query
(`EmployeeRepository.grossAmounts`). Median has no standard SQL equivalent (SQLite included),
so `SalaryStatsCalculator` sorts the (at most 10,000) values in Java and picks the middle. This
is a deliberate split, not an oversight — pushing everything to Java would work but discards
what the database does well; pushing median into SQL would mean a non-portable window-function
hack for one figure.

## Currency is never converted, and the API says so explicitly
`docs/requirements.md` scopes out cross-country FX conversion. The first version of the
analytics endpoints didn't say anything about that limitation — a plain `average` field looked
just as trustworthy whether it came from one currency or was silently blending five. A live
smoke test against the real seed data caught this (the org-wide average was ₹283,991-ish,
nominally, but meaningless): `CurrencyAwareStats` now wraps every stats payload with the
currency it's actually in, or `"MIXED"` when the query spans more than one. The frontend
dashboard renders a warning instead of a number when currency is `"MIXED"`, and the breakdown
table shows `—` instead of a blended figure on a per-row basis (breaking down by department or
job title still spans countries, so most rows there are legitimately mixed — only the
country-breakdown rows are naturally single-currency).

## Breakdown queries: N+1 over a grouped query
`AnalyticsService.breakdownBy()` runs one query per distinct department/country/job title
rather than a single `GROUP BY`. With cardinality in the single digits to low teens per
dimension, this is a handful of fast queries, not a performance problem, and it reads as plain
reuse of the same `summary()` path used elsewhere — a grouped query would need its own
median-in-Java handling duplicated or awkwardly generalized.

## SQLite: one Hikari connection, not a pool
SQLite serializes writes regardless of how many connections you hand it; a normal-sized
connection pool just means most connections queue for the same underlying write lock while
looking free to the application. `maximum-pool-size=1` makes that constraint visible instead of
papering over it with retries.

## Seed data is deterministic, not random-random
`EmployeeDataGenerator.generate(count, seed)` uses a seeded `Random`, so the same seed always
produces the same 10,000 employees. That made the generator itself testable (assert exact
output for a given seed) and makes a fresh clone's seeded data reproducible rather than a new
random dataset every time someone runs it.

## Versions: current stable, not what was assumed going in
The brief was written assuming Spring Boot 3 and Jasmine/Karma; by the time this was built,
Spring Boot 4.1 and Angular's built-in Vitest runner were current stable. Both were adopted
rather than deliberately targeting older versions, and `requirements.md` was corrected to
match — using stale-but-familiar versions for a project starting today isn't a tradeoff worth
making.

## Two bugs a browser catches that unit tests don't
Both found by actually running the app against the real seeded data, not by the test suites
(which were passing throughout):
- SQLite failed to start on a fresh checkout because the driver creates the database file but
  not missing parent directories. Fixed in `SalaryManagementApplication.main()`.
- The salary edit preview displayed values like `110614.20999999999` — plain JS float
  arithmetic on real decimal salaries, invisible with the round numbers unit tests tend to use.
  Fixed by formatting the preview through `DecimalPipe`; the actual save path was never
  affected since it sends the raw components and the backend recomputes with `BigDecimal`.

## Docker Compose is written but not run-tested here
No working Docker install in this environment (see README). The stack was reviewed carefully
and both halves were verified independently (backend run live against real SQLite, frontend
built and run against the real backend through its dev proxy — which exercises the same
same-origin `/api/*` request path that nginx uses in the container), but `docker compose up`
itself has not been executed.
