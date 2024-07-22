package com.example.Todo_list.service;

import com.example.Todo_list.entity.Task;

import java.util.List;

public interface TaskService {

    /**
     * Saves a task to the database.
     *
     * @param task the task to be saved
     * @return the saved task
     */
    Task save(Task task);

    /**
     * Finds a task by its id.
     *
     * @param id the id of the task to be found
     * @return the found task
     */
    Task findTaskById(Long id);

    /**
     * Deletes a task by its id.
     *
     * @param id the id of the task to be deleted
     */
    void deleteTaskById(Long id);

    /**
     * Finds all tasks of a to-do list.
     *
     * @param todoId the id of the to-do list
     * @return the list of tasks
     */
    List<Task> findAllTasksOfToDo(Long todoId);

    /**
     * Finds all tasks assigned to a user.
     *
     * @param userId the id of the user
     * @return the list of tasks
     */
    List<Task> findAssignedTasksByUserId(Long userId);

    /**
     * Assigns a task to a user.
     *
     * @param taskId the id of the task
     * @param userId the id of the user
     */
    void assignTaskToUser(Long taskId, Long userId);

    /**
     * Removes a task from a user.
     *
     * @param taskId the id of the task
     * @param userId the id of the user
     */
    void removeTaskFromUser(Long taskId, Long userId);
}
