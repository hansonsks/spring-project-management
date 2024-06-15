package com.example.Todo_list.service;

import com.example.Todo_list.entity.ToDo;

import java.util.List;

public interface ToDoService {
    ToDo save(ToDo toDo);
    ToDo findToDoById(Long id);
    void deleteToDoById(Long id);
    List<ToDo> findAllToDo();
    List<ToDo> findAllToDoOfUserId(Long userId);
    void addCollaborator(Long toDoId, Long collaboratorId);
    void removeCollaborator(Long toDoId, Long collaboratorId);
}
