# Feature Specification: Modernized Report Refactor

**Feature Branch**: `[003-ui-backend-refactor]`  
**Created**: 2026-04-08  
**Status**: Draft  
**Input**: User description: "Refactor and modernize the report app with a polished Angular UI, migrate the 12 built-in report SQL rules into maintainable Java service abstractions without changing the maker/checker permission flow, generate unit tests and a migration document, and produce a workflow SPEC that shows an AI-assisted path from analysis to PR review."

## Clarifications

### Session 2026-04-08

- Q: How should the 12 report rules be migrated? → A: Move the report-specific calculations into Java service methods after fetching data from repositories, minimizing SQL to basic data retrieval.

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Faster Report Workspace (Priority: P1)

As a report user, I can open a clearer, more polished report workspace that shows the report catalog, current selection, run status, audit trail, and export actions in one place so I can complete report tasks with less friction.

**Why this priority**: The visual refresh is the most visible part of the refactor and directly improves the day-to-day experience for everyone who uses reports.

**Independent Test**: A user can log in, choose a report, run it, inspect the result history, and export the output without leaving the workspace.

**Acceptance Scenarios**:

1. **Given** a logged-in user with report access, **When** they open the main report workspace, **Then** they see a clearer layout with the available reports, selected report details, and primary actions.
2. **Given** a report has been run, **When** the user reviews the workspace, **Then** they can see the latest run status and associated audit trail in the same flow.
3. **Given** a report has no results, **When** the user runs it, **Then** the workspace shows an empty-state message instead of a broken table or blank screen.

---

### User Story 2 - Maintainable Report Logic (Priority: P2)

As a report maintainer, I can migrate the 12 built-in report calculations from embedded SQL into a dedicated backend rule layer so the report behavior stays the same while the code becomes easier to maintain and review.

**Why this priority**: This is the core technical refactor that reduces SQL complexity and makes future report changes safer without changing business rules.

**Independent Test**: A maintainer can validate each migrated report against its original output and confirm that the maker/checker flow still behaves the same.

**Acceptance Scenarios**:

1. **Given** one of the 12 built-in reports, **When** the migrated backend evaluates it, **Then** the result matches the approved baseline output for that report.
2. **Given** a migrated report rule changes, **When** the backend tests run, **Then** the service method has a unit test that verifies the expected business result.
3. **Given** a report rule cannot be evaluated, **When** the report is requested, **Then** the system shows a clear failure message without changing any permission decisions.

---

### User Story 3 - Reviewable Delivery Package (Priority: P3)

As a reviewer or teammate, I can open a migration document and a workflow SPEC that explain the refactor steps, validation approach, PR preparation, and hook suggestions so I can understand and demo the work without reconstructing the process myself.

**Why this priority**: The documentation package makes the refactor transferable and reviewable, which is essential for handoff and demo readiness after the code changes are complete.

**Independent Test**: A reviewer can read the documents and confirm that they cover the migration process, the AI-assisted workflow, and the evidence needed for review.

**Acceptance Scenarios**:

1. **Given** the feature is complete, **When** a reviewer opens the migration document, **Then** they see the source-to-target mapping, validation approach, and key implementation notes.
2. **Given** a teammate wants to follow the workflow, **When** they open the workflow SPEC, **Then** they see the step-by-step path from requirement analysis through code review preparation.
3. **Given** the workflow SPEC is reviewed, **When** the teammate checks the PR and hook guidance, **Then** they can understand the example PR description and simple git hook suggestions.

---

### User Story 4 - Stable Permissions and Structure (Priority: P3)

As an existing maker or checker, I can keep using the same report submission and approval flow after the refactor so the feature modernization does not disrupt established access rules or the project structure I already rely on.

**Why this priority**: The refactor must not break the current permission model or change how users submit and review reports.

**Independent Test**: A maker and a checker can complete the same report actions they already use, with the same access boundaries as before.

**Acceptance Scenarios**:

1. **Given** a maker user, **When** they submit a report run, **Then** the maker submission flow behaves as before.
2. **Given** a checker user, **When** they review a submitted report, **Then** the checker approval flow behaves as before.
3. **Given** a user without the correct role, **When** they attempt a restricted action, **Then** access remains blocked.

---

### Edge Cases

- A report returns no rows, and the interface must still show a useful empty state.
- A report contains a large number of rows, and the workspace must remain understandable without collapsing into an unreadable table.
- A migrated rule produces a different result from the approved baseline, and the mismatch must be obvious during review.
- A maker or checker tries to use the existing submit/approve flow after the refactor, and the permissions must stay unchanged.
- The migration document or workflow SPEC is incomplete, and the missing section must be obvious rather than silently skipped.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: The system MUST provide a polished report workspace that improves layout, alignment, button styling, and submission/approval usability while preserving the existing report actions.
- **FR-002**: The system MUST keep the maker and checker permission flow unchanged during and after the UI and backend refactor.
- **FR-003**: The system MUST move the SQL logic for the 12 built-in reports into Java service methods that perform the report-specific calculations after repository-based data retrieval, without changing business rules.
- **FR-004**: The system MUST provide unit tests for the migrated backend report methods so each report rule can be validated against expected outcomes.
- **FR-005**: The system MUST provide a migration document that explains the source SQL, the target backend approach, the validation method, and the key refactor decisions.
- **FR-006**: The system MUST provide a workflow SPEC in Markdown that documents requirement analysis, design, AI-assisted code generation, test generation, and code review preparation.
- **FR-007**: The system MUST include in the workflow SPEC the tools or skills used at each step, an example PR description, and simple git hook suggestions.
- **FR-008**: The system MUST display clear, user-friendly error messages when a report cannot be loaded, a migrated rule cannot be evaluated, or a workflow artifact is missing.
- **FR-009**: The system MUST keep migrated report outputs consistent with approved baseline results for the selected verification set.
- **FR-010**: The system MUST retain an auditable trail of report activity and refactor verification outcomes so maintainers can trace changes and review results.

### Key Entities *(include if feature involves data)*

- **Report Workspace**: The user-facing area where report browsing, execution, status, and history are presented together.
- **Report Definition**: The named report entry that identifies what the report is meant to produce and how it is organized for users.
- **Built-in Report Rule**: One of the 12 legacy report calculations that must be preserved while moving away from raw SQL and into Java service-layer business logic.
- **Migration Document**: The written summary of how the SQL logic was moved, how parity was verified, and what important constraints were preserved.
- **Workflow SPEC**: The Markdown guide that explains the AI-assisted development and review flow for this refactor.
- **Report Run**: A single execution instance of a report, including status and review history.
- **Audit Trail**: The record of actions taken on a report run so users and maintainers can understand what happened and when.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: A first-time user can locate, run, and review a report from the main workspace in under 2 minutes.
- **SC-002**: At least 90% of reviewed users can identify the current report status and next action without assistance after a short walkthrough.
- **SC-003**: All 12 built-in reports can be migrated so their approved baseline outputs still match the original behavior in the acceptance sample set.
- **SC-004**: Every migrated report rule has a unit test that validates the expected business output.
- **SC-005**: A reviewer can use the migration document and workflow SPEC to understand the refactor, the validation approach, and the review path without needing outside explanation.
- **SC-006**: Reviewers can distinguish UI changes, backend rule changes, and verification evidence clearly enough to approve or reject the refactor without ambiguity.

## Assumptions

- The existing report roles and approval flow remain in place during the refactor.
- The current project structure remains intact; the refactor stays within the existing frontend and backend application boundaries.
- The current backend platform remains the host for the refactored report rule layer.
- The initial migration focuses on the 12 built-in reports currently present in the legacy implementation.
- The target runtime compatibility remains Java 17 for the backend and Node 18 for the frontend toolchain.
- The AI tools used in the workflow support review and guidance rather than making production changes automatically.
- The report data model and expected business outcomes remain stable for the first refactor pass.
