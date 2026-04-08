# Implementation Plan: Modernized Report Refactor

**Branch**: `[003-ui-backend-refactor]` | **Date**: 2026-04-08 | **Spec**: `/Users/shuni/CascadeProjects/specs/003-ui-backend-refactor/spec.md`
**Input**: Feature specification from `/specs/003-ui-backend-refactor/spec.md`

**Note**: This template is filled in by the `/speckit.plan` command. See `.specify/templates/plan-template.md` for the execution workflow.

## Summary

Beautify the existing Angular report workspace and login flows without changing maker/checker permissions, while migrating the 12 built-in report calculations from embedded SQL into Java service-layer business logic backed by repository-based data retrieval. Deliver unit tests for the migrated backend methods, a migration document, and a workflow SPEC that explains the AI-assisted development and review path.

## Technical Context

<!--
  ACTION REQUIRED: Replace the content in this section with the technical details
  for the project. The structure here is presented in advisory capacity to guide
  the iteration process.
-->

**Language/Version**: Java 17 backend, Angular 17 / TypeScript 5.4 frontend, Node 18 toolchain  
**Primary Dependencies**: Spring Boot 3.2.4, Spring Data JPA, Spring JDBC, Spring Security, Angular core/forms/router, RxJS, JUnit 5, Spring Boot Test  
**Storage**: Existing relational database tables for `report_config`, `report_run`, and `report_audit_event`; H2 runtime profile already supported  
**Testing**: Gradle test task with Spring Boot Test / JUnit 5 for backend, Angular CLI test task for frontend, plus focused service unit tests for migrated report rules  
**Target Platform**: Browser-based Angular application backed by a Spring Boot web service  
**Project Type**: Web application  
**Performance Goals**: Keep report execution and approval flows responsive enough for interactive use; preserve existing API response behavior for the 12 reports  
**Constraints**: Preserve maker/checker permissions and routing, keep the current repository layout, avoid changing business outcomes, and stay compatible with Java 17 and Node 18  
**Scale/Scope**: One Angular frontend, one Spring backend, 12 migrated built-in reports, and the existing report submission/approval workflow

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

PASS: The plan stays within the existing frontend/backend application boundaries, preserves the maker/checker permission model, and uses the current technology stack without introducing a new top-level project structure.

PASS: No constitution-specific violations require complexity justification for this feature.

## Project Structure

### Documentation (this feature)

```text
specs/[###-feature]/
├── plan.md              # This file (/speckit.plan command output)
├── research.md          # Phase 0 output (/speckit.plan command)
├── data-model.md        # Phase 1 output (/speckit.plan command)
├── quickstart.md        # Phase 1 output (/speckit.plan command)
├── contracts/           # Phase 1 output (/speckit.plan command)
└── tasks.md             # Phase 2 output (/speckit.tasks command - NOT created by /speckit.plan)
```

### Source Code (repository root)

```text
backend/
├── src/main/java/com/legacy/report/
│   ├── controller/
│   ├── dao/
│   ├── model/
│   ├── repository/
│   └── service/
└── src/test/java/com/legacy/report/

frontend/
└── src/app/
    ├── components/auth/
    ├── components/report/
    ├── services/
    └── app.routes.ts

specs/003-ui-backend-refactor/
├── plan.md
├── research.md
├── data-model.md
├── quickstart.md
└── contracts/
```

**Structure Decision**: Keep the existing two-application layout intact, modernize the Angular UI inside `frontend/src/app/components/*`, migrate report computation into the backend service layer under `backend/src/main/java/com/legacy/report/service`, and store planning artifacts under `specs/003-ui-backend-refactor/`.

## Complexity Tracking

No complexity exceptions are required for this feature. The plan stays within the existing frontend/backend split and uses the current application structure.
