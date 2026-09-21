# Salary Management Software — Requirements

## Goal
Give ACME's HR Manager a web application to manage salary data for **10,000 employees across
multiple countries**, replacing spreadsheets, and to let them answer questions about how the
org pays people (headcount and pay by department, country, job title, etc.).

## User Persona
HR Manager — a single internal, trusted user. No self-service employee portal, no multi-role
access control in this version.

## In Scope

**Employee directory**
- Searchable, filterable (department, country, job title, status), paginated list — must stay
  responsive at 10,000 rows.
- Employee detail view showing profile + current salary structure.
- Create new employee (with initial salary structure). Deactivate/reactivate employee
  (soft delete — history stays intact for reporting).

**Salary management**
- Structured salary per employee: `basic + allowances − deductions = net`, `basic + allowances
  = gross`. Gross/net are computed, not hand-entered, to keep the numbers trustworthy.
- HR can edit an employee's salary components; the record always reflects the *current* salary
  only (no versioned history — see "Out of scope").
- Each employee has a `country` and a `currency` (e.g. IN/INR, US/USD) as descriptive attributes.

**"How does the org pay people" — analytics dashboard**
- Org-wide stats: headcount, average/median/min/max gross salary.
- The same stats sliceable by department, country, and job title.
- A fixed, purpose-built dashboard (not an ad-hoc query builder) — bounded scope, easy to test.

**Seeding**
- A script that generates 10,000 employees with realistic spread across ~5 countries, several
  departments, and job levels whose salary bands differ believably (so the dashboard shows
  meaningful variance, not noise).

## Deliberately Out of Scope (and why)

- **Authentication / roles.** Single trusted HR persona, as stated in the brief. Login/RBAC is
  orthogonal complexity that would eat time better spent on the salary domain and its tests.
- **Cross-country currency conversion.** Country/currency are stored as attributes for grouping
  and filtering; the dashboard does not convert to a single reporting currency. Real FX
  requires rate sourcing and staleness handling that's a separate problem from salary
  management.
- **Payroll processing, payslip generation, tax/statutory compliance.** These rules differ
  materially per country and are a domain of their own; this system stores and reports salary
  figures, it doesn't compute take-home pay after tax.
- **Salary history / revision audit trail.** Only the current salary is tracked; edits
  overwrite. Keeps the data model and queries simple. Noted as natural follow-up work.
- **Org hierarchy, leave/attendance, bank details, national ID.** Not needed to answer "how are
  people paid" and would add sensitive-PII handling scope without serving the stated goal.
- **Bulk CSV import/export.** The seed script covers the one bulk-load scenario (initial
  10,000 employees); ad-hoc import/export is a nice-to-have, not core.
- **Live public cloud deployment.** Delivered as a one-command `docker-compose up` stack
  (backend + frontend, SQLite as a mounted file) that runs the full system end-to-end locally.
  No cloud provider credentials are available in this environment to stand up a live URL; the
  stack is deploy-ready for whichever host is chosen.
- **Screen-recorded video demo.** Recording/narrating video isn't something this environment
  can produce; the repo will include a demo script/checklist the candidate can record against.

## Technical Approach
- **Backend:** Java 17, Spring Boot 3, Spring Data JPA, SQLite (file-based, zero infra to run).
- **Frontend:** Angular, Angular Material, server-side pagination against the API.
- **Testing (TDD):** JUnit 5 + Mockito for backend unit tests (salary calculation logic first,
  as pure functions), slice tests (`@DataJpaTest`, `@WebMvcTest`) over full context boots for
  speed; Jasmine/Karma for Angular component/service tests. Tests are written before the
  implementation they cover, and commits show red → green progression.
- **Packaging:** Docker Compose bundling backend (Spring Boot jar) and frontend (Angular build
  behind nginx), single command to run the whole stack.
