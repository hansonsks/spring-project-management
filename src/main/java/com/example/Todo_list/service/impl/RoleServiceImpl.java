package com.example.Todo_list.service.impl;

import com.example.Todo_list.entity.Role;
import com.example.Todo_list.repository.RoleRepository;
import com.example.Todo_list.service.RoleService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private static final Logger logger = LoggerFactory.getLogger(RoleServiceImpl.class);
    private final RoleRepository roleRepository;

    @Override
    public Role findRoleByName(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Cannot find Role with null or empty name");
        }

        Optional<Role> role = roleRepository.findByName(name);

        if (role.isPresent()) {
            logger.info("RoleService.findRoleByName(): Found " + role.get());
            return role.get();
        } else {
            logger.error("RoleService.findRoleByName(): No Role with name=" + name + " was found");
            throw new EntityNotFoundException("No Role with name=" + name + " was found");
        }
    }

    @Override
    public void deleteRoleByName(String name) {
        Role role = this.findRoleByName(name);
        logger.info("RoleService.deleteRoleByName(): Deleting " + role);
        roleRepository.delete(role);
    }

    @Override
    public List<Role> findAllRoles() {
        logger.info("RoleService.findAllRoles(): Finding all Roles");
        return roleRepository.findAll();
    }
}