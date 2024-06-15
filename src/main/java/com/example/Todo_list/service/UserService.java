package com.example.Todo_list.service;

import com.example.Todo_list.entity.Role;
import com.example.Todo_list.entity.User;

import java.util.List;

public interface UserService {
    User save(User user);
    User findUserById(Long id);
    User updateUser(User user);
    void deleteUserById(Long id);
    List<User> findAllUser();
}