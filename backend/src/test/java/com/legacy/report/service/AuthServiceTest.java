package com.legacy.report.service;

import com.legacy.report.model.User;
import com.legacy.report.repository.UserRepository;
import com.legacy.report.security.JwtTokenProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService 单元测试")
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
        testUser.setUsername("testUser");
        testUser.setPassword("encodedPassword");
        testUser.setRole("MAKER");
    }

    @AfterEach
    void tearDown() {
        // 测试后清理
    }

    @Test
    @DisplayName("应该成功登录当用户名和密码正确时")
    void should_login_successfully_when_username_and_password_are_correct() {
        // Arrange
        String username = "testUser";
        String rawPassword = "rawPassword";
        String expectedToken = "jwtToken";

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(rawPassword, testUser.getPassword())).thenReturn(true);
        when(jwtTokenProvider.createToken(username)).thenReturn(expectedToken);

        // Act
        String result = authService.login(username, rawPassword);

        // Assert
        assertThat(result).isEqualTo(expectedToken);
        verify(userRepository, times(1)).findByUsername(username);
        verify(passwordEncoder, times(1)).matches(rawPassword, testUser.getPassword());
        verify(jwtTokenProvider, times(1)).createToken(username);
    }

    @Test
    @DisplayName("应该抛出异常当用户名不存在时")
    void should_throw_exception_when_username_not_exists() {
        // Arrange
        String username = "nonExistentUser";
        String rawPassword = "rawPassword";

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> authService.login(username, rawPassword))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Invalid username or password");

        verify(userRepository, times(1)).findByUsername(username);
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtTokenProvider, never()).createToken(anyString());
    }

    @Test
    @DisplayName("应该抛出异常当密码不匹配时")
    void should_throw_exception_when_password_not_matches() {
        // Arrange
        String username = "testUser";
        String rawPassword = "wrongPassword";

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(rawPassword, testUser.getPassword())).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> authService.login(username, rawPassword))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Invalid username or password");

        verify(userRepository, times(1)).findByUsername(username);
        verify(passwordEncoder, times(1)).matches(rawPassword, testUser.getPassword());
        verify(jwtTokenProvider, never()).createToken(anyString());
    }

    @Test
    @DisplayName("应该抛出异常当用户名为空时")
    void should_throw_exception_when_username_is_null() {
        // Arrange
        String username = null;
        String rawPassword = "rawPassword";

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> authService.login(username, rawPassword))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Invalid username or password");
    }

    @Test
    @DisplayName("应该抛出异常当密码为空时")
    void should_throw_exception_when_password_is_null() {
        // Arrange
        String username = "testUser";
        String rawPassword = null;

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(rawPassword, testUser.getPassword())).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> authService.login(username, rawPassword))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Invalid username or password");
    }

    @Test
    @DisplayName("应该抛出异常当用户名为空字符串时")
    void should_throw_exception_when_username_is_empty() {
        // Arrange
        String username = "";
        String rawPassword = "rawPassword";

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> authService.login(username, rawPassword))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Invalid username or password");
    }

    @Test
    @DisplayName("应该返回用户当用户名存在时")
    void should_return_user_when_username_exists() {
        // Arrange
        String username = "testUser";

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(testUser));

        // Act
        User result = authService.findByUsername(username);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(testUser.getId());
        assertThat(result.getUsername()).isEqualTo(testUser.getUsername());
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    @DisplayName("应该返回null当用户名不存在时")
    void should_return_null_when_username_not_exists() {
        // Arrange
        String username = "nonExistentUser";

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // Act
        User result = authService.findByUsername(username);

        // Assert
        assertThat(result).isNull();
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    @DisplayName("应该返回null当用户名为null时")
    void should_return_null_when_username_is_null() {
        // Arrange
        String username = null;

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // Act
        User result = authService.findByUsername(username);

        // Assert
        assertThat(result).isNull();
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    @DisplayName("应该返回null当用户名为空字符串时")
    void should_return_null_when_username_is_empty() {
        // Arrange
        String username = "";

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // Act
        User result = authService.findByUsername(username);

        // Assert
        assertThat(result).isNull();
        verify(userRepository, times(1)).findByUsername(username);
    }
}
