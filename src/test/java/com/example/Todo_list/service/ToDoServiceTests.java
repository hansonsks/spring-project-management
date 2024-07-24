package com.example.Todo_list.service;

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
    @DisplayName("save() should save a ToDo and return it")
    void saveToDo() {
        when(toDoRepository.save(any(ToDo.class))).thenReturn(toDo);

        ToDo actual = toDoService.save(toDo);

        verify(toDoRepository).save(any(ToDo.class));
        assertEquals(toDo, actual);
    }

    @Test
    @DisplayName("save() should throw NullEntityException when saving null ToDo")
    void saveNullToDo() {
        assertThrows(NullEntityException.class, () -> toDoService.save(null));
        verify(toDoRepository, times(0)).save(any(ToDo.class));
    }

    @Test
    @DisplayName("findToDoById() should return a ToDo by its id")
    void testFindToDoById() {
        when(toDoRepository.findById(any(long.class))).thenReturn(Optional.of(toDo));

        ToDo actual = toDoService.findToDoById(toDo.getId());

        verify(toDoRepository).findById(any(long.class));
        assertEquals(toDo, actual);
    }

    @Test
    @DisplayName("findToDoById() should throw EntityNotFoundException when ToDo is not found given an id")
    void testFindToDoByInvalidId() {
        assertThrows(EntityNotFoundException.class, () -> toDoService.findToDoById(999L));
    }

    @Test
    @DisplayName("findDeleteToDoById() should delete a ToDo by its id")
    void testDeleteToDoById() {
        when(toDoRepository.findById(any(long.class))).thenReturn(Optional.of(toDo));

        toDoService.deleteToDoById(toDo.getId());

        verify(toDoRepository).findById(any(long.class));
        verify(toDoRepository, times(1)).delete(any(ToDo.class));
    }

    @Test
    @DisplayName("findDeleteToDoById() should throw EntityNotFoundException when ToDo is not found given an id")
    void testDeleteToDoByInvalidId() {
        when(toDoRepository.findById(any(long.class))).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> toDoService.deleteToDoById(999L));

        verify(toDoRepository).findById(any(long.class));
        verify(toDoRepository, times(0)).delete(any(ToDo.class));
    }

    @Test
    @DisplayName("findAllToDos() should return an empty list if no ToDos are found")
    void testFindAllToDosEmpty() {
        when(toDoRepository.findAll()).thenReturn(Collections.emptyList());

        List<ToDo> actual = toDoService.findAllToDos();

        assertTrue(actual.isEmpty());
        verify(toDoRepository).findAll();
    }

    @Test
    @DisplayName("findAllToDos() should return a list of ToDos if they are found")
    void testFindAllToDosNonEmpty() {
        when(toDoRepository.findAll()).thenReturn(Collections.singletonList(toDo));

        List<ToDo> actual = toDoService.findAllToDos();

        assertEquals(1, actual.size());
        verify(toDoRepository).findAll();
    }

    @Test
    @DisplayName("findAllToDosOfUserId() should return an empty list if no ToDos are found for a user")
    void testFindAllToDosOfUserIdEmpty() {
        when(toDoRepository.findTodoByOwnerId(any(long.class))).thenReturn(Collections.emptyList());

        List<ToDo> actual = toDoService.findAllToDoOfUserId(1L);

        assertTrue(actual.isEmpty());
        verify(toDoRepository).findTodoByOwnerId(any(long.class));
    }

    @Test
    @DisplayName("findAllToDosOfUserId() should return a list of ToDos if they are found for a user")
    void testFindAllToDosOfUserIdNonEmpty() {
        when(toDoRepository.findTodoByOwnerId(any(long.class))).thenReturn(Collections.singletonList(toDo));

        List<ToDo> actual = toDoService.findAllToDoOfUserId(1L);

        assertEquals(1, actual.size());
        assertEquals(toDo, actual.get(0));
        verify(toDoRepository).findTodoByOwnerId(any(long.class));
    }

    @Test
    @DisplayName("findAllToDosOfUserId() should throw EntityNotFoundException when no ToDos are found for a user")
    void testFindAllToDosOfInvalidUserId() {
        when(toDoService.findAllToDoOfUserId(any(long.class))).thenThrow(new EntityNotFoundException());
        assertThrows(EntityNotFoundException.class, () -> toDoService.findAllToDoOfUserId(999L));
    }

    @Test
    @DisplayName("addCollaborator() should add a collaborator to a ToDo")
    void testAddCollaborator() {
        when(toDoRepository.findById(any(long.class))).thenReturn(Optional.of(toDo));
        when(userRepository.findById(any(long.class))).thenReturn(Optional.of(user));

        toDoService.addCollaborator(toDo.getId(), user.getId());

        assertTrue(toDo.getCollaborators().contains(user));
        assertTrue(user.getCollaborators().contains(toDo));
    }

    @Test
    @DisplayName("addCollaborator() should throw EntityNotFoundException when adding a collaborator to an invalid ToDo")
    void testAddInvalidCollaborator() {
        when(toDoRepository.findById(any(long.class))).thenReturn(Optional.of(toDo));
        assertThrows(EntityNotFoundException.class, () -> toDoService.addCollaborator(toDo.getId(), user.getId()));
    }

    @Test
    @DisplayName("addCollaborator() should throw EntityNotFoundException when adding a collaborator to an invalid ToDo")
    void testAddCollaboratorToInvalidToDo() {
        assertThrows(EntityNotFoundException.class, () -> toDoService.addCollaborator(toDo.getId(), user.getId()));
    }

    @Test
    @DisplayName("removeCollaborator() should remove a collaborator from a ToDo")
    void testRemoveCollaborator() {
        toDo.getCollaborators().add(user);

        when(toDoRepository.findById(any(long.class))).thenReturn(Optional.of(toDo));
        when(userRepository.findById(any(long.class))).thenReturn(Optional.of(user));

        toDoService.removeCollaborator(toDo.getId(), user.getId());

        assertFalse(toDo.getCollaborators().contains(user));
    }

    @Test
    @DisplayName("removeCollaborator() should throw EntityNotFoundException when removing an invalid collaborator from a ToDo")
    void testRemoveInvalidCollaborator() {
        when(toDoRepository.findById(any(long.class))).thenReturn(Optional.of(toDo));

        assertThrows(EntityNotFoundException.class, () -> toDoService.removeCollaborator(toDo.getId(), user.getId()));
    }

    @Test
    @DisplayName("removeCollaborator() should throw EntityNotFoundException when removing a collaborator from an invalid ToDo")
    void testRemoveCollaboratorFromInvalidToDo() {
        assertThrows(EntityNotFoundException.class, () -> toDoService.removeCollaborator(toDo.getId(), user.getId()));
    }

    @Test
    @DisplayName("removeCollaborator() should throw UserIsToDoOwnerException when removing the owner from a ToDo")
    void testRemoveOwnerFromCollaborators() {
        toDo.setOwner(user);

        when(toDoRepository.findById(any(long.class))).thenReturn(Optional.of(toDo));
        when(userRepository.findById(any(long.class))).thenReturn(Optional.of(toDo.getOwner()));

        assertThrows(UserIsToDoOwnerException.class, () -> toDoService.removeCollaborator(toDo.getId(), user.getId()));
    }
}
