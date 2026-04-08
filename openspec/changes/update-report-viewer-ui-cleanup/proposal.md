## Why

The current report viewer UI contains redundant elements and incomplete functionality that creates user confusion. Users see duplicate information (login form, current status section) and the history list lacks proper data viewing capabilities. This cleanup will streamline the interface and improve user experience by removing redundancy and completing the interactive history functionality.

## What Changes

- **Remove current report status section**: Delete the entire "Maker perspective: current report run status and submission approval" section that displays current run details and audit trail
- **Remove login form**: Delete the user login information section from report viewer since authentication is handled elsewhere
- **Enhance history list operations**: Add "Download Excel" button after "View Process" in the operations column
- **Complete report name interaction**: Ensure clicking report names properly displays report data in modal

## Capabilities

### New Capabilities
- `ui-cleanup`: Streamline report viewer interface by removing redundant elements and completing interactive functionality

### Modified Capabilities
- `history-list-enhancement`: Complete the interactive history list with proper report name click handling and operation buttons

## Impact

- Affected components: `report-viewer.component.html`, `report-viewer.component.css`, `report-viewer.component.ts`
- User experience: Cleaner interface with reduced clutter and improved workflow
- No API changes required - purely frontend UI modifications
- No breaking changes to existing functionality, only removals and enhancements
