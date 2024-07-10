package com.example.Todo_list.unit.service;

import com.example.Todo_list.entity.Task;
import com.example.Todo_list.exception.NullEntityException;
import com.example.Todo_list.repository.TaskRepository;
import com.example.Todo_list.service.impl.TaskServiceImpl;
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

    private Task task;

    @BeforeEach
    public void beforeEach() {
        task = new Task();
        task.setId(1L);
        task.setName("Expected Task");
        task.setDescription("Expected Task Description");
    }

    @Test
    @Transactional
    @DisplayName("Test")
    void testSaveToDo() {
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        Task actual = taskService.save(task);

        verify(taskRepository).save(any(Task.class));
        assertEquals(task, actual);
    }

    @Test
    @DisplayName("Test")
    void testSaveInvalidToDo() {
        assertThrows(NullEntityException.class, () -> taskService.save(null));
        verify(taskRepository, times(0)).save(any(Task.class));
    }

    @Test
    @DisplayName("Test")
    void testFindToDoById() {
        when(taskRepository.findById(any(long.class))).thenReturn(Optional.of(task));
        assertEquals(task, taskService.findTaskById(task.getId()));
    }

    @Test
    @DisplayName("Test")
    void testDeleteTaskById() {
        when(taskRepository.findById(any(long.class))).thenReturn(Optional.of(task));

        taskService.deleteTaskById(task.getId());

        verify(taskRepository).findById(any(long.class));
        verify(taskRepository).delete(any(Task.class));
    }

    @Test
    @DisplayName("Test")
    void testDeleteTaskByInvalidId() {
        assertThrows(EntityNotFoundException.class, () -> taskService.deleteTaskById(1L));
        verify(taskRepository, times(0)).delete(any(Task.class));
    }

    @Test
    @DisplayName("Test")
    void testFindAllTasksOfToDoNonEmpty() {
        when(taskRepository.findByTodoId(any(long.class))).thenReturn(Collections.singletonList(task));

        List<Task> actual = taskService.findAllTasksOfToDo(1L);

        assertFalse(actual.isEmpty());
        assertEquals(1, actual.size());
        verify(taskRepository).findByTodoId(any(long.class));
    }

    @Test
    @DisplayName("Test")
    void testFindAllTasksOfToDoEmpty() {
        when(taskRepository.findByTodoId(any(long.class))).thenReturn(Collections.emptyList());

        List<Task> actual = taskService.findAllTasksOfToDo(1L);

        assertTrue(actual.isEmpty());
        verify(taskRepository).findByTodoId(any(long.class));
    }
}
