package com.legacy.report.unit.service;

import com.legacy.report.model.User;
import com.legacy.report.repository.UserRepository;
import com.legacy.report.service.CurrentUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link CurrentUserService}.
 * Tests user context retrieval and role checking.
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("CurrentUserService Unit Tests")
class CurrentUserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private CurrentUserService currentUserService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("password");
        testUser.setRole("MAKER");

        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    @DisplayName("getCurrentUserOrThrow should return user when authenticated")
    void getCurrentUserOrThrow_ShouldReturnUser_WhenAuthenticated() {
        // Given
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // When
        User result = currentUserService.getCurrentUserOrThrow();

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("testuser");
    }

    @Test
    @DisplayName("getCurrentUserOrThrow should throw exception when authentication is null")
    void getCurrentUserOrThrow_ShouldThrowException_WhenAuthenticationNull() {
        // Given
        when(securityContext.getAuthentication()).thenReturn(null);

        // When/Then
        assertThatThrownBy(() -> currentUserService.getCurrentUserOrThrow())
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("未找到认证用户");
    }

    @Test
    @DisplayName("getCurrentUserOrThrow should throw exception when authentication name is null")
    void getCurrentUserOrThrow_ShouldThrowException_WhenAuthenticationNameNull() {
        // Given
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(null);

        // When/Then
        assertThatThrownBy(() -> currentUserService.getCurrentUserOrThrow())
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("未找到认证用户");
    }

    @Test
    @DisplayName("getCurrentUserOrThrow should throw exception when user not found")
    void getCurrentUserOrThrow_ShouldThrowException_WhenUserNotFound() {
        // Given
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("unknownuser");
        when(userRepository.findByUsername("unknownuser")).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> currentUserService.getCurrentUserOrThrow())
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("用户不存在");
    }

    @Test
    @DisplayName("hasRole should return true when user has the required role")
    void hasRole_ShouldReturnTrue_WhenUserHasRole() {
        // Given
        testUser.setRole("MAKER");

        // When
        boolean result = currentUserService.hasRole(testUser, "MAKER");

        // Then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("hasRole should return false when user does not have the required role")
    void hasRole_ShouldReturnFalse_WhenUserDoesNotHaveRole() {
        // Given
        testUser.setRole("CHECKER");

        // When
        boolean result = currentUserService.hasRole(testUser, "MAKER");

        // Then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("hasRole should return true when user has multiple roles including required")
    void hasRole_ShouldReturnTrue_WhenUserHasMultipleRoles() {
        // Given
        testUser.setRole("MAKER,CHECKER");

        // When
        boolean hasMaker = currentUserService.hasRole(testUser, "MAKER");
        boolean hasChecker = currentUserService.hasRole(testUser, "CHECKER");

        // Then
        assertThat(hasMaker).isTrue();
        assertThat(hasChecker).isTrue();
    }

    @Test
    @DisplayName("hasRole should return false when user is null")
    void hasRole_ShouldReturnFalse_WhenUserIsNull() {
        // When
        boolean result = currentUserService.hasRole(null, "MAKER");

        // Then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("hasRole should return false when user role is null")
    void hasRole_ShouldReturnFalse_WhenUserRoleIsNull() {
        // Given
        testUser.setRole(null);

        // When
        boolean result = currentUserService.hasRole(testUser, "MAKER");

        // Then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("hasRole should be case insensitive")
    void hasRole_ShouldBeCaseInsensitive() {
        // Given
        testUser.setRole("maker,checker");

        // When
        boolean result = currentUserService.hasRole(testUser, "MAKER");

        // Then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("requireRole should not throw when user has role")
    void requireRole_ShouldNotThrow_WhenUserHasRole() {
        // Given
        testUser.setRole("MAKER");

        // When/Then - should not throw
        currentUserService.requireRole(testUser, "MAKER");
    }

    @Test
    @DisplayName("requireRole should throw when user does not have role")
    void requireRole_ShouldThrow_WhenUserDoesNotHaveRole() {
        // Given
        testUser.setRole("CHECKER");

        // When/Then
        assertThatThrownBy(() -> currentUserService.requireRole(testUser, "MAKER"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("当前用户没有所需角色");
    }
}
