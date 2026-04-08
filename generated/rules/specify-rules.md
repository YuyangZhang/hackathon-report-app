# CascadeProjects Development Guidelines

Auto-generated from all feature plans. Last updated: 2026-04-08

## Active Technologies
- Java 17, TypeScript 5.7, Angular 19.2, Spring Boot 3.3.2 + Spring Boot Web/Validation/Test, JUnit 5, Mockito, Spring test slices, RestAssured, in-memory database test profile, Angular core/router/forms/http, Jest, Playwright, consumer-driven contract test tooling, Allure result adapters, GitHub Actions (002-testing-framework-scaffold)
- Production app currently uses in-memory service state; backend integration tests standardize on an in-memory database profile for scaffolded persistence-style verification (002-testing-framework-scaffold)
- Java 17 backend, Angular 17 / TypeScript 5.4 frontend, Node 18 toolchain + Spring Boot 3.2.4, Spring Data JPA, Spring JDBC, Spring Security, Angular core/forms/router, RxJS, JUnit 5, Spring Boot Tes (003-ui-backend-refactor)
- Existing relational database tables for `report_config`, `report_run`, and `report_audit_event`; H2 runtime profile already supported (003-ui-backend-refactor)

- (001-angular-figma-ui)

## Project Structure

```text
src/
tests/
```

## Commands

# Add commands for 

## Code Style

: Follow standard conventions

## Recent Changes
- 003-ui-backend-refactor: Added Java 17 backend, Angular 17 / TypeScript 5.4 frontend, Node 18 toolchain + Spring Boot 3.2.4, Spring Data JPA, Spring JDBC, Spring Security, Angular core/forms/router, RxJS, JUnit 5, Spring Boot Tes
- 002-testing-framework-scaffold: Added Java 17, TypeScript 5.7, Angular 19.2, Spring Boot 3.3.2 + Spring Boot Web/Validation/Test, JUnit 5, Mockito, Spring test slices, RestAssured, in-memory database test profile, Angular core/router/forms/http, Jest, Playwright, consumer-driven contract test tooling, Allure result adapters, GitHub Actions

- 001-angular-figma-ui: Added

<!-- MANUAL ADDITIONS START -->
<!-- MANUAL ADDITIONS END -->
