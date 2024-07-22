package com.example.Todo_list.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a task in the system.
 */
@Entity
@Data
@ToString(exclude = {"todo", "assignedUsers"})
@EqualsAndHashCode(of = "id")
@Table(name = "tasks")
public class Task {

    /**
     * The unique identifier of the task.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The name of the task.
     */
    @NotBlank(message = "Your task's name must not be empty")
    @Column(name = "name", nullable = false)
    @Size(
            min = 3,
            max = 255,
            message = "Your task's name must have a minimum of 3 characters and a maximum of 255 characters"
    )
    private String name;

    /**
     *  The description of the task.
     */
    @NotBlank(message = "Your task's description must not be empty")
    @Column(name = "description")
    @Size(
            max = 255,
            message = "Your task's description must have a maximum of 255 characters"
    )
    private String description;

    /**
     * The priority of the task.
     */
    @Column(name = "priority")
    @Enumerated(EnumType.STRING)
    private Priority priority;

    /**
     * The state of the task.
     */
    @ManyToOne
    @JoinColumn(name = "state_id")
    private State state;

    /**
     * The todo that the task belongs to.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "todo_id")
    private ToDo todo;

    /**
     * The comments of the task.
     */
    @OneToMany(mappedBy = "task", cascade = CascadeType.REMOVE)
    private List<Comment> comments = new ArrayList<>();

    /**
     * The users assigned to the task.
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "tasks_collaborators",
            joinColumns = @JoinColumn(name = "task_id"),
            inverseJoinColumns = @JoinColumn(name = "collaborator_id")
    )
    private List<User> assignedUsers = new ArrayList<>();

    /**
     * The deadline of the task.
     */
    @FutureOrPresent(message = "Your task's deadline must be in the future or present")
    @Column(name = "deadline")
    private ZonedDateTime deadline = null; // Can be null
}
