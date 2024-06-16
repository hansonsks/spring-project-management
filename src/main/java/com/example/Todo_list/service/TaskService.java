package com.example.Todo_list.service;

import com.example.Todo_list.entity.Task;

import java.util.List;

public interface TaskService {

    Task save(Task task);

    Task findTaskById(Long id);

    void deleteTaskById(Long id);

    List<Task> findAllTaskOfToDo(Long todoId);
}
