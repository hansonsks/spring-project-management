package com.example.Todo_list.unit.repository;

import com.example.Todo_list.entity.Role;
import com.example.Todo_list.repository.RoleRepository;
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
    @DisplayName("RoleRepository.findByName() finds Role by names")
    void findByNameFound() {
        assertTrue(roleRepository.findByName("ADMIN").isPresent());

        Role savedRole = roleRepository.findByName("ADMIN").get();
        assertNotNull(savedRole);
        assertEquals("ADMIN", savedRole.getName());
    }

    @Test
    @DisplayName("RoleRepository.findByName() returns an empty Optional<Role> if Role not found")
    void findByNameNotFound() {
        assertFalse(roleRepository.findByName("INVALID ROLE").isPresent());
    }
}
