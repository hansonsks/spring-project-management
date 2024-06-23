package com.example.Todo_list.service.impl;

import com.example.Todo_list.entity.Task;
import com.example.Todo_list.exception.NullEntityException;
import com.example.Todo_list.repository.TaskRepository;
import com.example.Todo_list.service.TaskService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private static final Logger logger = LoggerFactory.getLogger(TaskServiceImpl.class);
    private final TaskRepository taskRepository;

    @Override
    public Task save(Task task) {
        if (task == null) {
            logger.error("TaskService.save(): Attempting to save null task");
            throw new NullEntityException(this.getClass().getName(), "Task cannot be null");
        }

        logger.info("TaskService.save(): Saving " + task);
        return taskRepository.save(task);
    }

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

    @Override
    public void deleteTaskById(Long id) {
        Task task = this.findTaskById(id);
        logger.info("TaskService.deleteTaskById(): Successfully deleted " + task.toString());
        taskRepository.delete(task);
    }

    @Override
    public List<Task> findAllTaskOfToDo(Long todoId) {
        logger.info("TaskService.findAllTaskOfToDo(): Finding all tasks by todoId" + todoId);
        return taskRepository.findByTodoId(todoId);
    }
}
