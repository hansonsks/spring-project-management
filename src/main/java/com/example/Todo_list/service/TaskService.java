package com.example.Todo_list.service;

import com.example.Todo_list.entity.Task;

import java.util.List;

public interface TaskService {

    Task save(Task task);

    Task findTaskById(Long id);

    void deleteTaskById(Long id);

    List<Task> findAllTasksOfToDo(Long todoId);

    List<Task> findAssignedTasksByUserId(Long userId);

    void assignTaskToUser(Long taskId, Long userId);

    void removeTaskFromUser(Long taskId, Long userId);
}
