package com.legacy.report.unit.service;

import com.legacy.report.model.User;
import com.legacy.report.repository.UserRepository;
import com.legacy.report.security.JwtTokenProvider;
import com.legacy.report.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link AuthService}.
 * Tests authentication logic with mocked dependencies.
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService Unit Tests")
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private AuthService authService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("encodedPassword123");
        testUser.setRole("MAKER");
    }

    @Test
    @DisplayName("login should return token when credentials are valid")
    void login_ShouldReturnToken_WhenCredentialsValid() {
        // Given
        String rawPassword = "password123";
        String expectedToken = "mock-jwt-token";

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(rawPassword, testUser.getPassword())).thenReturn(true);
        when(jwtTokenProvider.createToken("testuser")).thenReturn(expectedToken);

        // When
        String result = authService.login("testuser", rawPassword);

        // Then
        assertThat(result).isEqualTo(expectedToken);
        verify(userRepository).findByUsername("testuser");
        verify(passwordEncoder).matches(rawPassword, "encodedPassword123");
        verify(jwtTokenProvider).createToken("testuser");
    }

    @Test
    @DisplayName("login should throw exception when username not found")
    void login_ShouldThrowException_WhenUsernameNotFound() {
        // Given
        when(userRepository.findByUsername("unknownuser")).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> authService.login("unknownuser", "password123"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Invalid username or password");

        verify(userRepository).findByUsername("unknownuser");
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtTokenProvider, never()).createToken(anyString());
    }

    @Test
    @DisplayName("login should throw exception when password does not match")
    void login_ShouldThrowException_WhenPasswordDoesNotMatch() {
        // Given
        String rawPassword = "wrongpassword";

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(rawPassword, testUser.getPassword())).thenReturn(false);

        // When/Then
        assertThatThrownBy(() -> authService.login("testuser", rawPassword))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Invalid username or password");

        verify(userRepository).findByUsername("testuser");
        verify(passwordEncoder).matches(rawPassword, "encodedPassword123");
        verify(jwtTokenProvider, never()).createToken(anyString());
    }

    @Test
    @DisplayName("login should throw exception for null username")
    void login_ShouldThrowException_WhenUsernameIsNull() {
        // When/Then
        assertThatThrownBy(() -> authService.login(null, "password123"))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("login should throw exception for null password")
    void login_ShouldThrowException_WhenPasswordIsNull() {
        // Given
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(null, testUser.getPassword())).thenReturn(false);

        // When/Then
        assertThatThrownBy(() -> authService.login("testuser", null))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Invalid username or password");
    }

    @Test
    @DisplayName("findByUsername should return user when found")
    void findByUsername_ShouldReturnUser_WhenFound() {
        // Given
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // When
        User result = authService.findByUsername("testuser");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("testuser");
        assertThat(result.getRole()).isEqualTo("MAKER");
        verify(userRepository).findByUsername("testuser");
    }

    @Test
    @DisplayName("findByUsername should return null when user not found")
    void findByUsername_ShouldReturnNull_WhenNotFound() {
        // Given
        when(userRepository.findByUsername("unknownuser")).thenReturn(Optional.empty());

        // When
        User result = authService.findByUsername("unknownuser");

        // Then
        assertThat(result).isNull();
        verify(userRepository).findByUsername("unknownuser");
    }

    @Test
    @DisplayName("findByUsername should handle case sensitive usernames")
    void findByUsername_ShouldHandleCaseSensitivity() {
        // Given
        when(userRepository.findByUsername("TestUser")).thenReturn(Optional.empty());

        // When
        User result = authService.findByUsername("TestUser");

        // Then
        assertThat(result).isNull();
        verify(userRepository).findByUsername("TestUser");
    }
}
