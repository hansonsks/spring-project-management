package com.example.Todo_list.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TaskDTO {

    private Long id;

    @NotBlank(message = "Your task's name must not be empty")
    @Size(
            min = 3,
            max = 255,
            message = "Your task's name must have a minimum of 3 characters and a maximum of 255 characters"
    )
    private String name;

    @NotBlank(message = "Your task's description must not be empty")
    @Size(
            max = 255,
            message = "Your task's description must have a maximum of 255 characters"
    )
    private String description;

    private String priority;

    private Long toDoId;

    private String state;
}
