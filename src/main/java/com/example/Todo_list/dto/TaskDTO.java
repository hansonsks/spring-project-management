package com.example.Todo_list.dto;

import com.example.Todo_list.entity.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * A DTO class for the Task entity
 */
@Data
@NoArgsConstructor
public class TaskDTO {

    /**
     * The task's id
     */
    private Long id;

    /**
     * The task's name
     */
    @NotBlank(message = "Your task's name must not be empty")
    @Size(
            min = 3,
            max = 255,
            message = "Your task's name must have a minimum of 3 characters and a maximum of 255 characters"
    )
    private String name;

    /**
     * The task's description
     */
    @NotBlank(message = "Your task's description must not be empty")
    @Size(
            max = 255,
            message = "Your task's description must have a maximum of 255 characters"
    )
    private String description;

    /**
     * The task's priority
     */
    private String priority;

    /**
     * The task's state

     */
    private Long toDoId;

    /**
     * The task's owner's unique identifier
     */
    private Long ownerId;

    /**
     * The task's state
     */
    private String state;

    /**
     * The task's assigned users
     */
    private List<User> assignedUsers;

    /**
     * The task's deadline
     */
    private LocalDateTime deadline;
}
