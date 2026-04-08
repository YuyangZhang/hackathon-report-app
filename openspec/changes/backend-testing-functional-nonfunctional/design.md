## Context

The backend is a Spring Boot 3.2.4 application using Java 17, with the following architecture:

- **Controllers**: REST API endpoints in `com.legacy.report.controller`
- **Services**: Business logic in `com.legacy.report.service`
- **Repositories**: Data access in `com.legacy.report.repository` and `com.legacy.report.dao`
- **Security**: JWT-based authentication with Spring Security
- **Database**: H2 in-memory database (with JPA/Hibernate)
- **Build System**: Gradle with `spring-boot-starter-test` already configured

Current state: The project has minimal to no test coverage. The `spring-boot-starter-test` dependency is present but underutilized.

## Goals / Non-Goals

**Goals:**

1. **Stress Testing**: Implement load testing using Gatling to validate API performance under concurrent load (100+ concurrent users)
2. **Unit Testing**: Achieve 70%+ code coverage for service and repository layers using JUnit 5 and Mockito
3. **API Testing**: Create integration tests for all REST endpoints using `@SpringBootTest` and `TestRestTemplate`/`MockMvc`
4. **CI Integration**: Ensure all tests can run via Gradle `test` task for CI/CD pipeline compatibility

**Non-Goals:**

- UI/frontend testing (covered by Topic 4)
- Database migration testing
- Security penetration testing
- Performance optimization (testing only, not fixing performance issues)
- Testing of external system integrations (focus on internal API contracts)

## Decisions

### Testing Framework: JUnit 5 + Mockito

**Decision**: Use JUnit 5 (Jupiter) with Mockito for unit and integration testing.

**Rationale**: 
- Already included via `spring-boot-starter-test`
- Industry standard for Spring Boot applications
- Supports modern testing patterns (nested tests, parameterized tests, extensions)

**Alternative Considered**: TestNG - rejected due to Spring Boot's native JUnit 5 integration

### API Testing: MockMvc over TestRestTemplate

**Decision**: Use MockMvc for API integration tests.

**Rationale**:
- Tests run in the same JVM as the application (faster)
- More control over request/response handling
- Better integration with Spring Security testing

**Alternative Considered**: TestRestTemplate with embedded server - rejected for slower execution speed

### Stress Testing: Gatling

**Decision**: Use Gatling for stress/load testing.

**Rationale**:
- Scala-based DSL excellent for complex scenarios
- High performance (async, non-blocking)
- Good reporting capabilities
- Can be integrated with Gradle via Gatling plugin

**Alternative Considered**: JMeter - rejected due to XML-based test plans and less maintainable code

### Test Directory Structure

**Decision**: Follow standard Maven/Gradle structure with `src/test/java` mirroring `src/main/java`.

```
src/test/java/com/legacy/report/
├── unit/
│   ├── controller/
│   ├── service/
│   └── repository/
├── integration/
│   └── api/
└── stress/
src/test/resources/
├── application-test.properties
└── gatling/
```

**Rationale**:
- Clear separation between unit, integration, and stress tests
- Gradle can run different test categories via tags
- Easy to locate and maintain tests

## Risks / Trade-offs

| Risk | Impact | Mitigation |
|------|--------|------------|
| H2 database may not reflect production database behavior | High | Document this limitation; recommend separate integration environment for full database testing |
| Stress testing on developer machines may not reflect production infrastructure | Medium | Set realistic concurrency levels (100-500 users); focus on relative performance trends |
| Test execution time may slow down CI/CD pipeline | Medium | Use `@Tag` annotations to separate fast unit tests from slower integration tests; run unit tests on every build, integration tests on PR/merge |
| Existing code may have tight coupling making testing difficult | Medium | Use Mockito to mock dependencies; refactor only if absolutely necessary for testability |
| JWT security testing complexity | Low | Use `@WithMockUser` and Spring Security test support; create test JWT utility |

## Migration Plan

### Phase 1: Infrastructure Setup
1. Add Gatling Gradle plugin and dependencies
2. Create test directory structure
3. Configure `application-test.properties` for test environment

### Phase 2: Unit Testing
1. Write unit tests for service layer (highest business logic concentration)
2. Write unit tests for utility classes
3. Write unit tests for repository layer with `@DataJpaTest`

### Phase 3: API Integration Testing
1. Create base test class with MockMvc configuration
2. Write tests for each controller endpoint
3. Include security context setup for protected endpoints

### Phase 4: Stress Testing
1. Create Gatling simulation scenarios for critical user journeys
2. Configure concurrent user simulation
3. Establish baseline performance metrics

### Rollback Strategy
- All changes are additive (new test files, updated build.gradle)
- Reverting is as simple as removing test directories and reverting build.gradle
- No production code changes required

## Open Questions

1. **Test Data**: Should we use `@Sql` annotations to load test data, or create a TestDataBuilder utility?
2. **Coverage Threshold**: Is 70% coverage the right target, or should we adjust based on initial assessment?
3. **Stress Test Environment**: Should stress tests run as part of CI or be manually triggered?
