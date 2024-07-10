package com.example.Todo_list.unit.service;

import com.example.Todo_list.entity.Role;
import com.example.Todo_list.repository.RoleRepository;
import com.example.Todo_list.service.impl.RoleServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RoleServiceTests {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleServiceImpl roleService;

    private Role role;

    @BeforeEach
    public void beforeEach() {
        role = new Role();
        role.setId(1L);
        role.setName("Admin");
    }

    @Test
    @DisplayName("findRoleById() returns an existing Role given an ID")
    void testFindRoleById() {
        when(roleRepository.findById(any(long.class))).thenReturn(Optional.of(role));
        assertEquals(role, roleService.findRoleById(role.getId()));
    }

    @Test
    @DisplayName("testFindRoleByName() returns an existing Role given a name")
    void testFindRoleByName() {
        when(roleRepository.findByName(any(String.class))).thenReturn(Optional.of(role));
        assertEquals(role, roleService.findRoleByName(role.getName()));
    }

    @Test
    @DisplayName("testFindRoleByName() throws EntityNotFoundException if a Role with invalid names")
    void testFindRoleByInvalidName() {
        assertThrows(EntityNotFoundException.class, () -> roleService.findRoleByName("Invalid Role"));
        assertThrows(IllegalArgumentException.class, () -> roleService.findRoleByName(""));
        assertThrows(IllegalArgumentException.class, () -> roleService.findRoleByName(null));
    }

    @Test
    @DisplayName("testFindAllRoles() returns the list of all Role when it is empty")
    void testFindAllRolesEmpty() {
        List<Role> expected = Collections.emptyList();
        when(roleRepository.findAll()).thenReturn(expected);

        List<Role> actual = roleService.findAllRoles();
        assertEquals(actual, expected);

        verify(roleRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("testFindAllRoles() returns the list of all Role when it is non-empty")
    void testFindAllRolesNonEmpty() {
        List<Role> expected = Collections.singletonList(role);
        when(roleRepository.findAll()).thenReturn(expected);

        List<Role> actual = roleService.findAllRoles();
        assertEquals(actual, expected);

        verify(roleRepository, times(1)).findAll();
    }
}
