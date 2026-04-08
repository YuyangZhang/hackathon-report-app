# Implementation Testing

## Purpose

This stage validates that the delivered code satisfies the clarified requirements without breaking existing behavior.

## Validation Strategy

Testing should proceed from lower-risk, faster feedback layers toward broader workflow validation:

1. unit tests
2. integration tests
3. API tests
4. E2E or workflow tests when user behavior is impacted

## Validation Evidence

Capture evidence such as:

- passing test output
- failing test analysis when blocked
- screenshots for UI or workflow changes
- logs for backend execution or approval flow problems
- notes on known limitations or environment blockers

## Key Validation Areas For The Report App

- auth and role boundaries
- report execution dispatch
- report run persistence
- maker-checker approval workflow
- audit event creation
- export behavior
- output parity for migrated reports

## Regression Safety Expectations

The validation set should prove that:

- existing critical flows still work
- newly introduced abstractions are covered
- previously migrated logic does not silently fall back to incorrect execution paths
- security-sensitive flows still respect role or authentication rules

## AI Assistance

AI can help by:

- interpreting test failures
- grouping failures by likely root cause
- identifying missing coverage
- summarizing validation results for changelog or delivery artifacts

## Exit Criteria

Implementation testing is complete when:

- planned test layers have been executed or a blocker is clearly documented
- evidence exists for core requirements
- regressions have been ruled out or isolated
- outstanding risks are explicitly listed
