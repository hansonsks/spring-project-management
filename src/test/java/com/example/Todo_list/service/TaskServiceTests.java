package com.example.Todo_list.service;

import com.example.Todo_list.entity.Task;
import com.example.Todo_list.entity.User;
import com.example.Todo_list.exception.NullEntityException;
import com.example.Todo_list.repository.TaskRepository;
import com.example.Todo_list.repository.UserRepository;
import com.example.Todo_list.service.impl.TaskServiceImpl;
import com.example.Todo_list.service.impl.UserServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTests {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskServiceImpl taskService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private Task task;
    private User user;

    @BeforeEach
    public void beforeEach() {
        task = new Task();
        task.setId(1L);
        task.setName("Expected Task");
        task.setDescription("Expected Task Description");

        user = new User();
        user.setId(1L);
    }

    @Test
    @Transactional
    @DisplayName("save() should save a task")
    void testSaveToDo() {
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        Task actual = taskService.save(task);

        verify(taskRepository).save(any(Task.class));
        assertEquals(task, actual);
    }

    @Test
    @DisplayName("save() should throw NullEntityException when task is null")
    void testSaveInvalidToDo() {
        assertThrows(NullEntityException.class, () -> taskService.save(null));
        verify(taskRepository, times(0)).save(any(Task.class));
    }

    @Test
    @DisplayName("findTaskById() should find and return a task")
    void testFindTaskById() {
        when(taskRepository.findById(any(long.class))).thenReturn(Optional.of(task));
        assertEquals(task, taskService.findTaskById(task.getId()));
    }

    @Test
    @DisplayName("deleteTaskById() should delete a task by its id")
    void testDeleteTaskById() {
        when(taskRepository.findById(any(long.class))).thenReturn(Optional.of(task));

        taskService.deleteTaskById(task.getId());

        verify(taskRepository).findById(any(long.class));
        verify(taskRepository).delete(any(Task.class));
    }

    @Test
    @DisplayName("deleteTaskById() should throw EntityNotFoundException when task is not found")
    void testDeleteTaskByInvalidId() {
        assertThrows(EntityNotFoundException.class, () -> taskService.deleteTaskById(1L));
        verify(taskRepository, times(0)).delete(any(Task.class));
    }

    @Test
    @DisplayName("findAllTasksOfToDo() should find all tasks of a todo")
    void testFindAllTasksOfToDoNonEmpty() {
        when(taskRepository.findByTodoId(any(long.class))).thenReturn(Collections.singletonList(task));

        List<Task> actual = taskService.findAllTasksOfToDo(1L);

        assertFalse(actual.isEmpty());
        assertEquals(1, actual.size());
        verify(taskRepository).findByTodoId(any(long.class));
    }

    @Test
    @DisplayName("findAllTasksOfToDo() should return an empty list when no tasks are found")
    void testFindAllTasksOfToDoEmpty() {
        when(taskRepository.findByTodoId(any(long.class))).thenReturn(Collections.emptyList());

        List<Task> actual = taskService.findAllTasksOfToDo(1L);

        assertTrue(actual.isEmpty());
        verify(taskRepository).findByTodoId(any(long.class));
    }

    @Test
    @DisplayName("findAssignedTasksByUserId() should return a list of tasks assigned to a user")
    void testFindAssignedTaskByUserId() {
        task.getAssignedUsers().add(user);
        user.getAssignedTasks().add(task);

        when(taskRepository.findAssignedTasksByUserId(any(long.class))).thenReturn(Collections.singletonList(task));

        List<Task> actual = taskService.findAssignedTasksByUserId(user.getId());

        assertFalse(actual.isEmpty());
        assertEquals(1, actual.size());
        assertEquals(task, actual.get(0));
    }

    @Test
    @DisplayName("assignTaskToUser() should assign a task to a user")
    void testAssignTaskToUser() {
        when(taskRepository.findById(any(long.class))).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);
        when(userRepository.findById(any(long.class))).thenReturn(Optional.of(user));

        taskService.assignTaskToUser(task.getId(), user.getId());

        verify(taskRepository).findById(any(long.class));
        verify(taskRepository).save(any(Task.class));
        assertTrue(task.getAssignedUsers().contains(user));
        assertTrue(user.getAssignedTasks().contains(task));
    }

    @Test
    @DisplayName("removeTaskFromUser() should remove a task from a user")
    void testRemoveTaskFromUser() {
        task.getAssignedUsers().add(user);
        user.getAssignedTasks().add(task);

        when(taskRepository.findById(any(long.class))).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);
        when(userRepository.findById(any(long.class))).thenReturn(Optional.of(user));

        taskService.removeTaskFromUser(task.getId(), user.getId());

        verify(taskRepository).findById(any(long.class));
        verify(taskRepository).save(any(Task.class));
        assertFalse(task.getAssignedUsers().contains(user));
        assertFalse(user.getAssignedTasks().contains(task));
    }
}
