package com.legacy.report.service;

import com.legacy.report.model.User;
import com.legacy.report.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CurrentUserService 单元测试")
class CurrentUserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private CurrentUserService currentUserService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testUser");
        testUser.setRole("MAKER,CHECKER");
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("应该返回当前用户当认证存在时")
    void should_return_current_user_when_authentication_exists() {
        // Arrange
        String username = "testUser";

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(testUser));

        try (MockedStatic<SecurityContextHolder> mockedContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockedContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            // Act
            User result = currentUserService.getCurrentUserOrThrow();

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getUsername()).isEqualTo(username);
            verify(userRepository, times(1)).findByUsername(username);
        }
    }

    @Test
    @DisplayName("应该抛出异常当认证为null时")
    void should_throw_exception_when_authentication_is_null() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(null);

        try (MockedStatic<SecurityContextHolder> mockedContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockedContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            // Act & Assert
            assertThatThrownBy(() -> currentUserService.getCurrentUserOrThrow())
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("未找到认证用户");

            verify(userRepository, never()).findByUsername(anyString());
        }
    }

    @Test
    @DisplayName("应该抛出异常当认证用户名为null时")
    void should_throw_exception_when_authentication_name_is_null() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(null);

        try (MockedStatic<SecurityContextHolder> mockedContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockedContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            // Act & Assert
            assertThatThrownBy(() -> currentUserService.getCurrentUserOrThrow())
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("未找到认证用户");

            verify(userRepository, never()).findByUsername(anyString());
        }
    }

    @Test
    @DisplayName("应该抛出异常当用户不存在时")
    void should_throw_exception_when_user_not_exists() {
        // Arrange
        String username = "nonExistentUser";

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        try (MockedStatic<SecurityContextHolder> mockedContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockedContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            // Act & Assert
            assertThatThrownBy(() -> currentUserService.getCurrentUserOrThrow())
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("用户不存在: " + username);

            verify(userRepository, times(1)).findByUsername(username);
        }
    }

    @Test
    @DisplayName("应该返回true当用户拥有指定角色时")
    void should_return_true_when_user_has_required_role() {
        // Arrange
        String requiredRole = "MAKER";

        // Act
        boolean result = currentUserService.hasRole(testUser, requiredRole);

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("应该返回true当用户拥有多个角色中的指定角色时")
    void should_return_true_when_user_has_required_role_in_multiple_roles() {
        // Arrange
        String requiredRole = "CHECKER";

        // Act
        boolean result = currentUserService.hasRole(testUser, requiredRole);

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("应该返回false当用户没有指定角色时")
    void should_return_false_when_user_does_not_have_required_role() {
        // Arrange
        String requiredRole = "ADMIN";

        // Act
        boolean result = currentUserService.hasRole(testUser, requiredRole);

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("应该返回false当用户为null时")
    void should_return_false_when_user_is_null() {
        // Arrange
        String requiredRole = "MAKER";

        // Act
        boolean result = currentUserService.hasRole(null, requiredRole);

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("应该返回false当用户角色为null时")
    void should_return_false_when_user_role_is_null() {
        // Arrange
        String requiredRole = "MAKER";
        testUser.setRole(null);

        // Act
        boolean result = currentUserService.hasRole(testUser, requiredRole);

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("应该返回false当用户角色为空字符串时")
    void should_return_false_when_user_role_is_empty() {
        // Arrange
        String requiredRole = "MAKER";
        testUser.setRole("");

        // Act
        boolean result = currentUserService.hasRole(testUser, requiredRole);

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("应该返回true当角色匹配不区分大小写时")
    void should_return_true_when_role_matches_case_insensitive() {
        // Arrange
        String requiredRole = "maker";
        testUser.setRole("MAKER,CHECKER");

        // Act
        boolean result = currentUserService.hasRole(testUser, requiredRole);

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("应该返回true当角色包含空格时")
    void should_return_true_when_role_contains_whitespace() {
        // Arrange
        String requiredRole = "MAKER";
        testUser.setRole(" MAKER , CHECKER ");

        // Act
        boolean result = currentUserService.hasRole(testUser, requiredRole);

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("应该不抛出异常当用户拥有指定角色时")
    void should_not_throw_exception_when_user_has_required_role() {
        // Arrange
        String requiredRole = "MAKER";

        // Act & Assert
        currentUserService.requireRole(testUser, requiredRole);
    }

    @Test
    @DisplayName("应该抛出异常当用户没有指定角色时")
    void should_throw_exception_when_user_does_not_have_required_role() {
        // Arrange
        String requiredRole = "ADMIN";

        // Act & Assert
        assertThatThrownBy(() -> currentUserService.requireRole(testUser, requiredRole))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("当前用户没有所需角色: " + requiredRole);
    }

    @Test
    @DisplayName("应该抛出异常当用户为null时")
    void should_throw_exception_when_user_is_null_for_requireRole() {
        // Arrange
        String requiredRole = "MAKER";

        // Act & Assert
        assertThatThrownBy(() -> currentUserService.requireRole(null, requiredRole))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("当前用户没有所需角色: " + requiredRole);
    }
}
