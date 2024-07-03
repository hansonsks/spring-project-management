package com.example.Todo_list.unit.repository;

import com.example.Todo_list.entity.State;
import com.example.Todo_list.repository.StateRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class StateRepositoryTests {

    @Autowired
    private StateRepository stateRepository;

    @Test
    @DisplayName("StateRepository.findByName() finds a State given its name")
    void testFindByNameFound() {
        List<String> stateNames = List.of("New", "In Progress", "Under Review", "Completed");
        for (String stateName : stateNames) {
            assertTrue(stateRepository.findByName(stateName).isPresent());

            State testState = stateRepository.findByName(stateName).get();
            assertNotNull(testState);
            assertEquals(stateName, testState.getName());
        }
    }

    @Test
    @DisplayName("StateRepository.findByName() returns an empty Optional<State> if no State with the given name exists")
    void testFindByNameNotFound() {
        assertFalse(stateRepository.findByName("Unknown State").isPresent());
    }
}
