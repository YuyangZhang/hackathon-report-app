# Change Preparation

## Purpose

This stage packages the delivered work into a release-ready change bundle with supporting evidence and traceability.

## Required Change Package Elements

A complete package should include:

- requirement summary
- clarified scope and acceptance criteria
- relevant ADR references
- implementation summary
- test evidence summary
- changelog entry
- known risks or rollout notes
- links to supporting docs and workflows

## Change Preparation Checklist

| Item | Purpose |
|------|------|
| Scope Summary | States what changed |
| Requirement Links | Preserves traceability |
| ADR Links | Explains technical choices |
| Test Evidence | Shows verification |
| Risk Notes | Surfaces residual concerns |
| Rollout Notes | Captures environment or startup dependencies |
| Documentation Links | Points to lasting project knowledge |

## Security And Quality Hooks

Before packaging a change, confirm any required control hooks have been satisfied, such as:

- no hardcoded secrets introduced
- no unsafe SQL concatenation added
- no unreviewed insecure dependency changes
- required regression suites executed
- documentation/changelog obligations satisfied

## Example Change Package For The Report App

A strong package for the report-refactor theme may include:

- summary of migrated built-in reports
- service classes introduced or updated
- dispatch path changes
- regression tests added
- startup or environment notes such as Java 17 requirements

## Exit Criteria

Change preparation is complete when:

- the supporting artifacts are assembled
- traceability is preserved
- quality and security obligations are addressed
- the package is ready for review or release discussion
