package com.example.Todo_list.repository;

import com.example.Todo_list.entity.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class RoleRepositoryTests {

    @Autowired
    private RoleRepository roleRepository;

    @Test
    @DisplayName("findByName() finds Role by names")
    void testFindByNameFound() {
        assertTrue(roleRepository.findByName("ADMIN").isPresent());

        Role savedRole = roleRepository.findByName("ADMIN").get();
        assertNotNull(savedRole);
        assertEquals("ADMIN", savedRole.getName());
    }

    @Test
    @DisplayName("findByName() returns an empty Optional<Role> if Role not found")
    void testFindByNameNotFound() {
        assertFalse(roleRepository.findByName("INVALID ROLE").isPresent());
    }
}
