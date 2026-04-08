## ADDED Requirements

### Requirement: Comprehensive Report Listing
The system SHALL display a comprehensive list of all reports with detailed information including name, status, generated time, submit time, approval time, and available operations.

#### Scenario: Report List Display
- **WHEN** user navigates to the Report List section
- **THEN** the system SHALL display a table with columns: Report Name, Status, Generated Time, Submit Time, Approval Time, Operations
- **AND** each row SHALL represent a single report instance
- **AND** the list SHALL support pagination for large datasets

#### Scenario: Status Indicators
- **WHEN** displaying report status
- **THEN** status SHALL be shown with color-coded badges (executing=blue, generated=green, pending=orange, rejected=red)
- **AND** status SHALL update in real-time as reports progress through workflow

### Requirement: Report Operations
The system SHALL provide operation buttons for each report including "View Process", "Download Excel", and other relevant actions.

#### Scenario: Operation Buttons
- **WHEN** viewing the report list
- **THEN** each report row SHALL display operation buttons based on report status and user permissions
- **AND** "View Process" SHALL show the approval workflow history
- **AND** "Download Excel" SHALL be available only for generated/approved reports

#### Scenario: Bulk Operations
- **WHEN** user selects multiple reports
- **THEN** bulk operations SHALL be available for approved actions
- **AND** the system SHALL validate permissions before executing bulk operations
