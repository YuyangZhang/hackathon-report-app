# Report API Contract

## Purpose

This document describes the existing report HTTP surface that the refactor must preserve.

## Base Paths

- `/api/reports`
- `/api/report-runs`

## Endpoints

### GET `/api/reports`

Returns the list of report definitions.

#### Response Shape

- Array of report objects
- Each object includes `id`, `name`, `sql`, and `description`

### GET `/api/reports/{id}`

Returns one report definition by id.

#### Response Shape

- Single report object with `id`, `name`, `sql`, and `description`

### POST `/api/reports/{id}/execute`

Executes the selected report and creates a report run.

#### Response Shape

- Array of result rows
- Each row contains the report output fields for that report

### POST `/api/reports/{id}/submit`

Submits a generated report run for checker review.

#### Response Shape

- No body on success

### POST `/api/reports/{id}/decision`

Submits the checker decision for a report run.

#### Request Shape

- `decision`: `APPROVED` or `REJECTED`
- `comment`: optional free-text comment

#### Response Shape

- No body on success

### GET `/api/report-runs/my-latest?reportId={id}`

Returns the current user’s latest run for a given report.

### GET `/api/report-runs/my-runs`

Returns the maker’s submitted runs.

### GET `/api/report-runs/submitted`

Returns the submitted runs available for checker review.

### GET `/api/report-runs/checker/history`

Returns the checker’s historical review list.

### GET `/api/report-runs/{id}/audit`

Returns the audit trail for a report run.

### GET `/api/report-runs/{id}/export`

Exports a completed report run as an Excel file.

## Contract Notes

- The refactor must keep these routes stable.
- The UI may change presentation, but the permission flow must remain consistent.
- The migrated Java business rules must preserve the result shape expected by these endpoints.
