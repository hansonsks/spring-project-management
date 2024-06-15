package com.example.Todo_list.service.impl;

import com.example.Todo_list.entity.Role;
import com.example.Todo_list.entity.User;
import com.example.Todo_list.exception.NullEntityException;
import com.example.Todo_list.repository.RoleRepository;
import com.example.Todo_list.repository.UserRepository;
import com.example.Todo_list.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public User save(User user) {
        if (user == null) {
            logger.error("UserService.save(): Attempting to save null User");
            throw new NullEntityException(this.getClass().getName(), "User cannot be null");
        }

        logger.info("UserService.save(): Encrypting user password...");
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        logger.info("UserService.save(): Saving " + user);
        return userRepository.save(user);
    }

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

    @Override
    public void deleteUserById(Long id) {
        User user = this.findUserById(id);
        logger.info("UserService.deleteUserById(): Deleting " + user);
        userRepository.delete(user);
    }

    @Override
    public List<User> findAllUser() {
        logger.info("UserService.findAllUser(): Finding all users");
        return userRepository.findAll();
    }
}
