package com.example.Todo_list.dto;

import com.example.Todo_list.entity.State;
import com.example.Todo_list.repository.StateRepository;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToStateConverter implements Converter<String, State> {
    private final StateRepository stateRepository;

    public StringToStateConverter(StateRepository stateRepository) {
        this.stateRepository = stateRepository;
    }

    @Override
    public State convert(String source) {
        Long id = Long.parseLong(source);
        return stateRepository.findById(id).orElse(null);
    }
}
