## ADDED Requirements

### Requirement: All REST endpoints return expected HTTP status codes
Each API endpoint SHALL be tested to verify it returns correct HTTP status codes for success and error scenarios.

#### Scenario: Successful report generation request
- **WHEN** a valid POST request is made to `/api/reports` with proper authentication
- **THEN** endpoint returns HTTP 200 OK status
- **AND** response body contains the expected report data structure
- **AND** response content-type is `application/json`

#### Scenario: Unauthenticated request handling
- **WHEN** a request is made to a protected endpoint without valid JWT token
- **THEN** endpoint returns HTTP 401 Unauthorized status
- **AND** response contains appropriate error message indicating authentication failure

#### Scenario: Invalid input handling
- **WHEN** a POST request is made with missing required fields or invalid data format
- **THEN** endpoint returns HTTP 400 Bad Request status
- **AND** response contains field-level error details
- **AND** no database changes occur for invalid requests

### Requirement: API request and response contracts are validated
All API request/response payloads SHALL be tested to ensure they conform to defined JSON schemas.

#### Scenario: Report list API response structure
- **WHEN** a GET request is made to `/api/reports`
- **THEN** response body matches expected JSON schema with all required fields
- **AND** date fields are formatted as ISO 8601 strings
- **AND** numeric fields are of correct type (integer vs decimal)
- **AND** nested objects follow defined structure

#### Scenario: Report creation request validation
- **WHEN** a POST request is made to `/api/reports` with valid payload
- **THEN** request body is correctly deserialized into DTO object
- **AND** all required fields are present in the request
- **AND** optional fields can be omitted without error

### Requirement: Protected endpoints enforce security rules
All secured endpoints SHALL be tested to verify proper authentication and authorization enforcement.

#### Scenario: Valid JWT authentication
- **WHEN** a request is made with a valid JWT token in the Authorization header
- **THEN** endpoint processes the request successfully
- **AND** user identity is correctly extracted from the token

#### Scenario: Expired JWT token handling
- **WHEN** a request is made with an expired JWT token
- **THEN** endpoint returns HTTP 401 Unauthorized
- **AND** response indicates token expiration reason

#### Scenario: Insufficient permissions handling
- **WHEN** an authenticated user without required role accesses admin endpoint
- **THEN** endpoint returns HTTP 403 Forbidden status
- **AND** access is denied despite valid authentication

### Requirement: API integration tests run with Spring Boot context
All API tests SHALL use `@SpringBootTest` with MockMvc to test endpoints in a realistic container environment.

#### Scenario: Full application context test
- **WHEN** an API integration test class is annotated with `@SpringBootTest`
- **THEN** Spring Boot application context loads successfully
- **AND** MockMvc is configured and autowired
- **AND** test can perform HTTP requests against endpoints
- **AND** application uses `application-test.properties` configuration

#### Scenario: Test isolation with database cleanup
- **WHEN** multiple API tests run in sequence
- **THEN** each test starts with clean database state
- **AND** `@Transactional` or `@Sql` cleanup ensures test isolation
- **AND** tests do not interfere with each other's data
