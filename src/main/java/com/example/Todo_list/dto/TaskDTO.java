package com.example.Todo_list.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TaskDTO {

    private Long id;

    private String name;

    private String description;

    private String priority;

    private long toDoId;

    private String state;
}
