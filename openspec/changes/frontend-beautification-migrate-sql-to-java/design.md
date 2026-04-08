# Design: Frontend Beautification & SQL Migration

## Overview

This document describes the technical approach for modernizing the frontend UI and migrating SQL business logic to Java.

## Architecture

### Frontend Architecture

```
┌─────────────────────────────────────────┐
│         Angular Application             │
├─────────────────────────────────────────┤
│  Modern UI Components                   │
│  - Angular Material or Tailwind CSS     │
│  - Responsive Layouts                   │
│  - Consistent Design System             │
├─────────────────────────────────────────┤
│  State Management                       │
│  - Angular Services                     │
│  - RxJS for async operations            │
├─────────────────────────────────────────┤
│  API Integration Layer                  │
│  - HTTP Client with interceptors        │
│  - Error handling                       │
└─────────────────────────────────────────┘
```

### Backend Architecture

```
┌─────────────────────────────────────────┐
│         REST API Layer                  │
│    (Spring Boot Controllers)            │
├─────────────────────────────────────────┤
│         Service Layer                   │
│  (Business Logic migrated from SQL)     │
├─────────────────────────────────────────┤
│         Repository Layer                │
│  (Data access, simplified queries)      │
├─────────────────────────────────────────┤
│         Database Layer                  │
│  (Clean SQL, no business logic)         │
└─────────────────────────────────────────┘
```

## Key Technical Decisions

### Frontend

1. **UI Framework**: Angular Material or custom Tailwind CSS components
   - Provides pre-built, accessible components
   - Consistent design language
   - Easy theming support

2. **Responsive Design**: CSS Grid and Flexbox
   - Mobile-first approach
   - Adaptive layouts for different screen sizes

3. **Icons**: Lucide or Material Icons
   - Lightweight, modern icon set
   - Easy integration with Angular

### Backend

1. **Architecture Pattern**: Layered Architecture
   - Controllers: Handle HTTP requests/responses
   - Services: Contain business logic (migrated from SQL)
   - Repositories: Data access only, no business logic
   - Entities/Models: Domain objects

2. **Business Logic Migration Strategy**:
   - Identify SQL queries with embedded logic (CASE statements, complex JOINs, aggregations)
   - Extract logic into Java service classes
   - Use Java Streams and functional programming for data transformation
   - Keep SQL queries simple (SELECT, INSERT, UPDATE, DELETE only)

3. **Testing Strategy**:
   - Unit tests for service layer using JUnit and Mockito
   - Integration tests for repository layer
   - Mock database for isolated testing

## Migration Plan for SQL Logic

### Step 1: Identify SQL with Business Logic

Look for:
- Complex CASE statements in SQL
- Nested subqueries with business rules
- Aggregations with conditional logic
- String manipulation in SQL
- Date calculations with business meaning

### Step 2: Extract to Java Services

Example transformation:

**Before (SQL with logic):**
```sql
SELECT 
    r.id,
    r.name,
    CASE 
        WHEN r.status = 'ACTIVE' AND r.end_date > NOW() THEN 'VALID'
        WHEN r.status = 'PENDING' THEN 'PENDING_APPROVAL'
        ELSE 'EXPIRED'
    END as derived_status
FROM reports r
```

**After (Java service):**
```java
public String calculateDerivedStatus(Report report) {
    if ("ACTIVE".equals(report.getStatus()) && report.getEndDate().isAfter(LocalDateTime.now())) {
        return "VALID";
    } else if ("PENDING".equals(report.getStatus())) {
        return "PENDING_APPROVAL";
    } else {
        return "EXPIRED";
    }
}
```

### Step 3: Simplify SQL Queries

```sql
SELECT id, name, status, end_date FROM reports
```

## Frontend Design Guidelines

### Color Palette
- Primary: Deep blue (#1976d2) for main actions
- Secondary: Teal (#009688) for secondary actions
- Background: Light gray (#f5f5f5) for page background
- Text: Dark gray (#333) for readability

### Typography
- Font family: Roboto or system sans-serif
- Headings: 24px, 20px, 18px
- Body: 14px
- Small: 12px

### Spacing
- Base unit: 8px
- Standard padding: 16px
- Card padding: 24px
- Section margin: 32px

### Components
- Cards with subtle shadows for content grouping
- Consistent button styles (raised, flat, icon)
- Form inputs with clear labels and validation
- Tables with sorting and pagination

## API Contract

### Endpoints to Update

| Endpoint | Method | Description |
|----------|--------|-------------|
| /api/reports | GET | List all reports |
| /api/reports/{id} | GET | Get report by ID |
| /api/reports | POST | Create new report |
| /api/reports/{id} | PUT | Update report |
| /api/reports/{id} | DELETE | Delete report |

### Response Format

```json
{
  "data": { ... },
  "status": "success",
  "message": null
}
```

## Testing Strategy

### Frontend Tests
- Component unit tests with Angular Testing Utilities
- Service tests with mocked HTTP client

### Backend Tests
- Service layer: JUnit + Mockito
- Repository layer: @DataJpaTest with H2 database
- Integration tests: @SpringBootTest with TestRestTemplate

## Documentation

1. **Workflow Documentation**: Document the AI-augmented development workflow
2. **Architecture Decision Records**: Record key technical decisions
3. **API Documentation**: Update endpoint documentation
4. **Setup Guide**: Instructions for running the modernized application
