package com.example.Todo_list.repository;

import com.example.Todo_list.entity.Priority;
import com.example.Todo_list.entity.Task;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class TaskRepositoryTests {

    @Autowired
    private TaskRepository taskRepository;

    @Test
    @DisplayName("findByTodoId() returns a List<Task> associated with a ToDo given its ID")
    void testFindTasksByTodoIdFound() {
        Long todoId = 1L;
        List<Task> tasks = taskRepository.findByTodoId(todoId);
        assertNotNull(tasks);
        assertFalse(tasks.isEmpty());
        assertEquals(5, tasks.size());

        Task task = tasks.get(0);
        assertEquals(todoId, task.getId());
        assertEquals("Trivial Task", task.getName());
        assertEquals("Demo Description", task.getDescription());
        assertEquals(Priority.valueOf("TRIVIAL"), task.getPriority());
        assertEquals("Completed", task.getState().getName());
        assertNotNull(task.getTodo());
    }

    @Test
    @DisplayName("findByTodoId() returns an empty List<Task> if the TodoId was not found")
    void testFindTasksByTodoIdNotFound() {
        assertTrue(taskRepository.findByTodoId(999L).isEmpty());
    }
}
