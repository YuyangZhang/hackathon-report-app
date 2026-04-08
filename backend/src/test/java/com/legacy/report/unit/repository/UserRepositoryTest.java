package com.legacy.report.unit.repository;

import com.legacy.report.model.User;
import com.legacy.report.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Repository tests for {@link UserRepository} using {@link DataJpaTest}.
 */
@Tag("unit")
@DataJpaTest
@TestPropertySource(locations = "classpath:application-test.properties")
@DisplayName("UserRepository Tests")
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private User makerUser;
    private User checkerUser;

    @BeforeEach
    void setUp() {
        makerUser = createUser("maker1", "password123", "MAKER");
        checkerUser = createUser("checker1", "password456", "CHECKER");

        entityManager.persist(makerUser);
        entityManager.persist(checkerUser);
        entityManager.flush();
    }

    private User createUser(String username, String password, String role) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setRole(role);
        return user;
    }

    @Test
    @DisplayName("findByUsername should return user when username exists")
    void findByUsername_ShouldReturnUser_WhenExists() {
        // When
        Optional<User> result = userRepository.findByUsername("maker1");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("maker1");
        assertThat(result.get().getRole()).isEqualTo("MAKER");
    }

    @Test
    @DisplayName("findByUsername should return empty when username not found")
    void findByUsername_ShouldReturnEmpty_WhenNotFound() {
        // When
        Optional<User> result = userRepository.findByUsername("nonexistent");

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findByUsername should be case sensitive")
    void findByUsername_ShouldBeCaseSensitive() {
        // When
        Optional<User> result = userRepository.findByUsername("MAKER1");

        // Then - should not find because case doesn't match
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("existsByUsername should return true for existing username")
    void existsByUsername_ShouldReturnTrue_ForExisting() {
        // When
        boolean exists = userRepository.existsByUsername("maker1");

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("existsByUsername should return false for non-existing username")
    void existsByUsername_ShouldReturnFalse_ForNonExisting() {
        // When
        boolean exists = userRepository.existsByUsername("nonexistent");

        // Then
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("save should persist new user")
    void save_ShouldPersistNewUser() {
        // Given
        User newUser = new User();
        newUser.setUsername("newuser");
        newUser.setPassword("newpass");
        newUser.setRole("MAKER,CHECKER");

        // When
        User saved = userRepository.save(newUser);

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getUsername()).isEqualTo("newuser");

        // Verify it can be retrieved
        User retrieved = entityManager.find(User.class, saved.getId());
        assertThat(retrieved).isNotNull();
        assertThat(retrieved.getRole()).isEqualTo("MAKER,CHECKER");
    }

    @Test
    @DisplayName("save should update existing user")
    void save_ShouldUpdateExistingUser() {
        // Given
        User existing = userRepository.findById(makerUser.getId()).orElseThrow();
        existing.setRole("CHECKER");

        // When
        User updated = userRepository.save(existing);

        // Then
        assertThat(updated.getRole()).isEqualTo("CHECKER");

        // Verify in database
        entityManager.flush();
        entityManager.clear();
        User retrieved = entityManager.find(User.class, makerUser.getId());
        assertThat(retrieved.getRole()).isEqualTo("CHECKER");
    }

    @Test
    @DisplayName("delete should remove user from database")
    void delete_ShouldRemoveUser() {
        // Given
        Long idToDelete = makerUser.getId();

        // When
        userRepository.deleteById(idToDelete);

        // Then
        Optional<User> result = userRepository.findById(idToDelete);
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findById should return user when exists")
    void findById_ShouldReturnUser_WhenExists() {
        // When
        Optional<User> result = userRepository.findById(checkerUser.getId());

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("checker1");
    }

    @Test
    @DisplayName("findById should return empty when user not found")
    void findById_ShouldReturnEmpty_WhenNotFound() {
        // When
        Optional<User> result = userRepository.findById(999L);

        // Then
        assertThat(result).isEmpty();
    }
}
