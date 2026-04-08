package com.legacy.report.integration.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.legacy.report.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Base class for API integration tests.
 * Provides common setup for MockMvc, authentication, and utility methods.
 * Test users are loaded by TestDataInitializer on application startup.
 */
@Tag("integration")
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
public class BaseApiTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected JwtTokenProvider jwtTokenProvider;

    protected String makerToken;
    protected String checkerToken;
    protected String adminToken;

    @BeforeEach
    void baseSetUp() throws Exception {
        // Generate tokens for different roles via login endpoint
        // Test users are created by UserInitializer with password "123456"
        makerToken = obtainAccessToken("maker1", "123456");
        checkerToken = obtainAccessToken("checker1", "123456");
        adminToken = obtainAccessToken("admin", "123456");
    }

    /**
     * Obtain JWT access token by logging in.
     */
    protected String obtainAccessToken(String username, String password) throws Exception {
        String loginRequest = String.format(
                "{\"username\": \"%s\", \"password\": \"%s\"}",
                username, password
        );

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        return extractTokenFromResponse(response);
    }

    /**
     * Extract token from JSON response.
     */
    private String extractTokenFromResponse(String response) {
        // Simple extraction - in real code use JsonPath or ObjectMapper
        int tokenStart = response.indexOf("\"token\":\"");
        if (tokenStart == -1) return null;
        tokenStart += 9; // length of "token":"
        int tokenEnd = response.indexOf("\"", tokenStart);
        return response.substring(tokenStart, tokenEnd);
    }

    /**
     * Create Authorization header with Bearer token.
     */
    protected String authorizationHeader(String token) {
        return "Bearer " + token;
    }

    /**
     * Set authentication in SecurityContext for testing.
     */
    protected void setAuthentication(String username, String role) {
        Authentication auth = new UsernamePasswordAuthenticationToken(
                username,
                null,
                java.util.List.of(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_" + role))
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    /**
     * Clear authentication after test.
     */
    protected void clearAuthentication() {
        SecurityContextHolder.clearContext();
    }

    /**
     * Convert object to JSON string.
     */
    protected String toJson(Object obj) throws Exception {
        return objectMapper.writeValueAsString(obj);
    }
}
