package com.example.Todo_list.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

/**
 * The entity class represents the state of the task.
 */
@Entity
@Data
@ToString(exclude = {"tasks"})
@EqualsAndHashCode(of = "id")
@Table(name = "states")
public class State {

    /**
     * The unique identifier of the state.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The name of the state.
     */
    @NotBlank(message = "The state name must not be empty")
    @Column(name = "name", nullable = false, unique = true)
    private String name;

    /**
     * The list of tasks that are in this state.
     */
    @OneToMany(mappedBy = "state", cascade = CascadeType.REMOVE)
    private List<Task> tasks;
}
