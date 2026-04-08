## ADDED Requirements

### Requirement: Interactive History List
The system SHALL provide an interactive submission history list where users can click on reports to view detailed data.

#### Scenario: Report Selection
- **WHEN** user clicks on any report in the history list
- **THEN** the system SHALL display the report data in a modal dialog
- **AND** the modal SHALL show all relevant report information and data

#### Scenario: List Data Display
- **WHEN** the history list is displayed
- **THEN** it SHALL show report name, status, generation time, submission time, approval time, and available operations
- **AND** each report row SHALL be clearly formatted and easy to scan

### Requirement: Enhanced List Operations
The system SHALL provide comprehensive operations for each report in the history list.

#### Scenario: Operation Buttons
- **WHEN** viewing the history list
- **THEN** each report row SHALL include "View Process" and "Download Excel" buttons
- **AND** operations SHALL be contextually enabled based on report status
- **AND** "Download Excel" SHALL only be available for approved or generated reports

#### Scenario: Data Sorting and Filtering
- **WHEN** the history list contains multiple reports
- **THEN** users SHALL be able to sort by date, status, or report name
- **AND** the system SHALL maintain sort preference during the session

### Requirement: Responsive History Display
The system SHALL ensure the history list works effectively across different screen sizes.

#### Scenario: Mobile History List
- **WHEN** viewing the history list on mobile devices
- **THEN** the list SHALL adapt to smaller screen width
- **AND** touch targets SHALL be appropriately sized for mobile interaction
- **AND** operations SHALL remain accessible and functional
