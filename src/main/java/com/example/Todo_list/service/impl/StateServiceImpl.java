package com.example.Todo_list.service.impl;

import com.example.Todo_list.entity.State;
import com.example.Todo_list.exception.NullEntityException;
import com.example.Todo_list.repository.StateRepository;
import com.example.Todo_list.service.StateService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StateServiceImpl implements StateService {

    private static final Logger logger = LoggerFactory.getLogger(StateServiceImpl.class);
    private final StateRepository stateRepository;

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

    @Override
    public void deleteStateByName(String name) {
        State state = this.findStateByName(name);
        logger.info("StateService.deleteStateByName(): Deleting " + state);
        stateRepository.delete(state);
    }

    @Override
    public List<State> findAllState() {
        logger.info("StateService.getAllState(): Finding all states");
        return stateRepository.findAll();
    }
}
