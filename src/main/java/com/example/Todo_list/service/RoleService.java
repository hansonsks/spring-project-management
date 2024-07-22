package com.example.Todo_list.service;

import com.example.Todo_list.entity.Role;

import java.util.List;

/**
 * Service class for managing Role entities.
 */
public interface RoleService {

    /**
     * Find Role by id
     *
     * @param id - id of Role
     * @return Role
     */
    Role findRoleById(Long id);

    /**
     * Find Role by name
     *
     * @param name - name of Role
     * @return Role
     */
    Role findRoleByName(String name);

    /**
     * Delete Role by name
     *
     * @param name - name of Role
     */
    void deleteRoleByName(String name);

    /**
     * Find all Roles
     *
     * @return List of Roles
     */
    List<Role> findAllRoles();
}
