## ADDED Requirements

### Requirement: Complete history list operations
The system SHALL provide complete operation buttons in the history list for each report entry.

#### Scenario: Add Excel download button
- **WHEN** user views the history list
- **THEN** each report entry SHALL have a "Download Excel" button
- **AND** the button SHALL be positioned after the "View Process" button

#### Scenario: Enable Excel download for approved reports
- **WHEN** user clicks "Download Excel" on an approved report
- **THEN** system SHALL download the Excel file for that report
- **AND** file SHALL be named with the report name

## MODIFIED Requirements

### Requirement: Interactive report name functionality
The system SHALL enable clicking on report names to display detailed report data in a modal dialog.

#### Scenario: Click report name
- **WHEN** user clicks on a report name in the history list
- **THEN** a modal dialog SHALL open showing report details
- **AND** report data SHALL be loaded and displayed in the modal

#### Scenario: Modal data display
- **WHEN** modal opens for a report
- **THEN** report information SHALL be displayed (ID, status, timestamps)
- **AND** report data table SHALL be displayed with all columns and rows
- **AND** export functionality SHALL be available in the modal

#### Scenario: Modal close functionality
- **WHEN** user clicks the close button or clicks outside the modal
- **THEN** modal SHALL close
- **AND** modal data SHALL be cleared
