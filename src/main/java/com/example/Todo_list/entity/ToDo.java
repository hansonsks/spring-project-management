package com.example.Todo_list.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a single TODO item.
 */
@Entity
@Data
@ToString(exclude = {"tasks", "collaborators"})
@EqualsAndHashCode(of = "id")
@Table(name = "todos")
public class ToDo {

    /**
     * The unique identifier of the TODO item.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The title of the TODO item.
     */
    @NotBlank(message = "Your TODO title must not be empty")
    @Column(name = "title", nullable = false)
    @Size(
            min = 3,
            max = 255,
            message = "TODO items must have a title between 3 and 255 characters"
    )
    private String title;

    /**
     * The description of the TODO item.
     */
    @NotBlank(message = "Your TODO description must not be empty")
    @Column(name = "description", nullable = false)
    @Size(
            max = 255,
            message = "TODO descriptions must not exceed 255 characters"
    )
    private String description;

    /**
     * The owner of the TODO item.
     */
    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    /**
     * The date and time when the TODO item was created.
     */
    @Column(name = "created_at", nullable = false)
    @CreationTimestamp
    private ZonedDateTime createdAt;

    /**
     * The list of tasks associated with the TODO item.
     */
    @OneToMany(mappedBy = "todo", cascade = CascadeType.REMOVE)
    private List<Task> tasks = new ArrayList<>();

    /**
     * The list of collaborators associated with the TODO item.
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "todos_collaborators",
            joinColumns = @JoinColumn(name = "todo_id"),
            inverseJoinColumns = @JoinColumn(name = "collaborator_id")
    )
    private List<User> collaborators = new ArrayList<>();
}
