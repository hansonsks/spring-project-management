package com.example.Todo_list.repository;

import com.example.Todo_list.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for Role entity.
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    /**
     * Find role by name.
     *
     * @param name role name
     * @return role
     */
    Optional<Role> findByName(String name);
}
