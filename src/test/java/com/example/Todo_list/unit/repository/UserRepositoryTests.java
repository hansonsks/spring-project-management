package com.example.Todo_list.unit.repository;

import com.example.Todo_list.entity.User;
import com.example.Todo_list.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class UserRepositoryTests {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("UserRepository.findByEmail() finds a User based on their email")
    void testFindByEmailFound() {
        // Test using admin email
        String adminEmail = "admin@mail.com";
        assertTrue(userRepository.findByEmail(adminEmail).isPresent());

        User adminUser = userRepository.findByEmail(adminEmail).get();
        assertNotNull(adminUser);
        assertEquals(adminEmail, adminUser.getEmail());

        // Test using user email
        String userEmail  = "user@mail.com";
        assertTrue(userRepository.findByEmail(userEmail).isPresent());

        User userUser = userRepository.findByEmail(userEmail).get();
        assertNotNull(userUser);
        assertEquals(userEmail, userUser.getEmail());
    }

    @Test
    @DisplayName("UserRepository.findByEmail() returns an empty Optional<User> if no user with the email exists")
    void testFindByEmailNotFound() {
        String invalidEmail = "error@mail.com";
        assertFalse(userRepository.findByEmail(invalidEmail).isPresent());
    }
}
