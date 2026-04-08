---
description: Validate backend and frontend locally by running backend tests, installing frontend dependencies, executing smoke and E2E suites, and using MCP tools for failure reproduction
---

## User Input

```text
$ARGUMENTS
```

You **MUST** consider the user input before proceeding (if not empty).

## Purpose

Use this workflow when you need to validate a full-stack change that touches both backend and frontend behavior.

This workflow is optimized for the repository layout:
- Backend app: `/Users/shuni/CascadeProjects/hackathon/hackathon-report-app/backend`
- Frontend app: `/Users/shuni/CascadeProjects/hackathon/hackathon-report-app/frontend`
- Backend tests: `hackathon/hackathon-report-app/backend/src/test`, `hackathon/hackathon-report-app/backend/src/integrationTest`, `hackathon/hackathon-report-app/backend/src/apiTest`
- Frontend E2E tests: `hackathon/hackathon-report-app/frontend/tests/e2e`
- Frontend Playwright config: `hackathon/hackathon-report-app/frontend/playwright.config.ts`
- Shared docs: `docs-canonical/`, `docs-implementation/`, `CHANGELOG.md`, `DRIFT-LOG.md`

## Skills

- Use the `mintlify` skill only if validation work also requires documentation or navigation updates for docs pages.
- Use Speckit workflows when validation reveals spec, plan, or task drift:
  - `/speckit.analyze` for cross-artifact consistency review
  - `/speckit.implement` when remaining implementation should be driven from tasks.md
  - `/speckit.tasks` if tasks are missing or stale
  - `/documentation` if implementation or canonical docs need to be updated after validation
- Do not invoke skills when the task is only to execute validation commands and summarize results.

## Pre-Execution Checks

1. Confirm both applications exist:
   - `hackathon/hackathon-report-app/backend`
   - `hackathon/hackathon-report-app/frontend`
2. Read relevant canonical documentation in `docs-canonical/` before changing configuration or test code.
3. If code or workflow changes are made, ensure `CHANGELOG.md` is updated before final completion.
4. Check whether `.specify/extensions.yml` exists.
   - If it exists, inspect `hooks.before_implement` and `hooks.after_implement`.
   - Treat hooks without `enabled: false` as enabled.
   - Treat hooks with empty or missing `condition` as executable.
   - Run mandatory hooks at the required phase.
5. Check for stale local processes before starting validation.
   - Inspect existing `npm install`, frontend dev server, and backend server processes.
   - Clear stale install jobs before retrying dependency installation.

## Hooks

### Before Validation Hooks

If `.specify/extensions.yml` defines executable `hooks.before_implement` entries, run them before validation when relevant.

Examples:
- prerequisite checks
- repository policy checks
- environment normalization
- local tooling setup validation

### After Validation Hooks

If `.specify/extensions.yml` defines executable `hooks.after_implement` entries, run them after validation completes.

Examples:
- documentation enforcement
- changelog checks
- traceability gates
- repository-specific quality checks

## MCPs

### mcp-playwright

Use `mcp-playwright` when a frontend smoke or E2E test fails, or when the user explicitly asks for interactive browser reproduction.

Use it for:
- reproducing a failing user flow interactively
- reading console errors
- inspecting network requests
- checking rendered state and visible controls
- confirming whether a failure is caused by startup, routing, API, or page logic

Do not use MCP Playwright before first attempting the automated browser suite unless the user explicitly requests manual interactive testing first.

### github-mcp-server

Use `github-mcp-server` only when validation work needs GitHub-side context, such as reviewing PR status, issues, or repository metadata.

Do not rely on it for local environment validation.

## Local Full-Stack Validation Workflow

1. Inspect the current repo state.
   - Verify backend and frontend project directories exist.
   - Verify key config files exist:
     - backend build file
     - frontend `package.json`
     - frontend `playwright.config.ts`
     - frontend Jest config if unit or contract tests are in scope
   - Check whether a prior `npm install` is still running or suspended.

2. Validate the backend first.
   - Use the backend project directory `hackathon/hackathon-report-app/backend`.
   - Run compile/test setup validation first:

     ```bash
     gradle -p hackathon/hackathon-report-app/backend testClasses
     ```

   - Then run the backend test layers:

     ```bash
     gradle -p hackathon/hackathon-report-app/backend test integrationTest apiTest
     ```

   - If backend tests fail, capture the exact failing test or task before changing code.
   - Do not proceed to frontend E2E assumptions that depend on backend behavior if backend validation is already failing.

3. Clear stale frontend install processes if necessary.
   - If multiple or suspended `npm install` jobs exist, terminate them before retrying install.
   - Do not start a new install while stale install jobs are still present.

4. Install frontend dependencies from `hackathon/hackathon-report-app/frontend`.
   - Recommended resilient command:

     ```bash
     npm install --no-audit --no-fund --fetch-timeout=300000 --fetch-retries=5 --prefer-online
     ```

   - If install fails, inspect the latest npm debug log.
   - Distinguish between:
     - registry/network timeout
     - peer dependency conflict
     - lockfile drift
     - package script failure

5. Validate the frontend smoke suite.
   - Run:

     ```bash
     npm --prefix hackathon/hackathon-report-app/frontend run test:smoke
     ```

   - If smoke fails because the frontend server is not reachable, inspect `playwright.config.ts`.
   - Ensure the web server command binds an explicit host and port when necessary.
   - Ensure the HTML report output does not conflict with Playwright artifact output.
   - Avoid requiring ffmpeg-based video capture unless intentionally needed.

6. Validate the full frontend E2E suite.
   - Run:

     ```bash
     npm --prefix hackathon/hackathon-report-app/frontend run test:e2e
     ```

   - If E2E fails, capture the failing test file, stack trace, and whether the failure is frontend-only or backend-dependent.

7. Optional frontend unit and contract validation.
   - Run these when requested or when the failure seems to be below browser level:

     ```bash
     npm --prefix hackathon/hackathon-report-app/frontend test
     npm --prefix hackathon/hackathon-report-app/frontend run test:contract
     ```

   - Use them for rendering, service, serialization, or request/response issues.

8. Reproduce interactively with MCP when automated browser tests fail.
   - Use `mcp-playwright` to navigate, inspect, and reproduce the failure manually.
   - Prefer automated failure output first, then interactive confirmation.

9. Final validation summary.
   - Report:
     - whether backend tests passed
     - whether frontend install succeeded
     - whether smoke passed
     - whether E2E passed
     - any config changes made
     - any remaining blockers
     - whether hooks were executed

## Failure Handling

- If backend Gradle tasks fail, isolate the failing test or source set before changing the frontend.
- If `npm install` fails with `EIDLETIMEOUT`, retry with the resilient install command and inspect npm logs.
- If Playwright fails with reporter/output path conflicts, separate the report directory from the Playwright artifact directory.
- If Playwright fails with missing ffmpeg due to retained video capture, disable video or install Playwright dependencies intentionally.
- If Playwright fails with `ERR_CONNECTION_REFUSED`, make the web server startup explicit and verify the expected URL is reachable.
- If the frontend E2E path depends on live backend behavior, ensure the backend server or mocked backend path is available before concluding the failure is purely frontend.

## Completion Criteria

This workflow is complete when all of the following are true:
- backend test tasks complete successfully, or a concrete blocker is isolated with evidence
- frontend dependencies install successfully, or a concrete blocker is isolated with evidence
- `npm --prefix hackathon/hackathon-report-app/frontend run test:smoke` passes, or a concrete blocker is isolated with evidence
- `npm --prefix hackathon/hackathon-report-app/frontend run test:e2e` passes, or a concrete blocker is isolated with evidence
- any relevant hooks have been run
- the final summary clearly states current backend and frontend validation status
