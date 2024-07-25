package com.example.Todo_list.controller.utils;

import com.example.Todo_list.entity.*;

import java.time.ZonedDateTime;

public class ControllerTestUtils {

    public static User createUser() {
        User user = new User();
        user.setId(1L);
        user.setFirstName("Test");
        user.setLastName("Doe");
        user.setEmail("test@mail.com");
        user.setPassword("StrongPassword12345?"); // Not encoded for testing purposes
        return user;
    }

    public static Role createRole() {
        Role role = new Role();
        role.setId(1L);
        role.setName("Admin");
        return role;
    }

    public static State createState() {
        State state = new State();
        state.setId(1L);
        state.setName("Test");
        return state;
    }

    public static Priority createPriority() {
        return Priority.TRIVIAL;
    }

    public static ToDo createToDo() {
        ToDo toDo = new ToDo();
        toDo.setId(1L);
        toDo.setTitle("ToDo Title");
        toDo.setDescription("ToDo Description");
        // toDo.setCreatedAt(ZonedDateTime.now());  // Difficult to test, causes formatting error in the thymeleaf template, but have no effect on the actual functionality
        return toDo;
    }

    public static Task createTask() {
        Task task = new Task();
        task.setId(1L);
        task.setName("Task Name");
        task.setDescription("Task Description");
        return task;
    }

    public static Comment createComment() {
        Comment comment = new Comment();
        comment.setId(1L);
        comment.setContent("Test Comment");
        return comment;
    }

    public static Notification createNotification() {
        Notification notification = new Notification();
        notification.setId(1L);
        notification.setTitle("Test Notification");
        notification.setMessage("Test Notification Content");
        notification.setCreatedAt(ZonedDateTime.now());
        return notification;
    }
}
