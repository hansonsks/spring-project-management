package com.example.Todo_list.repository;

import com.example.Todo_list.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for User entity
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find user by email
     *
     * @param email user's email
     * @return user with given email
     */
    Optional<User> findByEmail(String email);
}
