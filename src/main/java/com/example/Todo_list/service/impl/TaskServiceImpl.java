package com.example.Todo_list.service.impl;

import com.example.Todo_list.entity.Task;
import com.example.Todo_list.entity.User;
import com.example.Todo_list.exception.NullEntityException;
import com.example.Todo_list.repository.TaskRepository;
import com.example.Todo_list.repository.UserRepository;
import com.example.Todo_list.service.TaskService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service class for Task entity
 */
@Service
@Transactional
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private static final Logger logger = LoggerFactory.getLogger(TaskServiceImpl.class);
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    /**
     * Saves a task to the database
     *
     * @param task Task to be saved
     * @return Task that was saved
     */
    @Override
    public Task save(Task task) {
        if (task == null) {
            logger.error("TaskService.save(): Attempting to save null task");
            throw new NullEntityException(this.getClass().getName(), "Task cannot be null");
        }

        logger.info("TaskService.save(): Saving " + task);
        return taskRepository.save(task);
    }

    /**
     * Finds all tasks in the database
     *
     * @param id Id of the task to be found
     * @return List of all tasks
     */
    @Override
    public Task findTaskById(Long id) {
        Optional<Task> task = taskRepository.findById(id);

        if (task.isPresent()) {
            logger.info("TaskService.findTaskById(): " + task.get() + " was found");
            return task.get();
        } else {
            logger.error("TaskService.findTaskById(): No task with id=" + id + " was found");
            throw new EntityNotFoundException("No Task with id=" + id + " was found");
        }
    }

    /**
     * Finds all tasks in the database
     *
     * @param id Id of the task to be found
     * @return List of all tasks
     */
    @Override
    public void deleteTaskById(Long id) {
        Task task = this.findTaskById(id);
        logger.info("TaskService.deleteTaskById(): Successfully deleted " + task.toString());
        taskRepository.delete(task);
    }

    /**
     * Finds all tasks in the database
     * @param todoId Id of the todo
     * @return
     */
    @Override
    public List<Task> findAllTasksOfToDo(Long todoId) {
        logger.info("TaskService.findAllTaskOfToDo(): Finding all tasks by todoId=" + todoId);
        return taskRepository.findByTodoId(todoId);
    }

    /**
     * Finds all tasks in the database
     * @param userId Id of the user
     * @return List of all tasks
     */
    @Override
    public List<Task> findAssignedTasksByUserId(Long userId) {
        logger.info("TaskService.findTaskByAssignedUserId(): Finding all tasks by userId=" + userId);
        return taskRepository.findAssignedTasksByUserId(userId);
    }

    /**
     * Finds all tasks in the database
     * @param userId Id of the user
     * @return List of all tasks
     */
    @Override
    public void assignTaskToUser(Long taskId, Long userId) {
        Task task = this.findTaskById(taskId);
        User user = userRepository.findById(userId).orElseThrow(EntityNotFoundException::new);

        task.getAssignedUsers().add(user);
        user.getAssignedTasks().add(task);

        logger.info("TaskService.assignTaskToUser(): Assigned task " + task + " to user with id=" + userId);
        userRepository.save(user);
        taskRepository.save(task);
    }

    /**
     * Finds all tasks in the database
     * @param userId Id of the user
     * @return List of all tasks
     */
    @Override
    public void removeTaskFromUser(Long taskId, Long userId) {
        Task task = this.findTaskById(taskId);
        User user = userRepository.findById(userId).orElseThrow(EntityNotFoundException::new);

        task.getAssignedUsers().remove(user);
        user.getAssignedTasks().remove(task);

        logger.info("TaskService.removeTaskFromUser(): Removed task " + task + " from user with id=" + userId);
        userRepository.save(user);
        taskRepository.save(task);
    }
}
