package com.example.Todo_list.service;

import com.example.Todo_list.entity.ToDo;

import java.util.List;

/**
 * Service interface for the ToDo entity.
 */
public interface ToDoService {

    /**
     * Saves a ToDo entity.
     *
     * @param toDo the ToDo entity to save
     * @return the saved ToDo entity
     */
    ToDo save(ToDo toDo);

    /**
     * Finds a ToDo entity by its id.
     *
     * @param id the id of the ToDo entity to find
     * @return the found ToDo entity
     */
    ToDo findToDoById(Long id);

    /**
     * Deletes a ToDo entity by its id.
     *
     * @param id the id of the ToDo entity to delete
     */
    void deleteToDoById(Long id);

    /**
     * Finds all ToDo entities.
     *
     * @return a list of all ToDo entities
     */
    List<ToDo> findAllToDos();

    /**
     * Finds all ToDo entities of a user.
     *
     * @param userId the id of the user
     * @return a list of all ToDo entities of the user
     */
    List<ToDo> findAllToDoOfUserId(Long userId);

    /**
     * Adds a collaborator to a ToDo entity.
     *
     * @param toDoId         the id of the ToDo entity
     * @param collaboratorId the id of the collaborator
     */
    void addCollaborator(Long toDoId, Long collaboratorId);

    /**
     * Removes a collaborator from a ToDo entity.
     *
     * @param toDoId         the id of the ToDo entity
     * @param collaboratorId the id of the collaborator
     */
    void removeCollaborator(Long toDoId, Long collaboratorId);
}
