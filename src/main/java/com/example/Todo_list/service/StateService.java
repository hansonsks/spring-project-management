package com.example.Todo_list.service;

import com.example.Todo_list.entity.State;

import java.util.List;

public interface StateService {

    State save(State state);

    State findStateByName(String name);

    void deleteStateByName(String name);

    List<State> findAllStates();
}
