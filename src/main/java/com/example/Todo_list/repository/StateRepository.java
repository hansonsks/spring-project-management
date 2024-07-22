package com.example.Todo_list.repository;

import com.example.Todo_list.entity.State;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for managing {@link State} entities.
 */
@Repository
public interface StateRepository extends JpaRepository<State, Long> {

    /**
     * Finds a state by its name.
     *
     * @param name the name of the state
     * @return the state with the given name
     */
    Optional<State> findByName(String name);
}
