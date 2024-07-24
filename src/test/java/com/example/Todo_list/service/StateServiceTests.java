package com.example.Todo_list.service;

import com.example.Todo_list.entity.State;
import com.example.Todo_list.exception.NullEntityException;
import com.example.Todo_list.repository.StateRepository;
import com.example.Todo_list.service.impl.StateServiceImpl;
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
public class StateServiceTests {

    @Mock
    private StateRepository stateRepository;

    @InjectMocks
    private StateServiceImpl stateService;

    private State state;

    @BeforeEach
    public void beforeEach() {
        state = new State();
        state.setId(1L);
        state.setName("Test State");
    }

    @Test
    @DisplayName("save() should save a state and return it")
    void testSaveState() {
        when(stateRepository.save(any(State.class))).thenReturn(state);

        State actual = stateService.save(state);

        assertEquals(state, actual);
        verify(stateRepository).save(any(State.class));
    }

    @Test
    @DisplayName("save() should throw NullEntityException when state is null")
    void testSaveInvalidState() {
        assertThrows(NullEntityException.class, () -> stateService.save(null));
        verify(stateRepository, times(0)).save(any(State.class));
    }

    @Test
    @DisplayName("findById() should find a state by its ID")
    void testFindStateById() {
        when(stateRepository.findById(any(Long.class))).thenReturn(Optional.of(state));

        State actual = stateService.findStateById(state.getId());

        assertEquals(state, actual);
        verify(stateRepository).findById(state.getId());
    }

    @Test
    @DisplayName("findById() should throw NullEntityException when ID is null")
    void testFindStateByInvalidId() {
        assertThrows(NullEntityException.class, () -> stateService.findStateById(null));
        verify(stateRepository, times(0)).findById(any(Long.class));
    }

    @Test
    @DisplayName("findStateByName() should find a state by its name")
    void testFindStateByName() {
        when(stateRepository.findByName(any(String.class))).thenReturn(Optional.of(state));

        State actual = stateService.findStateByName(state.getName());

        assertEquals(state, actual);
        verify(stateRepository).findByName(state.getName());
    }

    @Test
    @DisplayName("findStateByName() should throw IllegalArgumentException when name is null or empty")
    void testFindStateByInvalidName() {
        assertThrows(IllegalArgumentException.class, () -> stateService.findStateByName(null));
        assertThrows(IllegalArgumentException.class, () -> stateService.findStateByName(""));
        assertThrows(EntityNotFoundException.class, () -> stateService.findStateByName("Invalid State"));
    }

    @Test
    @DisplayName("deleteStateByName() should delete a state by its name")
    void testDeleteStateByName() {
        when(stateRepository.findByName(any(String.class))).thenReturn(Optional.of(state));

        stateService.deleteStateByName(state.getName());

        verify(stateRepository).findByName(any(String.class));
        verify(stateRepository).delete(any(State.class));
    }

    @Test
    @DisplayName("deleteStateByName() should throw IllegalArgumentException when name is null or empty")
    void testDeleteStateByInvalidName() {
        assertThrows(IllegalArgumentException.class, () -> stateService.deleteStateByName(null));
        assertThrows(IllegalArgumentException.class, () -> stateService.deleteStateByName(""));
        assertThrows(EntityNotFoundException.class, () -> stateService.deleteStateByName("Invalid State"));
        verify(stateRepository, times(0)).delete(any(State.class));
    }

    @Test
    @DisplayName("findAllStates() should return an empty list when no states are found")
    void testFindAllStatesEmpty() {
        when(stateRepository.findAll()).thenReturn(Collections.emptyList());

        List<State> actual = stateService.findAllStates();

        assertTrue(actual.isEmpty());
    }

    @Test
    @DisplayName("findAllStates() should return a non-empty list of states when states are found")
    void testFindAllStatesNonEmpty() {
        when(stateRepository.findAll()).thenReturn(Collections.singletonList(state));

        List<State> actual = stateService.findAllStates();

        assertFalse(actual.isEmpty());
        assertEquals(1, actual.size());
        assertEquals(state, actual.get(0));
    }
}
