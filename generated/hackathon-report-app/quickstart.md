# Quickstart: Modernized Report Refactor

## Prerequisites

- Java 17
- Node 18
- A working database configuration for the report application
- Access to the existing report app repository

## Backend

1. Open the backend project.
2. Start the application with the existing Gradle wrapper.
3. Verify the backend starts successfully and exposes the existing report endpoints.
4. Run the backend tests to confirm the migrated report service methods still produce the expected outputs.

## Frontend

1. Open the frontend project.
2. Install dependencies if needed.
3. Start the Angular development server.
4. Open the report workspace and verify the improved layout, report selection, submission, and approval flows.

## Validation Checklist

- Log in as a maker and confirm report generation still works.
- Submit a generated report and confirm the status updates correctly.
- Log in as a checker and confirm review actions still work.
- Verify the updated UI keeps the same business flow while improving readability and spacing.
- Compare migrated report outputs against the approved baseline for all 12 built-in reports.

## Suggested Local Commands

### Backend

```bash
./gradlew test
./gradlew bootRun
```

### Frontend

```bash
npm install
npm start
npm test
```

## Demo Flow

1. Open the login screen.
2. Sign in as a maker.
3. Run a report and review the updated workspace.
4. Submit the report for approval.
5. Sign in as a checker.
6. Review and approve or reject the run.
7. Show the migration notes and workflow SPEC as supporting documentation.
