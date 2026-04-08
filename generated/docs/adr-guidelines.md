# ADR Generation

## Purpose

Architecture Decision Records capture the technical decisions that materially affect implementation, maintainability, and risk.

## When to Create an ADR

Create an ADR when a change involves:

- a new abstraction or architectural boundary
- migration of business logic across layers
- a major toolchain or framework decision
- testing strategy changes with broad impact
- security or compliance-sensitive tradeoffs

## ADR Template

```markdown
# ADR-XXX: Title

## Status
Proposed | Accepted | Superseded

## Context
What problem exists and what constraints apply?

## Decision
What was chosen?

## Options Considered
- Option A
- Option B
- Option C

## Consequences
Positive and negative outcomes of the decision.

## Follow-Up
Implementation or validation work required.
```

## Recommended ADRs For This Project Theme

Examples include:

- migrating SQL business logic into Java calculators
- preserving maker-checker workflow contracts during refactor
- selecting integration and E2E test boundaries
- defining security hooks for change delivery

## Decision Drivers

Document drivers such as:

- maintainability
- compatibility
- performance
- regression risk
- developer velocity
- traceability
- security baseline enforcement

## AI Assistance

AI can help by:

- drafting context and option comparisons
- summarizing tradeoffs
- identifying consequences and follow-up actions
- normalizing ADR language across the project

## Exit Criteria

ADR generation is complete when:

- significant technical choices are recorded
- rejected alternatives are visible
- consequences are documented
- downstream implementation and test implications are clear
