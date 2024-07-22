package com.example.Todo_list.service.impl;

import com.example.Todo_list.entity.User;
import com.example.Todo_list.exception.NullEntityException;
import com.example.Todo_list.repository.OAuthUserRepository;
import com.example.Todo_list.repository.UserRepository;
import com.example.Todo_list.service.UserService;
import com.example.Todo_list.utils.PasswordService;
import com.example.Todo_list.utils.SampleTodoInitializer;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service class for managing User entities.
 */
@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserRepository userRepository;
    private final OAuthUserRepository oAuthUserRepository;
    private final PasswordService passwordService;
    private final SampleTodoInitializer todoInitializer;

    /**
     * Saves a User entity to the database.
     *
     * @param user User entity to save
     * @return the saved User entity
     */
    @Override
    public User save(User user) {
        if (user == null) {
            logger.error("UserService.save(): Attempting to save null User");
            throw new NullEntityException(this.getClass().getName(), "User cannot be null");
        }

        logger.info("UserService.save(): Encrypting user password...");
        user.setPassword(passwordService.encodePassword(user.getPassword()));

        logger.info("UserService.save(): Saving " + user);
        userRepository.save(user);

        logger.info("UserService.save(): Initializing user's todo list...");
        todoInitializer.initUserToDo(user);

        return user;
    }

    /**
     * Finds a User entity by its id.
     *
     * @param id the id of the User entity
     * @return the found User entity
     * @throws EntityNotFoundException if no User entity with the given id was found
     */
    @Override
    public User findUserById(Long id) {
        Optional<User> user = userRepository.findById(id);

        if (user.isPresent()) {
            logger.info("UserService.findUserById(): Found " + user.get());
            return user.get();
        } else {
            logger.error("UserService.findUserById(): No User with userId=" + id + " was found");
            throw new EntityNotFoundException("No User with userId=" + id + " was found");
        }
    }

    /**
     * Finds a User entity by its email.
     *
     * @param email the email of the User entity
     * @return the found User entity
     * @throws EntityNotFoundException if no User entity with the given email was found
     */
    @Override
    public User findUserByEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);

        if (user.isPresent()) {
            logger.info("UserService.findUserByEmail(): Found " + user.get());
            return user.get();
        } else {
            logger.error("UserService.findUserByEmail(): No User with email=" + email + " was found");
            throw new EntityNotFoundException("No User with email=" + email + " was found");
        }
    }

    /**
     * Updates a User entity in the database.
     *
     * @param user User entity to update
     * @return the updated User entity
     * @throws EntityNotFoundException if no User entity with the given id was found
     */
    @Override
    public User updateUser(User user) {
        if (user == null) {
            logger.error("UserService.save(): Attempting to update null User");
            throw new NullEntityException(this.getClass().getName(), "User cannot be null");
        }

        User oldUser = this.findUserById(user.getId());
        logger.info("UserService.updateUser(): Updating " + oldUser + " to " + user);
        return userRepository.save(user);
    }

    /**
     * Deletes a User entity from the database.
     *
     * @param id the id of the User entity to delete
     * @throws EntityNotFoundException if no User entity with the given id was found
     */
    @Override
    public void deleteUserById(Long id) {
        User user = this.findUserById(id);
        logger.info("UserService.deleteUserById(): Deleting " + user);
        oAuthUserRepository.deleteOAuthUserByUser(user);
        userRepository.delete(user);
    }

    /**
     * Finds all User entities in the database.
     *
     * @return a list of all User entities
     */
    @Override
    public List<User> findAllUsers() {
        logger.info("UserService.findAllUser(): Finding all users");
        return userRepository.findAll();
    }
}
