package com.wintersports.repositories.user;

import com.wintersports.entities.User;
import com.wintersports.enums.Role;
import com.wintersports.enums.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class IUserRepositoryTest {

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername("ivan123");
        user.setEmail("ivan@test.com");
        user.setPassword("encoded_password");
        user.setRole(Role.ATHLETE);
        user.setStatus(UserStatus.PENDING);
        entityManager.persist(user);
        entityManager.flush();
    }

    @Test
    void findByUsername_success() {
        var found = userRepository.findByUsername("ivan123");
        assertTrue(found.isPresent());
        assertEquals("ivan123", found.get().getUsername());
        assertEquals("ivan@test.com", found.get().getEmail());
    }

    @Test
    void findByUsername_notFound() {
        var found = userRepository.findByUsername("nonexistent");
        assertTrue(found.isEmpty());
    }

    @Test
    void existsByUsername_returnsTrue() {
        assertTrue(userRepository.existsByUsername("ivan123"));
    }

    @Test
    void existsByUsername_returnsFalse() {
        assertFalse(userRepository.existsByUsername("nonexistent"));
    }

    @Test
    void existsByEmail_returnsTrue() {
        assertTrue(userRepository.existsByEmail("ivan@test.com"));
    }

    @Test
    void existsByEmail_returnsFalse() {
        assertFalse(userRepository.existsByEmail("nonexistent@test.com"));
    }

    @Test
    void findAll_returnsAllUsers() {
        var users = userRepository.findAll();
        assertEquals(1, users.size());
    }

    @Test
    void deleteById_success() {
        userRepository.deleteById(user.getId());
        assertFalse(userRepository.existsByUsername("ivan123"));
    }
}