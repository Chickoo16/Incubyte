# ACME Salary Management

Web app for ACME's HR Manager to manage salary data for 10,000 employees across five
countries, and answer questions about how the org pays people. See
[docs/requirements.md](docs/requirements.md) for the full scope and what's deliberately left
out, and [docs/architecture-diagram.svg](docs/architecture-diagram.svg) for the system diagram.

## Stack

- **Backend:** Java 21, Spring Boot 4.1, Spring Data JPA, SQLite (`backend/`)
- **Frontend:** Angular 22, Angular Material (`frontend/`)
- **Tests:** JUnit 5 + Mockito (backend), Angular's built-in Vitest runner (frontend) — see
  [docs/requirements.md](docs/requirements.md) for why these versions rather than the
  Spring Boot 3 / Jasmine-Karma originally assumed.

## Running locally (without Docker)

**Backend** — from `backend/`:

```
./mvnw spring-boot:run
```

Starts on `http://localhost:8080`. On first run it seeds 10,000 employees (takes a few
seconds); the SQLite file lands at `backend/data/salary.db` and is reused on subsequent runs.

**Frontend** — from `frontend/`:

```
npm start
```

Starts on `http://localhost:4200` with `/api/*` proxied to the backend (see
`frontend/proxy.conf.json`) — no CORS setup needed. Run the backend first.

## Running via Docker Compose

```
docker compose up --build
```

Serves the app on `http://localhost:8080` (nginx serving the Angular build, proxying `/api/*`
to the Spring Boot container internally). The SQLite file lives on a named volume
(`salary-data`) so it survives container restarts.

> **Not run-tested in this environment** — Docker isn't functional on the machine this was
> built on (no working `docker` CLI/daemon), so the Compose stack was written and reviewed
> carefully (backend and frontend were each verified independently — see below) but not
> actually run end-to-end through `docker compose up`. Please verify this step on a machine
> with a working Docker install before relying on it.

## Running tests

**Backend** — from `backend/`: `./mvnw test` (70 tests: unit tests for salary/stats
calculation, `@DataJpaTest` repository slice tests, Mockito service tests, `@WebMvcTest`
controller tests).

**Frontend** — from `frontend/`: `npm test` (35 tests: HTTP service tests against a mocked
backend, component logic and rendering tests).

## What was actually verified

Both the backend and frontend were run live (not just their test suites) against real seeded
data during development — see commit history for the two bugs that live testing caught and
fixed (a SQLite startup failure, and a UI currency/precision issue). Docker Compose is the one
piece not run end-to-end, per the note above.

## Manual demo walkthrough

No video is included — recording/narrating video isn't something this environment can produce.
Use this checklist to record one, or to demo it live:

1. Start the backend and frontend (or `docker compose up --build`) and open the app.
2. **Employee directory** (`/`): show the 10,000-row table is paginated and responsive; search
   by name; filter by department/country/job title/status; note the per-row currency next to
   each salary figure.
3. Click a row to open **employee detail**: edit the salary (basic/allowances/deductions),
   watch the gross/net preview update live, save, and see it persist. Deactivate the employee,
   confirm the status flips and the row's filter behavior changes.
4. **Add employee** (`/employees/new`): fill the form, submit, land on the new employee's
   detail page.
5. **Dashboard** (`/dashboard`): show the org-wide headcount card, and point out the mixed
   currency warning replacing a blended average — explain why (five currencies, no FX
   conversion, per `docs/requirements.md`). Filter to a single country and show the average/
   median/min/max become real numbers. Switch the breakdown table between department/country/
   job title, and show that only country rows (single currency) show numeric figures while
   department/job-title rows correctly show "—" for money stats.
