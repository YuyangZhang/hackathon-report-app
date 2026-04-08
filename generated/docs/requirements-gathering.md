# Requirement Gathering

## Purpose

This stage captures the original stakeholder need before technical interpretation begins. The goal is to preserve business intent, constraints, urgency, and expected value.

## Inputs

Typical inputs include:

- stakeholder requests
- issue descriptions
- demo feedback
- support escalations
- compliance or audit requirements
- hackathon evaluation criteria

## Intake Template

Use the following structure for each incoming requirement:

| Field | Description |
|------|------|
| Request Title | Short business-friendly title |
| Request Source | Stakeholder, team, customer, or document |
| Business Problem | What pain point or opportunity exists |
| Desired Outcome | What success looks like |
| Scope Clues | Known systems, screens, APIs, workflows |
| Constraints | Timeline, tools, compatibility, permissions |
| Risks | Operational, security, UX, data, compliance |
| Evidence | Screenshots, examples, logs, documents |
| Open Questions | Unknowns requiring clarification |

## Capture Guidance

When collecting a requirement:

1. Capture the request in the stakeholder's language first.
2. Separate facts from assumptions.
3. Record explicit constraints such as Java version, Node version, security rules, or approval flow requirements.
4. Note any regression-sensitive areas.
5. Preserve any references to affected docs, APIs, or workflows.

## Sample Requirement Capture

**Title:** Preserve maker-checker report approvals while moving report logic out of SQL

**Business Problem:** Complex report SQL is difficult to maintain and slows delivery of enhancements.

**Desired Outcome:** Move business logic into maintainable Java services without changing user-visible report outcomes or approval flow behavior.

**Constraints:** Java 17 compatibility, Node 18 compatibility, preserve API behavior, keep existing report templates working.

**Open Questions:** Which reports are in scope, how parity is validated, and what regression evidence is required.

## AI Assistance

AI can help by:

- summarizing raw stakeholder notes
- extracting risks and constraints
- identifying hidden assumptions
- organizing intake content into a standard template

## Exit Criteria

Requirement gathering is complete when:

- the request is captured in a standard format
- business intent is documented
- known constraints are recorded
- major unknowns are listed for clarification
