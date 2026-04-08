package com.legacy.report.integration.api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for {@link com.legacy.report.controller.AuthController}.
 * Tests authentication endpoints and security scenarios.
 */
@DisplayName("AuthController API Tests")
class AuthControllerTest extends BaseApiTest {

    @Test
    @DisplayName("POST /api/auth/login should succeed with valid credentials")
    void login_WithValidCredentials_ShouldSucceed() throws Exception {
        String loginRequest = """
                {
                    "username": "maker1",
                    "password": "123456"
                }
                """;

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.token").isString())
                .andExpect(jsonPath("$.user").exists())
                .andExpect(jsonPath("$.user.username").value("maker1"))
                .andExpect(jsonPath("$.user.role").value("MAKER"));
    }

    @Test
    @DisplayName("POST /api/auth/login should fail with invalid credentials")
    void login_WithInvalidCredentials_ShouldFail() throws Exception {
        String loginRequest = """
                {
                    "username": "maker1",
                    "password": "wrongpassword"
                }
                """;

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequest))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("POST /api/auth/login should fail with non-existent username")
    void login_WithNonExistentUsername_ShouldFail() throws Exception {
        String loginRequest = """
                {
                    "username": "nonexistentuser",
                    "password": "password"
                }
                """;

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequest))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("POST /api/auth/login should fail with null username")
    void login_WithNullUsername_ShouldFail() throws Exception {
        String loginRequest = """
                {
                    "username": null,
                    "password": "password"
                }
                """;

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequest))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/auth/login should fail with null password")
    void login_WithNullPassword_ShouldFail() throws Exception {
        String loginRequest = """
                {
                    "username": "maker1",
                    "password": null
                }
                """;

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequest))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/auth/login should fail with empty credentials")
    void login_WithEmptyCredentials_ShouldFail() throws Exception {
        String loginRequest = """
                {
                    "username": "",
                    "password": ""
                }
                """;

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequest))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("GET /api/auth/profile should return user profile with valid token")
    void getProfile_WithValidToken_ShouldReturnProfile() throws Exception {
        mockMvc.perform(get("/api/auth/profile")
                        .header("Authorization", authorizationHeader(makerToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("maker1"))
                .andExpect(jsonPath("$.role").value("MAKER"));
    }

    @Test
    @DisplayName("GET /api/auth/profile should return 401 without token")
    void getProfile_WithoutToken_ShouldReturn401() throws Exception {
        mockMvc.perform(get("/api/auth/profile"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /api/auth/profile should return 401 with invalid token")
    void getProfile_WithInvalidToken_ShouldReturn401() throws Exception {
        mockMvc.perform(get("/api/auth/profile")
                        .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /api/auth/logout should succeed with valid token")
    void logout_WithValidToken_ShouldSucceed() throws Exception {
        mockMvc.perform(post("/api/auth/logout")
                        .header("Authorization", authorizationHeader(makerToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Logged out successfully"));
    }

    @Test
    @DisplayName("POST /api/auth/logout should succeed without token (stateless)")
    void logout_WithoutToken_ShouldSucceed() throws Exception {
        mockMvc.perform(post("/api/auth/logout"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Logged out successfully"));
    }

    @Test
    @DisplayName("Checker login should return token with CHECKER role")
    void checkerLogin_ShouldReturnCheckerRole() throws Exception {
        String loginRequest = """
                {
                    "username": "checker1",
                    "password": "123456"
                }
                """;

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.role").value("CHECKER"));
    }

    @Test
    @DisplayName("Admin login should return combined roles")
    void adminLogin_ShouldReturnCombinedRoles() throws Exception {
        String loginRequest = """
                {
                    "username": "admin",
                    "password": "123456"
                }
                """;

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.role").value("MAKER,CHECKER"));
    }

    @Test
    @DisplayName("Profile endpoint should not expose password")
    void getProfile_ShouldNotExposePassword() throws Exception {
        mockMvc.perform(get("/api/auth/profile")
                        .header("Authorization", authorizationHeader(makerToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    @DisplayName("Login response should include token with Bearer prefix in usage")
    void loginResponse_TokenShouldBeUsable() throws Exception {
        String loginRequest = """
                {
                    "username": "maker1",
                    "password": "123456"
                }
                """;

        // Login and get token
        String response = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequest))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Extract token
        String token = extractTokenFromResponse(response);

        // Use token to access protected endpoint
        mockMvc.perform(get("/api/reports")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    private String extractTokenFromResponse(String response) {
        int tokenStart = response.indexOf("\"token\":\"");
        if (tokenStart == -1) return null;
        tokenStart += 9;
        int tokenEnd = response.indexOf("\"", tokenStart);
        return response.substring(tokenStart, tokenEnd);
    }
}
