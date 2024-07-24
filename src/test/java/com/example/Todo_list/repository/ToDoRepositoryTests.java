package com.example.Todo_list.repository;

import com.example.Todo_list.entity.ToDo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class ToDoRepositoryTests {

    @Autowired
    private ToDoRepository toDoRepository;

    @Test
    @DisplayName("findToDoByOwnerId() returns a non-empty List<ToDo> given a valid User ID")
    void testFindToDoByOwnerIdFound() {
        Long adminUserId = 1L;
        List<ToDo> todos = toDoRepository.findTodoByOwnerId(adminUserId);
        assertNotNull(todos);
        assertFalse(todos.isEmpty());
        assertEquals(3, todos.size());

        ToDo todo = todos.get(0);
        assertEquals(1, todo.getId());
        assertEquals("Admin Project #1", todo.getTitle());
        assertEquals("Demo Description", todo.getDescription());
        assertEquals(1, todo.getOwner().getId());
        assertFalse(todo.getTasks().isEmpty());
        assertFalse(todo.getCollaborators().isEmpty());
    }

    @Test
    @DisplayName("findToDoByOwnerId() returns an empty List<ToDo> if the given User ID was invalid")
    void testFindToDoByOwnerIdNotFound() {
        assertTrue(toDoRepository.findTodoByOwnerId(999L).isEmpty());
    }
}
