---
description: Analyze hackathon report app startup and recovery work at the process level only, focusing on skills, hooks, MCP usage, diagnostics, and decision logic without running npm start or Gradle build commands
---

## User Input

```text
$ARGUMENTS
```

You **MUST** consider the user input before proceeding (if not empty).

## Purpose

Use this workflow when you need a reasoning-first startup recovery plan for the hackathon report application without executing the application startup itself.

This workflow is for thinking, planning, and diagnosis only. It explicitly excludes direct frontend start commands such as `npm start` and backend build or runtime commands such as `gradle build`, `gradle bootRun`, or `./gradlew bootRun`.

This workflow is optimized for the repository layout:
- Frontend app: `/Users/shuni/CascadeProjects/hackathon/hackathon-report-app/frontend`
- Backend app: `/Users/shuni/CascadeProjects/hackathon/hackathon-report-app/backend`
- Feature quickstart: `/Users/shuni/CascadeProjects/specs/003-ui-backend-refactor/quickstart.md`
- Canonical docs: `/Users/shuni/CascadeProjects/docs-canonical/`
- Changelog: `/Users/shuni/CascadeProjects/CHANGELOG.md`

## Skills

- Do not invoke a skill if the task is only to reason about startup blockers and recovery paths.
- Use `mintlify` only if the startup analysis also requires restructuring published documentation pages.
- Use Speckit workflows only when startup analysis reveals design/spec inconsistencies:
  - `/speckit.analyze` for cross-artifact consistency checks
  - `/speckit.implement` when missing implementation tasks must be resumed from `tasks.md`
  - `/speckit.tasks` if the current startup or recovery work is not reflected in task planning artifacts

## Hooks

1. Check whether `.specify/extensions.yml` exists.
2. If it exists, inspect:
   - `hooks.before_implement`
   - `hooks.after_implement`
3. Treat disabled hooks as skipped.
4. Treat hooks with empty or missing `condition` as executable.
5. If a mandatory hook is relevant to startup diagnosis, include it in the plan at the correct phase.

### Before-Phase Hook Guidance

Appropriate pre-hooks may include:
- prerequisite checks
- environment normalization
- repository-specific environment validation
- local toolchain verification

### After-Phase Hook Guidance

Appropriate post-hooks may include:
- changelog enforcement
- documentation validation
- traceability checks
- quality gates such as DocGuard

## MCPs

### mcp-playwright

Use `mcp-playwright` only when:
- the user explicitly asks for interactive reproduction, or
- automated/browser-level evidence already suggests the issue is in routing, rendering, network behavior, or runtime availability

Use MCP Playwright for:
- checking whether the frontend renders expected screens once it is known to be running
- checking console errors
- checking network failures to backend endpoints
- distinguishing between frontend-only issues and backend-unavailable issues
- validating whether startup symptoms are visible in-browser after the runtime state is already known

Do not use MCP Playwright as the first diagnostic step when the primary blocker is clearly environment, wrapper, Gradle, or Java setup.

## Preconditions

1. Read the relevant canonical and implementation guidance first.
   - Review `AGENTS.md`.
   - Review `docs-canonical/ARCHITECTURE.md`.
   - Review relevant quickstart or implementation docs such as `specs/003-ui-backend-refactor/quickstart.md`.
2. Confirm the frontend and backend paths exist.
3. If any workflow or documentation files will be changed, plan to update `CHANGELOG.md`.
4. If any workflow or documentation files are changed, plan to run `npx docguard guard` before final completion.
5. Respect the file change rules in `AGENTS.md`, especially when the work expands beyond a small number of files.

## Process-Only Startup Analysis Workflow

1. Establish the intended runtime contract.
   - Identify expected frontend port.
   - Identify expected backend port.
   - Identify required Java version from `build.gradle`.
   - Identify frontend toolchain expectations from `package.json`.
   - Identify backend runtime assumptions from `application.yml`.

2. Inspect the startup surface without launching the app.
   - Review frontend scripts in `package.json`.
   - Review backend Gradle and wrapper files.
   - Review runtime configuration files.
   - Check whether the wrapper configuration is portable or machine-specific.
   - Check whether required wrapper artifacts are present.

3. Classify likely blocker categories.
   - Frontend dependency/tooling blocker
   - Backend wrapper/bootstrap blocker
   - Java version blocker
   - Gradle compatibility blocker
   - Environment/path portability blocker
   - Partial success case where frontend is healthy but backend is blocked

4. Use decision logic for frontend diagnosis.
   - If local Angular CLI is likely unavailable, infer that dependencies may be missing.
   - If only dependency-install symptoms are present, treat startup as blocked by installation readiness rather than app logic.
   - Separate non-blocking warnings from fatal runtime blockers.

5. Use decision logic for backend diagnosis.
   - If wrapper execution fails due to permissions, classify it as a script-execute issue.
   - If `GradleWrapperMain` is missing, inspect wrapper JAR presence and wrapper properties.
   - If `distributionUrl` is machine-specific, classify it as a portability defect.
   - If wrapper is repaired but the build still fails, inspect Java version and Gradle/plugin compatibility.
   - If the project requires Java 17 and only Java 8 is installed, stop analysis with a clear environment blocker instead of mutating code to fit Java 8.

6. Choose the least-invasive recovery path.
   - Prefer environment correction over source-code downgrade.
   - Prefer wrapper repair over replacing the build system.
   - Prefer toolchain alignment over dependency or plugin churn.
   - Prefer clear blocker reporting when the missing prerequisite is external to the repository.

7. Decide whether MCP usage is justified.
   - If the blocker is purely wrapper or Java-related, do not use MCP Playwright.
   - If the frontend is known to be reachable and behavior still appears broken, MCP Playwright may be used later.

8. Produce the execution recommendation set.
   - Identify which commands should be run next, but do not run them in this workflow.
   - Distinguish between safe read-only checks and mutating recovery commands.
   - Call out where user approval is needed.

9. Produce the final diagnosis summary.
   - State whether the likely issue is frontend, backend, environment, wrapper, or Java-related.
   - State the minimum fix path.
   - State whether documentation/workflow updates are needed.
   - State whether hooks or follow-up workflows should be used.

## Failure Handling

- If analysis reveals a missing local toolchain requirement, stop and report the prerequisite explicitly.
- If analysis reveals a machine-specific wrapper path, recommend wrapper normalization instead of local one-off workarounds.
- If analysis reveals a Java mismatch, recommend installing or selecting Java 17 instead of changing project source compatibility.
- If multiple blockers exist, report them in dependency order so the user can resolve the first true blocker first.
- If the startup investigation expands into implementation changes, switch to the appropriate implementation workflow rather than continuing in a reasoning-only loop.

## Completion Criteria

This workflow is complete when all of the following are true:
- the expected startup contract has been identified
- the likely blocker category has been isolated
- the recovery path is expressed as ordered next actions
- any relevant skills, hooks, and MCP decisions are documented
- if workflow or documentation files were changed, `CHANGELOG.md` is updated and `npx docguard guard` is run
