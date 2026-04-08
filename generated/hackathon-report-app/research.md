# Research: Modernized Report Refactor

## Decision 1: Keep the current Angular application shape and modernize the existing components

- **Decision**: Refactor the current `report-viewer` and `login` screens in place, using the existing Angular 17 standalone component structure.
- **Rationale**: The current app already exposes the core maker/checker journeys. Reworking the existing components reduces migration risk and keeps routing, permissions, and current behavior stable.
- **Alternatives considered**:
  - Rebuild the UI as a new design system wrapper: rejected because it would add unnecessary scope and risk for a visual refresh.
  - Introduce a separate frontend app: rejected because the current project structure should remain intact.

## Decision 2: Move report-specific calculations into a dedicated Java service layer

- **Decision**: Fetch report data through repository/DAO access, then perform the report-specific calculations in Java service methods.
- **Rationale**: This keeps SQL limited to data retrieval, makes calculations easier to test, and separates business rules from raw query text.
- **Alternatives considered**:
  - Keep all logic in SQL: rejected because it makes the 12 report rules harder to maintain and review.
  - Use SQL stored procedures: rejected because the goal is to make the business logic more readable and testable in Java.

## Decision 3: Preserve the maker/checker permission flow unchanged

- **Decision**: Keep the current role checks, report submission, and approval flow intact while the UI and report logic are refactored.
- **Rationale**: The feature is a modernization effort, not a permissions redesign. Changing access rules would increase regression risk without adding value.
- **Alternatives considered**:
  - Redesign roles during refactor: rejected because it would blur the feature scope and complicate validation.

## Decision 4: Use service-level unit tests for each migrated report rule

- **Decision**: Add focused unit tests for migrated report service methods and verify the output against expected baseline behavior.
- **Rationale**: Unit tests provide fast regression coverage for each migrated rule and make parity checks easier to review.
- **Alternatives considered**:
  - Only rely on end-to-end testing: rejected because the logic migration needs tighter feedback at the service boundary.

## Decision 5: Document the work as a migration guide plus workflow SPEC

- **Decision**: Produce a migration document and a workflow SPEC that explain the path from analysis to PR preparation.
- **Rationale**: The feature includes both technical refactoring and a repeatable AI-assisted workflow, so the documentation must cover both the implementation and the process.
- **Alternatives considered**:
  - Keep only inline code comments: rejected because the feature needs a reusable handoff artifact.

## Decision 6: Keep the contract surface centered on the existing HTTP report endpoints

- **Decision**: Document the current report and report-run endpoints as the interface contract for the refactor.
- **Rationale**: The surrounding UI and integration points already depend on those endpoints, so the plan should preserve them while the internals change.
- **Alternatives considered**:
  - Add a new API version immediately: rejected because it is unnecessary for a behavior-preserving refactor.
