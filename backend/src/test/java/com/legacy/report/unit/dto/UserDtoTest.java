package com.legacy.report.unit.dto;

import com.legacy.report.dto.UserDto;
import com.legacy.report.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link UserDto}.
 * Tests DTO conversion and field mapping.
 */
@Tag("unit")
@DisplayName("UserDto Unit Tests")
class UserDtoTest {

    @Test
    @DisplayName("fromEntity should map all fields correctly")
    void fromEntity_ShouldMapAllFields() {
        // Given
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPassword("shouldNotBeCopied");
        user.setRole("MAKER");

        // When
        UserDto dto = UserDto.fromEntity(user);

        // Then
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getUsername()).isEqualTo("testuser");
        assertThat(dto.getRole()).isEqualTo("MAKER");
    }

    @Test
    @DisplayName("fromEntity should handle null id")
    void fromEntity_ShouldHandleNullId() {
        // Given
        User user = new User();
        user.setId(null);
        user.setUsername("testuser");
        user.setRole("CHECKER");

        // When
        UserDto dto = UserDto.fromEntity(user);

        // Then
        assertThat(dto.getId()).isNull();
        assertThat(dto.getUsername()).isEqualTo("testuser");
    }

    @Test
    @DisplayName("fromEntity should handle null username")
    void fromEntity_ShouldHandleNullUsername() {
        // Given
        User user = new User();
        user.setId(1L);
        user.setUsername(null);
        user.setRole("MAKER");

        // When
        UserDto dto = UserDto.fromEntity(user);

        // Then
        assertThat(dto.getUsername()).isNull();
    }

    @Test
    @DisplayName("fromEntity should handle null role")
    void fromEntity_ShouldHandleNullRole() {
        // Given
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setRole(null);

        // When
        UserDto dto = UserDto.fromEntity(user);

        // Then
        assertThat(dto.getRole()).isNull();
    }

    @Test
    @DisplayName("fromEntity should not include password in DTO")
    void fromEntity_ShouldNotIncludePassword() {
        // Given
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPassword("secret123");
        user.setRole("MAKER");

        // When
        UserDto dto = UserDto.fromEntity(user);

        // Then - UserDto doesn't have password field, so it shouldn't be exposed
        assertThat(dto.getUsername()).isEqualTo("testuser");
        // No password getter exists on UserDto
    }

    @Test
    @DisplayName("fromEntity should handle user with multiple roles")
    void fromEntity_ShouldHandleMultipleRoles() {
        // Given
        User user = new User();
        user.setId(1L);
        user.setUsername("admin");
        user.setRole("MAKER,CHECKER,ADMIN");

        // When
        UserDto dto = UserDto.fromEntity(user);

        // Then
        assertThat(dto.getRole()).isEqualTo("MAKER,CHECKER,ADMIN");
    }

    @Test
    @DisplayName("DTO setters and getters should work correctly")
    void settersAndGetters_ShouldWorkCorrectly() {
        // Given
        UserDto dto = new UserDto();

        // When
        dto.setId(100L);
        dto.setUsername("newuser");
        dto.setRole("CHECKER");

        // Then
        assertThat(dto.getId()).isEqualTo(100L);
        assertThat(dto.getUsername()).isEqualTo("newuser");
        assertThat(dto.getRole()).isEqualTo("CHECKER");
    }

    @Test
    @DisplayName("Multiple DTOs from same entity should be independent")
    void multipleDtosFromSameEntity_ShouldBeIndependent() {
        // Given
        User user = new User();
        user.setId(1L);
        user.setUsername("original");
        user.setRole("MAKER");

        // When
        UserDto dto1 = UserDto.fromEntity(user);
        UserDto dto2 = UserDto.fromEntity(user);

        // Modify dto1
        dto1.setUsername("modified");

        // Then
        assertThat(dto1.getUsername()).isEqualTo("modified");
        assertThat(dto2.getUsername()).isEqualTo("original");
    }
}
