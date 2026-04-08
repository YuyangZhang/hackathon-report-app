---
description: Create or update project documentation for architecture, business workflows, setup, permissions, APIs, and report or SQL logic.
---

## User Input

```text
$ARGUMENTS
```

You **MUST** consider the user input before proceeding (if not empty).

## Purpose

Use this workflow when the user asks to generate, update, or reorganize documentation for an existing project.

This workflow is intended for technical and business-facing documentation such as:

- system architecture documentation
- business workflow documentation
- role and permission documentation
- setup and run guides
- API summaries
- report and SQL logic analysis
- presentation-ready project analysis docs

## Skills

- Use the `mintlify` skill if the request involves Mintlify pages, `docs.json`, `.mdx` pages, navigation, or documentation site structure.
- Use Speckit workflows only when the user is explicitly asking for specification or planning artifacts:
  - `/speckit.specify` for feature specifications
  - `/speckit.plan` for implementation planning
  - `/speckit.analyze` for read-only cross-artifact consistency analysis
- Do not use Speckit workflows as a substitute for normal project documentation unless the user explicitly asks for spec artifacts.

## Pre-Execution Checks

1. Read `AGENTS.md` and relevant project rules before editing documentation.
2. Read `docs-canonical/` first when the repository uses Canonical-Driven Development.
3. Check whether the documentation target already exists.
   - Prefer updating an existing document over creating duplicate documentation.
4. If the documentation is for a cloned or nested project, verify the correct project root before writing files.
5. Check whether `.specify/extensions.yml` exists.
   - If it exists, inspect any relevant hooks before documentation work when appropriate.
6. If the repository contains `docs.json`, read it before creating new docs pages.
7. If the request references existing requirements, specs, tasks, or changelog entries, read those sources before writing.

## Documentation Workflow

1. Clarify the documentation goal.
   - Determine whether the user wants:
     - a new document
     - an update to an existing document
     - a presentation-ready summary
     - architecture diagrams
     - setup instructions
     - workflow analysis
     - report or SQL logic explanation

2. Identify the authoritative sources.
   - Read only the files needed to answer accurately.
   - Prefer source-of-truth files such as:
     - backend and frontend entry points
     - controllers, services, entities, repositories
     - configuration files
     - schema and seed SQL
     - README and implementation docs
     - canonical docs when applicable

3. Build a concise system model.
   - Map the main runtime components.
   - Identify roles, data flow, approval flow, and persistence model.
   - Distinguish between implemented behavior and implied behavior.
   - Call out important constraints, assumptions, or risks.

4. Decide the documentation output structure.
   - Typical sections include:
     - Overview
     - Project Structure
     - Architecture
     - Tech Stack
     - Roles and Permissions
     - Business Workflow
     - Data Model or Database Structure
     - Setup and Run Instructions
     - API or Integration Summary
     - SQL or Report Logic Analysis
     - Risks, Gaps, or Future Refactor Notes
   - Include Mermaid diagrams when they improve clarity.

5. Write or update the documentation.
   - Keep the writing clear, scannable, and accurate.
   - Use repository-relative paths when referencing files.
   - Prefer markdown tables for role matrices, endpoint summaries, or technology inventories.
   - Prefer Mermaid for architecture or workflow diagrams.
   - Do not invent behavior not supported by code or docs.

6. Validate the documentation.
   - Confirm all requested topics are covered.
   - Confirm file paths, commands, and role names match the source.
   - Confirm diagrams reflect the documented flow.
   - If the repository uses DocGuard and documentation files were changed, run the relevant DocGuard validation workflow when appropriate.

7. Finalize with a concise summary.
   - State what file was created or updated.
   - Summarize the covered topics.
   - Mention any unresolved ambiguities or follow-up recommendations.

## Recommended Output Patterns

### For project analysis requests

Produce a presentation-ready markdown document with:

- short overview
- project structure summary
- architecture Mermaid diagram
- business workflow Mermaid diagram
- role permission table
- setup and run instructions
- SQL or business logic analysis
- concise conclusions and risks

### For architecture documentation requests

Focus on:

- component responsibilities
- data flow
- backend/frontend boundaries
- storage interactions
- external dependencies
- deployment or runtime assumptions

### For operational setup docs

Focus on:

- prerequisites
- install commands
- startup commands
- default ports
- environment assumptions
- test or validation commands

## Completion Criteria

This workflow is complete when all of the following are true:

- the intended documentation target is identified
- source files supporting the documentation were reviewed
- the resulting document covers the user’s requested topics
- all diagrams, tables, and commands are consistent with the codebase
- the final response clearly states what was created or updated
