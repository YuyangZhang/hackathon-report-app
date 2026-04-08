package com.legacy.report.unit.dto;

import com.legacy.report.dto.LoginRequest;
import com.legacy.report.dto.LoginResponse;
import com.legacy.report.dto.UserDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link LoginRequest} and {@link LoginResponse} DTOs.
 */
@Tag("unit")
@DisplayName("Login DTOs Unit Tests")
class LoginDtoTest {

    // ============== LoginRequest Tests ==============

    @Test
    @DisplayName("LoginRequest should store username correctly")
    void loginRequest_ShouldStoreUsername() {
        // Given
        LoginRequest request = new LoginRequest();

        // When
        request.setUsername("testuser");

        // Then
        assertThat(request.getUsername()).isEqualTo("testuser");
    }

    @Test
    @DisplayName("LoginRequest should store password correctly")
    void loginRequest_ShouldStorePassword() {
        // Given
        LoginRequest request = new LoginRequest();

        // When
        request.setPassword("secret123");

        // Then
        assertThat(request.getPassword()).isEqualTo("secret123");
    }

    @Test
    @DisplayName("LoginRequest should handle null username")
    void loginRequest_ShouldHandleNullUsername() {
        // Given
        LoginRequest request = new LoginRequest();
        request.setUsername(null);

        // Then
        assertThat(request.getUsername()).isNull();
    }

    @Test
    @DisplayName("LoginRequest should handle null password")
    void loginRequest_ShouldHandleNullPassword() {
        // Given
        LoginRequest request = new LoginRequest();
        request.setPassword(null);

        // Then
        assertThat(request.getPassword()).isNull();
    }

    @Test
    @DisplayName("LoginRequest should handle empty strings")
    void loginRequest_ShouldHandleEmptyStrings() {
        // Given
        LoginRequest request = new LoginRequest();
        request.setUsername("");
        request.setPassword("");

        // Then
        assertThat(request.getUsername()).isEmpty();
        assertThat(request.getPassword()).isEmpty();
    }

    // ============== LoginResponse Tests ==============

    @Test
    @DisplayName("LoginResponse should store token correctly")
    void loginResponse_ShouldStoreToken() {
        // Given
        LoginResponse response = new LoginResponse();
        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...";

        // When
        response.setToken(token);

        // Then
        assertThat(response.getToken()).isEqualTo(token);
    }

    @Test
    @DisplayName("LoginResponse should store user correctly")
    void loginResponse_ShouldStoreUser() {
        // Given
        LoginResponse response = new LoginResponse();
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setUsername("testuser");
        userDto.setRole("MAKER");

        // When
        response.setUser(userDto);

        // Then
        assertThat(response.getUser()).isNotNull();
        assertThat(response.getUser().getUsername()).isEqualTo("testuser");
    }

    @Test
    @DisplayName("LoginResponse should handle null token")
    void loginResponse_ShouldHandleNullToken() {
        // Given
        LoginResponse response = new LoginResponse();
        response.setToken(null);

        // Then
        assertThat(response.getToken()).isNull();
    }

    @Test
    @DisplayName("LoginResponse should handle null user")
    void loginResponse_ShouldHandleNullUser() {
        // Given
        LoginResponse response = new LoginResponse();
        response.setUser(null);

        // Then
        assertThat(response.getUser()).isNull();
    }

    @Test
    @DisplayName("LoginResponse should store complex JWT token")
    void loginResponse_ShouldStoreComplexJwtToken() {
        // Given
        LoginResponse response = new LoginResponse();
        String complexToken = "Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWV9.TJVA95OrM7E2cBab30RMHrHDcEfxjoYZgeFONFh7HgQ";

        // When
        response.setToken(complexToken);

        // Then
        assertThat(response.getToken()).isEqualTo(complexToken);
    }

    @Test
    @DisplayName("LoginResponse user should be independent object")
    void loginResponse_ShouldHaveIndependentUserObject() {
        // Given
        LoginResponse response = new LoginResponse();
        UserDto userDto = new UserDto();
        userDto.setUsername("original");
        response.setUser(userDto);

        // When - modify original
        userDto.setUsername("modified");

        // Then - response should reflect the change (same reference)
        assertThat(response.getUser().getUsername()).isEqualTo("modified");
    }

    // ============== Combined Tests ==============

    @Test
    @DisplayName("LoginRequest and LoginResponse work together")
    void loginRequestAndResponse_WorkTogether() {
        // Given - simulate login flow
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("password123");

        // Simulate creating a response
        LoginResponse response = new LoginResponse();
        response.setToken("generated-jwt-token");

        UserDto userDto = new UserDto();
        userDto.setUsername(request.getUsername());
        userDto.setRole("MAKER");
        response.setUser(userDto);

        // Then
        assertThat(response.getUser().getUsername()).isEqualTo(request.getUsername());
        assertThat(response.getToken()).isNotNull();
    }
}
