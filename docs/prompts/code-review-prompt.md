# Prompt: Thorough Code Review

Used with an AI reviewer (or as a manual checklist) to run a rigorous, defect-focused review of
a diff or PR before merge — beyond a summary of what the code does.

---

Review the following code changes with the rigor of a senior engineer doing a pre-merge review.
Do not just summarize what the code does — actively look for defects.

**Scope**: [paste diff / PR link / file paths here]

For each issue found, report:
- **File:line** reference
- **Severity**: blocker / major / minor / nit
- **What's wrong** (one sentence)
- **Concrete failure scenario**: specific input/state that triggers it, or why it's wrong even
  without a trigger

Check systematically across these categories:

1. **Correctness** — logic errors, off-by-one, wrong operator, incorrect boolean conditions,
   mishandled null/undefined/empty cases, race conditions, incorrect assumptions about ordering
   or state.
2. **Edge cases** — empty inputs, zero, negative numbers, max/overflow values, duplicate
   entries, concurrent access, malformed/unexpected data shapes.
3. **Error handling** — swallowed exceptions, missing try/catch where I/O or parsing can fail,
   errors that fail silently instead of surfacing, overly broad catches that hide real bugs.
4. **Security** — injection (SQL/command/XSS), unvalidated input crossing a trust boundary,
   secrets/credentials in code, unsafe deserialization, missing authz checks, path traversal.
5. **Data integrity** — floating-point/precision issues in money or measurement code, mutation
   of shared state, inconsistent units or currencies, off-by-one in pagination/indexing.
6. **Tests** — do tests exist for the changed behavior? Do they test the actual edge cases
   above, or just the happy path? Any assertions that can't actually fail?
7. **Consistency & reuse** — duplicated logic that should call an existing function, deviations
   from patterns already established elsewhere in the codebase.
8. **Simplicity** — unnecessary abstraction, dead code, over-engineering for a case that can't
   happen.

Rules:
- Only report things you've verified by reading the actual code — no speculative "this might be
  an issue."
- Rank findings most-severe first.
- If nothing is wrong in a category, say so briefly rather than omitting it — that's a signal
  the category was checked, not skipped.
- Do not suggest stylistic changes unrelated to correctness unless asked.
