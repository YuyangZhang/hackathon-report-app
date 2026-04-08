# Test Generation

## Purpose

This stage transforms clarified requirements and ADR outcomes into a concrete validation strategy.

## Testing Objectives

The generated tests should:

- prove functional correctness
- protect against regressions
- validate preserved behavior
- verify role and permission boundaries
- confirm system outputs remain consistent where parity is required

## Test Planning Structure

| Layer | Goal | Example Targets |
|------|------|------|
| Unit | Validate isolated logic | calculators, helpers, validators |
| Integration | Validate service and persistence interactions | report execution services, approval flow |
| API | Validate endpoint contracts | auth, report, run approval, export |
| E2E | Validate user-visible workflows | maker report generation, checker approval |

## Requirement-to-Test Traceability

Each clarified requirement should map to:

- at least one primary test
- any required regression suite
- relevant negative-path checks
- supporting data or fixtures

## Test Case Template

| Field | Description |
|------|------|
| Test ID | Stable identifier |
| Requirement IDs | Linked clarified requirements |
| Layer | Unit, integration, API, E2E |
| Scenario | What is being validated |
| Preconditions | Data, role, or environment setup |
| Steps | Ordered actions |
| Expected Result | Observable outcome |
| Regression Risk | What existing behavior this protects |

## Example Test Areas For The Report App

- built-in report calculations produce expected aggregates
- explicit SQL-backed report execution routes to migrated calculators when applicable
- maker users can submit report runs
- checker users can approve or reject submitted runs
- audit trail events are recorded across workflow transitions
- report export still uses the expected execution and template path

## AI Assistance

AI can help by:

- generating test matrices from clarified requirements
- proposing edge cases and failure scenarios
- organizing coverage by test layer
- producing traceability tables

## Exit Criteria

Test generation is complete when:

- every clarified requirement has validation coverage
- regression-sensitive flows are identified
- the chosen test layers match system risk
- expected evidence is defined before implementation completes
