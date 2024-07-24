package com.example.Todo_list.service.impl;

import com.example.Todo_list.entity.State;
import com.example.Todo_list.exception.NullEntityException;
import com.example.Todo_list.repository.StateRepository;
import com.example.Todo_list.service.StateService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service class for managing states
 */
@Service
@Transactional
@RequiredArgsConstructor
public class StateServiceImpl implements StateService {

    private static final Logger logger = LoggerFactory.getLogger(StateServiceImpl.class);
    private final StateRepository stateRepository;

    /**
     * Saves a state to the database
     *
     * @param state the state to be saved
     * @return the saved state
     */
    @Override
    public State save(State state) {
        if (state == null) {
            logger.error("StateService.createState(): Attempting to create null state");
            throw new NullEntityException(this.getClass().getName(), "State cannot be null");
        }

        logger.info("StateService.createState(): Saving " + state);
        return stateRepository.save(state);
    }

    @Override
    public State findStateById(Long id) {
        if (id == null) {
            logger.error("StateService.findStateById(): Cannot find State with null id");
            throw new NullEntityException("Cannot find State with null id");
        }

        Optional<State> state = stateRepository.findById(id);
        if (state.isPresent()) {
            logger.info("StateService.findStateById(): " + state.get() + " was found");
            return state.get();
        } else {
            logger.error("StateService.findStateById(): No state with id=" + id + " was found");
            throw new EntityNotFoundException("No State with id=" + id + " was found");
        }
    }

    /**
     * Finds a state by its name
     *
     * @param name the name of the state to be found
     * @return the state with the given name
     */
    @Override
    public State findStateByName(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Cannot find State with null or empty name");
        }

        Optional<State> state = stateRepository.findByName(name);
        if (state.isPresent()) {
            logger.info("StateService.findStateByName(): " + state.get() + " was found");
            return state.get();
        } else {
            logger.error("StateService.findStateByName(): No state with name=" + name + " was found");
            throw new EntityNotFoundException("No State with name=" + name + " was found");
        }
    }

    /**
     * Deletes a state by its name
     *
     * @param name the name of the state to be deleted
     */
    @Override
    public void deleteStateByName(String name) {
        State state = this.findStateByName(name);
        logger.info("StateService.deleteStateByName(): Deleting " + state);
        stateRepository.delete(state);
    }

    /**
     * Finds all states
     *
     * @return a list of all states
     */
    @Override
    public List<State> findAllStates() {
        logger.info("StateService.findAllState(): Finding all states");
        return stateRepository.findAll();
    }
}
