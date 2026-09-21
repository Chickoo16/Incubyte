# Prompt: Architecture Diagram

Used with an AI diagramming tool (e.g. Claude with diagramming support, Mermaid, or
draw.io/Excalidraw via an AI assistant) to produce the system architecture diagram for this
project. Based on `docs/requirements.md`.

---

Create a **C4-style container diagram** for a salary management system, as a single clear
image (SVG or Mermaid). Style: clean boxes and arrows, one legend, no more than ~12 nodes —
this documents an assessment project, not a production system, so keep it readable at a glance
rather than exhaustive.

**Actor**
- HR Manager (single internal user, browser-based) — no other user types.

**Containers / components to show**

1. **Angular SPA** (Angular Material) — runs in the HR Manager's browser. Screens: Employee
   Directory (search/filter/paginate), Employee Detail (profile + salary), Add/Edit Employee,
   Analytics Dashboard.
2. **Nginx** — serves the built Angular static assets and reverse-proxies `/api/*` calls to the
   backend. Entry point of the Docker Compose stack.
3. **Spring Boot REST API** (Java 17) — internally show three layers stacked: Controller layer
   (Employee, Salary, Dashboard/Analytics endpoints) → Service layer (business rules, salary
   gross/net computation, aggregation queries) → Repository layer (Spring Data JPA).
4. **SQLite database** — a single file, mounted as a Docker volume so data survives container
   restarts.
5. **Seed script** — a one-off Spring Boot CLI runner / batch job that populates the database
   with 10,000 employees on first run; show it writing to SQLite, separate from the live
   request path.
6. **Docker Compose boundary** — a dashed box enclosing Nginx+Angular container, the Spring
   Boot container, and the mounted SQLite volume, labeled "single `docker-compose up`, runs
   entirely locally — no external cloud dependency."

**Connections to label**
- HR Manager → Angular SPA: browser (HTTPS/HTTP)
- Angular SPA → Nginx: static asset requests
- Angular SPA → Nginx → Spring Boot API: `/api/*` REST calls (JSON)
- Spring Boot API → SQLite: JDBC (Spring Data JPA)
- Seed script → SQLite: JDBC, one-time/startup population

**Explicitly do not show** (out of scope per requirements — omit entirely rather than graying
out, to keep the diagram uncluttered): auth/identity provider, currency-conversion/FX service,
payroll/tax engine, salary-history store, any external cloud infrastructure.

**Output**: one diagram, HR Manager at the top, request flow going top-to-bottom or
left-to-right, Docker Compose boundary visually distinct (dashed border), a small legend if any
non-obvious notation is used.
