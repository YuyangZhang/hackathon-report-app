## ADDED Requirements

### Requirement: Real-time Status Updates
The system SHALL provide real-time updates for report generation status, showing "executing" status during generation and "generated" when complete.

#### Scenario: Status Progression
- **WHEN** a report generation is initiated
- **THEN** the report SHALL immediately appear in the list with "executing" status
- **AND** the status SHALL automatically update to "generated" when processing completes
- **AND** status updates SHALL occur without requiring manual page refresh

#### Scenario: Status Polling
- **WHEN** reports are in "executing" status
- **THEN** the system SHALL poll the backend for status updates every 5 seconds
- **AND** polling SHALL stop automatically when all reports reach terminal states

### Requirement: Generation Progress Indicators
The system SHALL provide visual indicators for report generation progress to enhance user experience.

#### Scenario: Progress Visualization
- **WHEN** a report is executing
- **THEN** a progress bar or spinner SHALL be displayed in the status column
- **AND** the progress indicator SHALL provide visual feedback about ongoing operations

#### Scenario: Completion Notifications
- **WHEN** a report generation completes
- **THEN** the status badge SHALL update immediately to reflect completion
- **AND** a subtle notification SHALL appear to inform users of the completed report
- **AND** the report row SHALL be highlighted briefly to draw attention to the update
