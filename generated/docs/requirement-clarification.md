# Requirement Clarification

## Purpose

This stage converts raw requests into clear, testable, and implementation-ready statements.

## Clarification Goals

A clarified requirement should answer:

- what must change
- what must not change
- who is affected
- how success will be measured
- what constraints must be preserved

## Clarification Workflow

1. Review the original intake.
2. Split compound requests into atomic requirements.
3. Identify missing definitions, actors, edge cases, and acceptance boundaries.
4. Convert ambiguous language into observable outcomes.
5. Distinguish scope, non-goals, and assumptions.

## Clarification Questions

Use targeted questions such as:

- Which users or roles are affected?
- Which screens, APIs, or reports are in scope?
- What existing behavior must remain unchanged?
- What technical constraints are mandatory?
- What output parity or approval flow conditions must be preserved?
- What evidence will count as successful delivery?

## Clarified Requirement Format

| Field | Description |
|------|------|
| Requirement ID | Stable identifier |
| Statement | Clear actionable requirement |
| Rationale | Why it matters |
| Acceptance Criteria | Observable pass conditions |
| Constraints | Mandatory boundaries |
| Dependencies | Systems, docs, or prior decisions |
| Non-Goals | Explicitly excluded scope |

## Example Clarified Statements

- Report execution for migrated built-in reports must be performed by Java service-layer calculators instead of embedded SQL execution paths.
- Existing maker and checker permissions must continue to control submission and approval behavior.
- Exported report output must continue to use the same templates and business outcomes as before the refactor.
- The backend implementation must remain compatible with Java 17.

## AI Assistance

AI can help by:

- rewriting ambiguous statements into testable language
- proposing acceptance criteria
- surfacing contradictions between constraints and scope
- generating clarification question sets

## Exit Criteria

Clarification is complete when:

- each requirement is actionable
- acceptance criteria exist
- constraints are explicit
- non-goals are recorded
- ambiguity is reduced to acceptable implementation risk
