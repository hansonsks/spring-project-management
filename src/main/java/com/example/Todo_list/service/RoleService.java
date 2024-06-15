package com.example.Todo_list.service;

import com.example.Todo_list.entity.Role;

import java.util.List;

public interface RoleService {

    Role findRoleByName(String name);

    void deleteRoleByName(String name);

    List<Role> findAllRoles();
}
