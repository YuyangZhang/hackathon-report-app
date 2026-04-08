## Context

The current report viewer component contains several redundant elements that create user confusion:
1. Login form is displayed even though authentication is handled at the app level
2. Current report status section duplicates information already available in the history list
3. History list operations are incomplete - missing Excel download functionality
4. Report name clicking functionality exists but needs verification and completion

The component is a standalone Angular component with existing modal functionality and service integration for report data retrieval.

## Goals / Non-Goals

**Goals:**
- Remove redundant UI elements to reduce interface clutter
- Complete the interactive history list functionality
- Ensure proper report data viewing through modal interaction
- Maintain existing functionality while streamlining the interface

**Non-Goals:**
- No changes to backend API or services
- No changes to authentication flow
- No changes to existing report execution logic
- No new features beyond the specified cleanup and completion

## Decisions

### UI Element Removal Strategy
- **Remove login form entirely**: Since authentication is handled at app level, the local login form in report viewer is redundant
- **Remove current status section**: The current run status and audit trail section duplicates information available in history list
- **Keep report execution controls**: Maintain report selection and execution functionality as core features

### History List Enhancement
- **Add Excel download button**: Place after "View Process" button in operations column
- **Verify modal functionality**: Ensure existing modal properly displays report data when names are clicked
- **Maintain sorting/filtering**: Keep existing sorting and filtering capabilities intact

### Component Structure Preservation
- **Keep existing modal infrastructure**: Modal components and methods remain unchanged
- **Preserve service integration**: ReportService calls for data retrieval remain intact
- **Maintain responsive design**: Keep existing CSS and responsive behavior

## Risks / Trade-offs

**Risk**: Removing current status section might confuse users who expect to see immediate feedback after report execution
**Mitigation**: History list automatically updates and shows the most recent report at the top when sorted by generation time

**Risk**: Users might lose access to audit trail information that was only available in current status section
**Mitigation**: Audit trail can still be accessed through "View Process" button in history list

**Trade-off**: Cleaner interface vs. immediate visibility of current run status
**Decision**: Prioritize cleaner interface since history list provides the same information with better organization

## Migration Plan

1. **Phase 1**: Remove redundant UI elements (login form, current status section)
2. **Phase 2**: Verify and complete history list functionality
3. **Phase 3**: Test complete workflow to ensure no functionality loss
4. **Phase 4**: Clean up unused component properties and methods

**Rollback Strategy**: Changes are purely template modifications, can be reverted by restoring the original HTML template

## Open Questions

- Should we remove any component properties that were only used by the deleted sections?
- Are there any CSS styles specific to the removed elements that should be cleaned up?
- Does the existing modal functionality properly handle all report data scenarios?
