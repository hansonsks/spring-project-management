package com.example.Todo_list.service;

import com.example.Todo_list.entity.State;

import java.util.List;

/**
 * Service class for managing State entities.
 */
public interface StateService {

    /**
     * Save a state.
     *
     * @param state the entity to save.
     * @return the persisted entity.
     */
    State save(State state);

    /**
     * Get the state by id.
     *
     * @param id the id of the state.
     * @return the state.
     */
    State findStateById(Long id);

    /**
     * Get all the states.
     *
     * @return the list of states.
     */
    State findStateByName(String name);

    /**
     * Delete the state by name.
     * @param name the name of the state.
     */
    void deleteStateByName(String name);

    /**
     * Get all the states.
     *
     * @return the list of states.
     */
    List<State> findAllStates();
}
