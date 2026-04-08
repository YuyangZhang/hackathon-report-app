# Project Summary: Hackathon Report Application

## Overview

This is a **full-stack report management application** built for a Windsurf Hackathon. It demonstrates a modernized approach to legacy report systems with a Maker-Checker approval workflow, JWT-based authentication, and Excel export capabilities.

## Architecture

### Backend (Spring Boot 3.2.4)
- **Framework**: Spring Boot 3.2.4 with Java 17
- **Port**: 8080
- **Database**: H2 (in-memory) with pre-configured users
- **Security**: JWT-based authentication with Spring Security
- **Key Libraries**:
  - JJWT for JWT handling
  - JXLS for Excel report generation
  - Spring Data JPA for data access
  - Spring Boot Actuator with Prometheus metrics

### Frontend (Angular 17)
- **Framework**: Angular 17.3
- **Port**: 4200
- **Testing**: Karma + Jasmine
- **Key Features**: Role-based UI routing, report execution, approval workflows

## Project Structure

```
backend/
├── src/main/java/com/legacy/report/
│   ├── ReportApplication.java      # Entry point
│   ├── config/                      # Security & CORS configuration
│   ├── controller/                  # REST API endpoints
│   │   ├── AuthController.java      # JWT login/logout
│   │   ├── ReportController.java    # Report CRUD & execution
│   │   └── ReportRunController.java # Approval workflow APIs
│   ├── service/                     # Business logic
│   │   ├── ReportService.java       # Report management
│   │   ├── ReportRunService.java    # Report execution & approval
│   │   ├── ReportExcelExportService.java  # Excel export
│   │   ├── AuditService.java        # Audit trail tracking
│   │   ├── AuthService.java         # Authentication logic
│   │   └── CurrentUserService.java  # User context
│   ├── model/                       # JPA entities
│   ├── repository/                  # Data access layer
│   ├── dto/                         # Data transfer objects
│   ├── security/                    # JWT filters & utilities
│   ├── dao/                         # Data access objects
│   └── exception/                   # Custom exceptions
├── build.gradle                     # Gradle build configuration
└── gradlew/gradlew.bat              # Gradle wrapper

frontend/
├── src/app/
│   ├── components/
│   │   ├── auth/login.component.ts       # Login page
│   │   ├── report/report-viewer.component.ts     # Report viewer
│   │   └── report/report-run-flow.component.ts  # Approval flow
│   ├── services/                   # HTTP services
│   ├── models/                      # TypeScript interfaces
│   ├── guards/                      # Route guards
│   └── interceptors/                # HTTP interceptors
├── package.json                     # npm dependencies
└── angular.json                     # Angular CLI configuration
```

## Key Features

### 1. Authentication & Authorization
- JWT-based stateless authentication
- Role-based access control (MAKER, CHECKER, ADMIN)
- Default users: `admin/123456`, `maker1/123456`, `checker1/123456`

### 2. Report Management
- Predefined report definitions stored in database
- SQL-based report execution with parameter support
- Report execution history tracking

### 3. Maker-Checker Workflow
- **Maker**: Executes reports, views results, submits for approval
- **Checker**: Reviews submitted reports, approves/rejects with comments
- **Audit Trail**: Complete timeline of approval decisions

### 4. Excel Export
- Export report results to Excel format
- Available to both Makers and Checkers (post-approval)

## API Endpoints

| Endpoint | Method | Description | Role |
|----------|--------|-------------|------|
| `/api/auth/login` | POST | Authenticate and get JWT | Any |
| `/api/auth/profile` | GET | Get current user profile | Any |
| `/api/auth/logout` | POST | Logout (client-side token clear) | Any |
| `/api/reports` | GET | List all reports | Any |
| `/api/reports/{id}` | GET | Get report definition | Any |
| `/api/reports/{id}/execute` | POST | Execute a report | Any |
| `/api/report-runs/{id}/submit` | POST | Submit for approval | Maker |
| `/api/report-runs/{id}/decision` | POST | Approve/Reject | Checker |
| `/api/report-runs/{id}/audit` | GET | Get approval timeline | Any |

## Getting Started

### Prerequisites
- Node.js 18+ and npm
- Java JDK 17+
- Git

### First-time Setup
```bash
cd frontend
npm install
cd ..
```

### Running the Application

**Backend** (Terminal 1):
```bash
cd backend
./gradlew bootRun      # Linux/macOS
.\gradlew bootRun      # Windows
```

**Frontend** (Terminal 2):
```bash
cd frontend
npm start
```

### Access URLs
- Frontend: http://localhost:4200
- Backend API: http://localhost:8080
- H2 Console: http://localhost:8080/h2-console

## Testing

**Backend**:
```bash
cd backend
./gradlew test
```

**Frontend**:
```bash
cd frontend
npm test
```

## Hackathon Context

This project addresses **Topic 1** of the Windsurf Hackathon:
- **Frontend Beautification**: Modern Angular UI with role-based views
- **SQL-to-Java Migration**: Report business logic migrated from SQL to maintainable Java service layer
- **Workflow Showcase**: End-to-end AI-augmented development workflow demonstration

## Technology Stack Summary

| Layer | Technology | Version |
|-------|------------|---------|
| Backend | Spring Boot | 3.2.4 |
| Backend | Java | 17 |
| Backend | Database | H2 (in-memory) |
| Security | JWT | 0.11.5 |
| Export | JXLS | 2.14.0 |
| Frontend | Angular | 17.3.0 |
| Frontend | TypeScript | 5.4 |
| Build | Gradle | 8.x (wrapper) |
| Test | JUnit 5 | Jupiter |
| Test | Karma + Jasmine | 6.4 / 5.1 |
