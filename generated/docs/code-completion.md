# Code Completion

## Purpose

This stage implements the clarified requirements using the approved architectural direction and testing plan.

## Implementation Principles

- follow the clarified requirement set
- respect accepted ADRs
- preserve required behavior and compatibility constraints
- prefer minimally invasive changes that improve maintainability
- update documentation and changelog when the project rules require it

## AI-Assisted Coding Guidance

AI assistance is appropriate for:

- locating affected modules
- scaffolding new classes or tests
- suggesting refactors aligned to the chosen architecture
- identifying likely call sites impacted by a central abstraction change
- comparing old and new execution paths for parity risks

AI assistance should not replace:

- architectural judgment
- verification of business outcomes
- security review
- compatibility checks against actual project constraints

## Implementation Checklist

Before coding:

- requirements are clarified
- ADRs exist for major decisions
- test targets are defined
- affected files and services are identified

During coding:

- keep imports and structure conventional
- preserve existing contracts unless explicitly approved
- update all relevant consumers of changed abstractions
- avoid introducing undocumented drift from canonical design

After coding:

- verify changed flows against the test plan
- summarize the changes in implementation terms
- update supporting docs if the change alters developer understanding

## Example For This Project

A requirement to migrate built-in report SQL logic to Java may involve:

- adding a report calculator abstraction
- moving aggregation logic into Java services
- redirecting report execution services to the new rule engine
- preserving raw-SQL fallback for non-built-in cases when needed
- adding regression tests for dispatch and report output shape

## Exit Criteria

Code completion is complete when:

- the delivered code matches clarified scope
- architectural choices are reflected in implementation
- impacted integrations have been updated
- implementation is ready for validation
