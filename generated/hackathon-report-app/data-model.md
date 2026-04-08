# Data Model: Modernized Report Refactor

## ReportDefinition

Represents one built-in report that users can run.

### Fields

- **id**: Unique numeric identifier
- **name**: Human-readable report name
- **sql**: Legacy SQL source or retrieval definition used to fetch the report’s base data
- **description**: Short explanation shown in the UI

### Rules

- Must remain uniquely identifiable by `id`
- Must keep the same report identity across the refactor
- Must continue to support all 12 built-in reports

### Relationships

- One ReportDefinition can have many ReportRun records

## ReportRun

Represents a single execution of a report and its approval lifecycle.

### Fields

- **id**: Unique numeric identifier
- **reportId**: Links to the report definition
- **reportName**: Snapshot of the report name at execution time
- **status**: Current lifecycle state
- **makerUsername**: User who generated the run
- **checkerUsername**: User who approved or rejected the run
- **generatedAt**: Time the run was created
- **submittedAt**: Time the run was sent for review
- **decidedAt**: Time the checker made a decision
- **parametersJson**: Optional captured parameters used for execution
- **resultSnapshot**: Optional captured output snapshot for export and audit

### Validation Rules

- `reportId` is required
- `reportName` is required
- `status` must be one of Generated, Submitted, Approved, Rejected
- `makerUsername` is required
- `generatedAt` is required
- `submittedAt` may exist only after generation
- `decidedAt` may exist only after approval or rejection

### State Transitions

- **Generated** → **Submitted** when the maker submits the run
- **Submitted** → **Approved** when a checker approves the run
- **Submitted** → **Rejected** when a checker rejects the run
- Terminal states: **Approved**, **Rejected**

### Relationships

- Many ReportRun records belong to one ReportDefinition
- One ReportRun can have many AuditTrail entries

## ReportAuditEvent

Represents an immutable audit record for a report run action.

### Fields

- **id**: Unique numeric identifier
- **reportRunId**: Links to the report run
- **reportId**: Links to the report definition
- **actorUsername**: User who performed the action
- **actorRole**: Role of the actor when the action happened
- **eventType**: Generated, Submitted, Approved, or Rejected
- **eventTime**: Time the event occurred
- **comment**: Optional note or review comment

### Validation Rules

- `reportRunId` is required
- `reportId` is required
- `actorUsername` is required
- `eventType` is required
- `eventTime` is required

### Relationships

- Each AuditTrail entry belongs to exactly one ReportRun
- Each AuditTrail entry is associated with one ReportDefinition

## BusinessRule

Represents the Java-side calculation logic for one migrated report.

### Fields

- **name**: Name of the report rule
- **inputData**: Base data loaded from repositories
- **calculatedValues**: Derived fields added by the service layer
- **baselineReference**: Expected result set used for parity checks

### Rules

- Must preserve legacy output semantics
- Must be deterministic for a given input data set
- Must be independently testable at the service layer

## WorkflowArtifact

Represents the supporting planning and delivery documents for the feature.

### Fields

- **researchDoc**: Notes and rationale for technical decisions
- **migrationDoc**: Human-readable migration summary
- **workflowSpec**: Step-by-step workflow for AI-assisted delivery
- **quickstartDoc**: How to run and verify the feature locally

### Rules

- Must be readable by reviewers without code context
- Must stay aligned with the final implementation scope
