# Backend Testing Guide

This document describes how to run the comprehensive testing suite for the Legacy Report Backend.

## Test Structure

The testing framework includes three types of tests:

1. **Unit Tests** - Fast, isolated tests with mocked dependencies
   - Service layer tests using Mockito
   - Repository tests using `@DataJpaTest`
   - Utility/DTO tests

2. **Integration Tests** - API endpoint tests using Spring Boot Test
   - Controller tests with MockMvc
   - Security and authentication tests
   - Error handling tests

3. **Stress Tests** - Performance and load tests using Gatling
   - Concurrent user simulation
   - Peak load testing
   - Response time validation

## Running Tests

### Run All Tests

```bash
./gradlew test
```

### Run Unit Tests Only

```bash
./gradlew unitTest
# or
./gradlew test -Punit
```

### Run Integration Tests Only

```bash
./gradlew integrationTest
# or
./gradlew test -Pintegration
```

### Run with Coverage Verification

```bash
./gradlew checkCoverage
```

This will fail the build if coverage thresholds are not met:
- Overall: 60%
- Service layer: 70%

## Test Reports

### JaCoCo Coverage Report

After running tests, view the coverage report:

```bash
# Open HTML report
open build/reports/jacoco/test/html/index.html

# XML report is also available for CI/CD
build/reports/jacoco/test/jacocoTestReport.xml
```

### Test Results

JUnit test results are available at:

```
build/reports/tests/test/index.html
```

## Stress Testing with Gatling

### Prerequisites

Ensure the application is running on the target environment:

```bash
./gradlew bootRun
```

### Run All Gatling Simulations

```bash
./gradlew gatlingRun
```

### Run Specific Simulation

```bash
./gradlew gatlingRun-com.legacy.report.stress.ReportListSimulation
./gradlew gatlingRun-com.legacy.report.stress.AuthenticationSimulation
./gradlew gatlingRun-com.legacy.report.stress.PeakTrafficSimulation
```

### Gatling Reports

After stress testing, view the detailed HTML report:

```bash
open build/reports/gatling/[simulation-name]/index.html
```

Reports include:
- Response time percentiles (min, max, p50, p95, p99)
- Throughput metrics
- Error rates
- Request latency graphs

## Test Categories

### Unit Tests

Located in `src/test/java/com/legacy/report/unit/`

- `service/` - Service layer business logic tests
- `repository/` - Data access layer tests with H2
- `dto/` - DTO conversion and validation tests
- `exception/` - Exception handling tests

### Integration Tests

Located in `src/test/java/com/legacy/report/integration/api/`

- `BaseApiTest` - Base class for API tests
- `ReportControllerTest` - Report API endpoint tests
- `AuthControllerTest` - Authentication endpoint tests

### Stress Tests

Located in `src/test/scala/com/legacy/report/stress/`

- `ReportListSimulation` - Load test for GET /api/reports (100 concurrent users)
- `ReportExecutionSimulation` - Load test for report execution
- `AuthenticationSimulation` - Load test for auth endpoints (200 concurrent users)
- `PeakTrafficSimulation` - Peak load test (500 concurrent users)

## Key Test Scenarios

### Unit Test Coverage

| Component | Test Class | Coverage |
|-----------|-----------|----------|
| ReportService | ReportServiceTest | CRUD, validation, business logic |
| AuthService | AuthServiceTest | Login success/failure scenarios |
| SqlQueryService | SqlQueryServiceTest | SQL validation, injection prevention |
| CurrentUserService | CurrentUserServiceTest | User context, role checking |
| AuditService | AuditServiceTest | Audit event recording |
| ReportRunService | ReportRunServiceTest | Report workflow execution |

### API Test Coverage

| Endpoint | Methods | Scenarios |
|----------|---------|-----------|
| /api/auth/login | POST | Success, invalid creds, null inputs |
| /api/auth/profile | GET | With token, without token, invalid token |
| /api/auth/logout | POST | Success |
| /api/reports | GET, POST | List all, create new |
| /api/reports/{id} | GET, PUT, DELETE | Get, update, soft delete |
| /api/reports/run | POST | SQL execution, validation |
| /api/reports/generate | POST | Report generation |
| /api/reports/{id}/execute | POST | Execute and create run |
| /api/reports/{id}/export | GET | Excel export |

### Performance Targets

| Metric | Target |
|--------|--------|
| Response time (p95) | < 2 seconds |
| Response time (p99) | < 5 seconds |
| Error rate | < 1% |
| Availability | > 99% |

## Continuous Integration

### CI Pipeline Integration

Add to your CI configuration:

```yaml
# Example GitHub Actions
- name: Run Unit Tests
  run: ./gradlew unitTest

- name: Run Integration Tests
  run: ./gradlew integrationTest

- name: Verify Coverage
  run: ./gradlew jacocoTestCoverageVerification

- name: Publish Coverage Report
  uses: actions/upload-artifact@v3
  with:
    name: coverage-report
    path: build/reports/jacoco/test/html
```

## Troubleshooting

### Tests fail with database connection error

Ensure `application-test.properties` is properly configured with H2 settings.

### Integration tests fail with 401

Check that test users are created in the `@BeforeEach` setup method.

### Gatling tests fail to connect

Ensure the backend is running and accessible at the configured `baseUrl`.

### Coverage verification fails

Run tests with the `checkCoverage` task to see detailed coverage information.

## Test Data

Test data is automatically created using:
- `TestDataBuilder` - Programmatic test data creation
- `@DataJpaTest` - Isolated database per test

The H2 in-memory database is used for all repository and integration tests to ensure test isolation.

## Additional Resources

- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)
- [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/index.html)
- [Gatling Documentation](https://gatling.io/docs/gatling/)
- [Spring Boot Testing](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.testing)
