## ADDED Requirements

### Requirement: Remove redundant UI elements
The system SHALL remove redundant UI elements from the report viewer to streamline the interface.

#### Scenario: Remove login form
- **WHEN** user views the report viewer
- **THEN** the login form section SHALL NOT be displayed

#### Scenario: Remove current report status section
- **WHEN** user views the report viewer
- **THEN** the current report run status and submission approval section SHALL NOT be displayed

#### Scenario: Preserve core functionality
- **WHEN** redundant elements are removed
- **THEN** report execution controls SHALL remain functional
- **AND** history list SHALL remain accessible

### Requirement: Clean up unused component properties
The system SHALL remove component properties that are no longer used after UI element removal.

#### Scenario: Remove unused properties
- **WHEN** UI elements are deleted
- **THEN** component properties used only by deleted elements SHALL be removed
- **AND** methods used only by deleted elements SHALL be removed

#### Scenario: Clean up CSS styles
- **WHEN** UI elements are deleted
- **THEN** CSS styles specific to deleted elements SHALL be removed
- **AND** shared styles SHALL remain intact
