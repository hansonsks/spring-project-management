package com.example.Todo_list.unit.service;

import com.example.Todo_list.entity.ToDo;
import com.example.Todo_list.entity.User;
import com.example.Todo_list.exception.NullEntityException;
import com.example.Todo_list.exception.UserIsToDoOwnerException;
import com.example.Todo_list.repository.ToDoRepository;
import com.example.Todo_list.repository.UserRepository;
import com.example.Todo_list.service.impl.ToDoServiceImpl;
import jakarta.persistence.EntityNotFoundException;
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
public class ToDoServiceTests {

    @Mock
    private ToDoRepository toDoRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ToDoServiceImpl toDoService;

    private ToDo toDo;
    private User user;

    @BeforeEach
    public void beforeEach() {
        user = new User();
        user.setId(1L);

        toDo = new ToDo();
        toDo.setId(1L);
        toDo.setTitle("Title");
        toDo.setDescription("Description");

//        user.setTodoList(List.of(toDo));
//        toDo.setOwner(user);
    }

    @Test
    @DisplayName("Test")
    void saveToDo() {
        when(toDoRepository.save(any(ToDo.class))).thenReturn(toDo);

        ToDo actual = toDoService.save(toDo);

        verify(toDoRepository).save(any(ToDo.class));
        assertEquals(toDo, actual);
    }

    @Test
    @DisplayName("Test")
    void saveNullToDo() {
        assertThrows(NullEntityException.class, () -> toDoService.save(null));
        verify(toDoRepository, times(0)).save(any(ToDo.class));
    }

    @Test
    @DisplayName("Test")
    void testFindToDoById() {
        when(toDoRepository.findById(any(long.class))).thenReturn(Optional.of(toDo));

        ToDo actual = toDoService.findToDoById(toDo.getId());

        verify(toDoRepository).findById(any(long.class));
        assertEquals(toDo, actual);
    }

    @Test
    @DisplayName("Test")
    void testFindToDoByInvalidId() {
        assertThrows(EntityNotFoundException.class, () -> toDoService.findToDoById(999L));
    }

    @Test
    @DisplayName("Test")
    void testDeleteToDoById() {
        when(toDoRepository.findById(any(long.class))).thenReturn(Optional.of(toDo));

        toDoService.deleteToDoById(toDo.getId());

        verify(toDoRepository).findById(any(long.class));
        verify(toDoRepository, times(1)).delete(any(ToDo.class));
    }

    @Test
    @DisplayName("Test")
    void testDeleteToDoByInvalidId() {
        when(toDoRepository.findById(any(long.class))).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> toDoService.deleteToDoById(999L));

        verify(toDoRepository).findById(any(long.class));
        verify(toDoRepository, times(0)).delete(any(ToDo.class));
    }

    @Test
    @DisplayName("Test")
    void testFindAllToDosEmpty() {
        when(toDoRepository.findAll()).thenReturn(Collections.emptyList());

        List<ToDo> actual = toDoService.findAllToDos();

        assertTrue(actual.isEmpty());
        verify(toDoRepository).findAll();
    }

    @Test
    @DisplayName("Test")
    void testFindAllToDosNonEmpty() {
        when(toDoRepository.findAll()).thenReturn(Collections.singletonList(toDo));

        List<ToDo> actual = toDoService.findAllToDos();

        assertEquals(1, actual.size());
        verify(toDoRepository).findAll();
    }

    @Test
    @DisplayName("Test")
    void testFindAllToDosOfUserIdEmpty() {
        when(toDoRepository.findTodoByOwnerId(any(long.class))).thenReturn(Collections.emptyList());

        List<ToDo> actual = toDoService.findAllToDoOfUserId(1L);

        assertTrue(actual.isEmpty());
        verify(toDoRepository).findTodoByOwnerId(any(long.class));
    }

    @Test
    @DisplayName("Test")
    void testFindAllToDosOfUserIdNonEmpty() {
        when(toDoRepository.findTodoByOwnerId(any(long.class))).thenReturn(Collections.singletonList(toDo));

        List<ToDo> actual = toDoService.findAllToDoOfUserId(1L);

        assertEquals(1, actual.size());
        assertEquals(toDo, actual.get(0));
        verify(toDoRepository).findTodoByOwnerId(any(long.class));
    }

    @Test
    @DisplayName("Test")
    void testFindAllToDosOfInvalidUserId() {
        when(toDoService.findAllToDoOfUserId(any(long.class))).thenThrow(new EntityNotFoundException());
        assertThrows(EntityNotFoundException.class, () -> toDoService.findAllToDoOfUserId(999L));
    }

    @Test
    @DisplayName("Test")
    void testAddCollaborator() {
        when(toDoRepository.findById(any(long.class))).thenReturn(Optional.of(toDo));
        when(userRepository.findById(any(long.class))).thenReturn(Optional.of(user));

        toDoService.addCollaborator(toDo.getId(), user.getId());

        assertTrue(toDo.getCollaborators().contains(user));
        assertTrue(user.getCollaborators().contains(toDo));
    }

    @Test
    @DisplayName("Test")
    void testAddInvalidCollaborator() {
        when(toDoRepository.findById(any(long.class))).thenReturn(Optional.of(toDo));
        assertThrows(EntityNotFoundException.class, () -> toDoService.addCollaborator(toDo.getId(), user.getId()));
    }

    @Test
    @DisplayName("Test")
    void testAddCollaboratorToInvalidToDo() {
        assertThrows(EntityNotFoundException.class, () -> toDoService.addCollaborator(toDo.getId(), user.getId()));
    }

    @Test
    @DisplayName("Test")
    void testRemoveCollaborator() {
        toDo.getCollaborators().add(user);

        when(toDoRepository.findById(any(long.class))).thenReturn(Optional.of(toDo));
        when(userRepository.findById(any(long.class))).thenReturn(Optional.of(user));

        toDoService.removeCollaborator(toDo.getId(), user.getId());

        assertFalse(toDo.getCollaborators().contains(user));
    }

    @Test
    @DisplayName("Test")
    void testRemoveInvalidCollaborator() {
        when(toDoRepository.findById(any(long.class))).thenReturn(Optional.of(toDo));

        assertThrows(EntityNotFoundException.class, () -> toDoService.removeCollaborator(toDo.getId(), user.getId()));
    }

    @Test
    @DisplayName("Test")
    void testRemoveCollaboratorFromInvalidToDo() {
        assertThrows(EntityNotFoundException.class, () -> toDoService.removeCollaborator(toDo.getId(), user.getId()));
    }

    @Test
    @DisplayName("Test")
    void testRemoveOwnerFromCollaborators() {
        toDo.setOwner(user);

        when(toDoRepository.findById(any(long.class))).thenReturn(Optional.of(toDo));
        when(userRepository.findById(any(long.class))).thenReturn(Optional.of(toDo.getOwner()));

        assertThrows(UserIsToDoOwnerException.class, () -> toDoService.removeCollaborator(toDo.getId(), user.getId()));
    }
}
