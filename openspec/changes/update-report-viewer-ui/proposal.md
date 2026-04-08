## Why

The current report viewer UI has redundant elements and poor user experience. The title "报表管理系统" and current user display are duplicated in the top navigation, creating confusion. The report execution status section is unclear and doesn't properly integrate with the submission history. Users need a streamlined interface where executed reports are automatically added to their submission history, and clicking on any report shows the detailed data in an intuitive way.

## What Changes

- **Remove Redundant Title**: Remove "报表管理系统" title and current user display from report viewer as they're already shown in the top navigation
- **Redesign Report Status Section**: Remove the current report execution status display and integrate it with the submission history workflow
- **Enhance Submission History**: When a report is executed, automatically add it to the submission history list with proper status tracking
- **Improve Report List Interaction**: Clicking on any report in the history list should display the report data in a clear, organized format
- **Add Download Excel Functionality**: Add download Excel button to the operations column in the report history list
- **Streamline User Experience**: Create a cohesive workflow from report execution to data viewing to history management

## Capabilities

### New Capabilities
- `report-execution-integration`: Seamless integration between report execution and history management
- `history-list-enhancement`: Improved submission history with interactive report viewing
- `report-data-modal`: Modal dialog for displaying detailed report data when clicking history items
- `excel-download-integration`: Download Excel functionality for reports in the history list

### Modified Capabilities
- `report-viewer`: Updated to remove redundant elements and improve workflow integration

## Impact

- **Frontend Components**: Major updates to report-viewer component and related templates
- **User Workflow**: Improved user experience with streamlined report management
- **Data Flow**: Better integration between report execution, history, and data viewing
- **Backend Integration**: Potential API enhancements for better history management
