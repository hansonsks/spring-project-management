package com.example.Todo_list.service;

import com.example.Todo_list.entity.User;

import java.util.List;

/**
 * Service class for managing User entities.
 */
public interface UserService {

    /**
     * Save user to database
     *
     * @param user user to save
     * @return saved user
     */
    User save(User user);

    /**
     * Find user by id
     *
     * @param id user id
     * @return user with given id
     */
    User findUserById(Long id);

    /**
     * Find user by email
     *
     * @param email user email
     * @return user with given email
     */
    User findUserByEmail(String email);

    /**
     * Update user
     *
     * @param user user to update
     * @return updated user
     */
    User updateUser(User user);

    /**
     * Delete user by id
     *
     * @param id user id
     */
    void deleteUserById(Long id);

    /**
     * Find all users
     *
     * @return list of all users
     */
    List<User> findAllUsers();
}
