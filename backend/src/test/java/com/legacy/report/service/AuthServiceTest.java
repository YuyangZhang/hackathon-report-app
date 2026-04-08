package com.legacy.report.service;

import com.legacy.report.model.User;
import com.legacy.report.repository.UserRepository;
import com.legacy.report.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
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
        testUser.setPassword("encodedPassword");
        testUser.setRole("USER");
    }

    @Test
    void testLoginSuccess() {
        // Given
        String username = "testuser";
        String rawPassword = "password";
        String expectedToken = "jwt-token";

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(rawPassword, testUser.getPassword())).thenReturn(true);
        when(jwtTokenProvider.createToken(username)).thenReturn(expectedToken);

        // When
        String result = authService.login(username, rawPassword);

        // Then
        assertEquals(expectedToken, result);
        verify(userRepository).findByUsername(username);
        verify(passwordEncoder).matches(rawPassword, testUser.getPassword());
        verify(jwtTokenProvider).createToken(username);
    }

    @Test
    void testLoginWithInvalidUsername() {
        // Given
        String username = "invaliduser";
        String rawPassword = "password";

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.login(username, rawPassword);
        });
        assertEquals("Invalid username or password", exception.getMessage());
        verify(userRepository).findByUsername(username);
        verify(passwordEncoder, never()).matches(any(), any());
        verify(jwtTokenProvider, never()).createToken(any());
    }

    @Test
    void testLoginWithInvalidPassword() {
        // Given
        String username = "testuser";
        String rawPassword = "wrongpassword";

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(rawPassword, testUser.getPassword())).thenReturn(false);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.login(username, rawPassword);
        });
        assertEquals("Invalid username or password", exception.getMessage());
        verify(userRepository).findByUsername(username);
        verify(passwordEncoder).matches(rawPassword, testUser.getPassword());
        verify(jwtTokenProvider, never()).createToken(any());
    }

    @Test
    void testFindByUsername() {
        // Given
        String username = "testuser";
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(testUser));

        // When
        User result = authService.findByUsername(username);

        // Then
        assertNotNull(result);
        assertEquals(testUser, result);
        verify(userRepository).findByUsername(username);
    }

    @Test
    void testFindByUsernameNotFound() {
        // Given
        String username = "nonexistent";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // When
        User result = authService.findByUsername(username);

        // Then
        assertNull(result);
        verify(userRepository).findByUsername(username);
    }
}
