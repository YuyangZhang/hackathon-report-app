# Tasks: Modernized Report Refactor

**Input**: Design documents from `/specs/003-ui-backend-refactor/`
**Prerequisites**: `plan.md` (required), `spec.md` (required), `research.md`, `data-model.md`, `contracts/`, `quickstart.md`

**Tests**: Backend unit tests and regression tests are required because the spec explicitly asks for migrated Java service tests and permission stability coverage.

**Organization**: Tasks are grouped by user story so each story can be implemented and verified independently.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (`US1`, `US2`, `US3`, `US4`)
- Include exact file paths in descriptions

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Shared foundation for the UI refresh and backend rule migration

- [ ] T001 [P] Add shared report UI theme tokens and spacing defaults in `frontend/src/styles.scss` for cards, buttons, tables, and form fields.
- [ ] T002 [P] Create the backend report-rule package scaffold and shared calculator interface in `backend/src/main/java/com/legacy/report/service/rules/ReportRuleCalculator.java` and `backend/src/main/java/com/legacy/report/service/ReportExecutionService.java`.

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core building blocks that all user stories depend on

**⚠️ CRITICAL**: No story work should begin until this phase is complete

- [ ] T003 [P] Refactor `backend/src/main/java/com/legacy/report/dao/ReportDao.java` to keep SQL focused on report metadata and base data retrieval only.
- [ ] T004 [P] Capture baseline output fixtures for the 12 built-in reports in `backend/src/test/resources/report-baselines/` for parity testing.

**Checkpoint**: The shared data access and test baseline foundation is ready for story work.

---

## Phase 3: User Story 1 - Report UI Beautification (Priority: P1) 🎯 MVP

**Goal**: Refresh the Angular report workspace and login/review screens so they feel modern and easier to use, without changing report behavior.

**Independent Test**: A user can log in, choose a report, run it, inspect history, and submit or review a run using the updated visual layout without any change to the maker/checker flow.

### Implementation for User Story 1

- [ ] T005 [P] [US1] Rewrite the main report workspace template in `frontend/src/app/components/report/report-viewer.component.html` with clearer sections, aligned controls, and short comments for the maker, result, and checker areas.
- [ ] T006 [P] [US1] Replace `frontend/src/app/components/report/report-viewer.component.css` with responsive card, table, button, and empty-state styling for the report workspace.
- [ ] T007 [P] [US1] Refresh `frontend/src/app/components/auth/login.component.ts` markup and inline styles so the login form matches the modernized report UI.
- [ ] T008 [P] [US1] Modernize `frontend/src/app/components/report/report-run-flow.component.ts` with a cleaner timeline layout and readable audit-history styling.
- [ ] T009 [P] [US1] Tune the root shell in `frontend/src/app/app.component.ts` so the polished layout and spacing are consistent across the login and report screens.

**Checkpoint**: The report workspace should look polished and remain functionally equivalent to the current behavior.

---

## Phase 4: User Story 2 - Java Report Rule Migration (Priority: P2)

**Goal**: Move the 12 built-in report calculations out of SQL and into Java service-layer business logic while preserving the existing outputs.

**Independent Test**: Each migrated report can be executed through the backend service layer and produce the same approved baseline output as the legacy SQL path.

### Tests for User Story 2

- [ ] T010 [P] [US2] Create the Java report rule dispatcher in `backend/src/main/java/com/legacy/report/service/ReportExecutionService.java` and the supporting files under `backend/src/main/java/com/legacy/report/service/rules/`.
- [ ] T011 [P] [US2] Implement the 12 migrated report calculation classes under `backend/src/main/java/com/legacy/report/service/rules/` with brief comments that explain each preserved business rule.
- [ ] T012 [US2] Rewrite `backend/src/main/java/com/legacy/report/service/ReportService.java` to call the new execution service instead of executing raw SQL business logic.
- [ ] T013 [US2] Update `backend/src/main/java/com/legacy/report/service/ReportRunService.java` and `backend/src/main/java/com/legacy/report/service/ReportExcelExportService.java` to consume the migrated execution path while preserving snapshots, audit events, and Excel exports.
- [ ] T014 [P] [US2] Add parameterized unit tests for all 12 built-in report calculators in `backend/src/test/java/com/legacy/report/service/rules/ReportRuleCalculatorTest.java` using the baseline fixtures.
- [ ] T015 [P] [US2] Add service-level regression tests in `backend/src/test/java/com/legacy/report/service/ReportServiceTest.java` and `backend/src/test/java/com/legacy/report/service/ReportExcelExportServiceTest.java` to verify execution parity and export behavior.

**Checkpoint**: The backend should now execute report logic through Java service methods with unit coverage for the migrated rules.

---

## Phase 5: User Story 3 - Migration Documentation and Workflow SPEC (Priority: P3)

**Goal**: Produce the handoff documents that explain the migration and the AI-assisted workflow used to deliver it.

**Independent Test**: A reviewer can read the migration document and workflow SPEC and understand the SQL-to-Java migration, the review path, and the PR preparation steps without needing extra context.

### Implementation for User Story 3

- [ ] T016 [P] [US3] Create `specs/003-ui-backend-refactor/migration.md` documenting the legacy SQL-to-Java migration, report-by-report mapping, and baseline verification strategy.
- [ ] T017 [P] [US3] Create `specs/003-ui-backend-refactor/workflow-spec.md` with the AI-assisted workflow from requirement analysis through code review preparation.
- [ ] T018 [US3] Add the example PR description, tools/skills table, and simple git hook suggestions to `specs/003-ui-backend-refactor/workflow-spec.md`.

**Checkpoint**: The feature should now have the supporting documentation needed for demo and PR submission.

---

## Phase 6: User Story 4 - Permission Stability and Regression Coverage (Priority: P3)

**Goal**: Keep maker/checker permissions, routes, and response shapes stable while the UI and backend internals are modernized.

**Independent Test**: A maker can still submit reports and a checker can still review them, with unchanged role boundaries and endpoint behavior.

### Tests for User Story 4

- [ ] T019 [P] [US4] Add backend regression tests in `backend/src/test/java/com/legacy/report/controller/ReportControllerTest.java` and `backend/src/test/java/com/legacy/report/controller/ReportRunControllerTest.java` to confirm maker submission, checker decision, audit, and export response stability.
- [ ] T020 [P] [US4] Add frontend route-guard smoke tests in `frontend/src/app/services/auth.guard.spec.ts` to confirm maker and checker navigation boundaries remain unchanged.

**Checkpoint**: Permission behavior and route protection should remain unchanged after the refactor.

---

## Phase 7: Polish & Cross-Cutting Concerns

**Purpose**: Final validation and cleanup across the full feature

- [ ] T021 [P] Run the validation steps from `specs/003-ui-backend-refactor/quickstart.md` and fix any integration issues uncovered in the backend or frontend.
- [ ] T022 [P] Update `CHANGELOG.md` and `DRIFT-LOG.md` with the final implementation summary if any delivered code diverges from the planned behavior.

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies - can start immediately.
- **Foundational (Phase 2)**: Depends on Setup completion - blocks all user stories.
- **User Stories (Phase 3+)**: Depend on Foundational completion.
  - User stories can then proceed in parallel if staffing allows.
  - Or be executed sequentially in priority order (P1 → P2 → P3 → P3).
- **Polish (Final Phase)**: Depends on the desired user stories being complete.

### User Story Dependencies

- **User Story 1 (P1)**: Independent after Foundational.
- **User Story 2 (P2)**: Independent after Foundational, but it reuses the data-access foundation and baseline fixtures.
- **User Story 3 (P3)**: Independent after the core implementation work is understood; it can proceed once the implementation direction is locked.
- **User Story 4 (P3)**: Should be validated after the UI and backend changes are in place to confirm nothing regressed.

### Within Each User Story

- Shared foundation tasks should complete before story-specific work.
- Tests for User Story 2 and User Story 4 should be written before or alongside the corresponding implementation.
- Keep UI template, stylesheet, and shell edits separate so they can be worked on in parallel.
- Keep backend service, DAO, and test changes separate so the migration work can be split safely.
- Story complete before moving to the next priority when running sequentially.

### Parallel Opportunities

- `T001` and `T002` can run in parallel because they touch different areas of the stack.
- `T003` and `T004` can run in parallel because they target the DAO and test resources.
- `T005`, `T006`, `T007`, `T008`, and `T009` can run in parallel because they modify different frontend files.
- `T010` and `T011` can run in parallel because they create separate backend service-layer files.
- `T014` and `T015` can run in parallel because they cover different backend test layers.
- `T016` and `T017` can run in parallel because they create separate documentation artifacts.
- `T019` and `T020` can run in parallel because they cover backend and frontend regression checks.
- `T021` and `T022` can run in parallel near the end once the feature is stable.

---

## Parallel Example: User Story 1

```text
Task: T005 [P] [US1] Rewrite the main report workspace template in `frontend/src/app/components/report/report-viewer.component.html`
Task: T006 [P] [US1] Replace `frontend/src/app/components/report/report-viewer.component.css` with responsive styling
Task: T007 [P] [US1] Refresh `frontend/src/app/components/auth/login.component.ts` markup and inline styles
Task: T008 [P] [US1] Modernize `frontend/src/app/components/report/report-run-flow.component.ts` timeline styling
Task: T009 [P] [US1] Tune the root shell in `frontend/src/app/app.component.ts`
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1: Setup.
2. Complete Phase 2: Foundational.
3. Complete Phase 3: User Story 1.
4. **STOP and VALIDATE**: Confirm the updated UI is usable and business behavior is unchanged.
5. Demo the refreshed report workspace if it is ready.

### Incremental Delivery

1. Complete Setup + Foundational → shared foundation ready.
2. Add User Story 1 → validate the modernized UI → demo the visual refresh.
3. Add User Story 2 → validate rule parity with unit tests → demo the migration.
4. Add User Story 3 → review the docs → share the migration and workflow materials.
5. Add User Story 4 → run permission regression checks → confirm maker/checker stability.

### Parallel Team Strategy

With multiple developers:

1. Team completes Setup + Foundational together.
2. Once Foundational is done:
   - Developer A: User Story 1
   - Developer B: User Story 2
   - Developer C: User Story 3
   - Developer D: User Story 4
3. Merge each story after its independent validation passes.

---

## Notes

- `[P]` tasks = different files, no dependencies.
- `[Story]` labels map tasks to specific user stories for traceability.
- The backend unit-test work is required because the spec explicitly requests migrated Java service tests.
- The permission regression tasks are required because the spec explicitly says maker/checker behavior must stay unchanged.
- Keep the implementation incremental: preserve current behavior first, then improve readability and maintainability.
- Avoid cross-story file conflicts where possible.
