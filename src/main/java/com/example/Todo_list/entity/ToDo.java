package com.example.Todo_list.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@ToString(exclude = {"tasks", "collaborators"})
@EqualsAndHashCode(of = "id")
@Table(name = "todos")
public class ToDo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Your TODO title must not be empty")
    @Column(name = "title", nullable = false)
    @Size(
            min = 3,
            max = 255,
            message = "TODO items must have a title between 3 and 255 characters"
    )
    private String title;

    @Column(name = "description", nullable = false)
    @Size(
            max = 255,
            message = "TODO descriptions must not exceed 255 characters"
    )
    private String description;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    @NotBlank(message = "The creation date must not be empty")
    @Column(name = "created_at", nullable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "toDo", cascade = CascadeType.REMOVE)
    private List<Task> tasks = new ArrayList<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "todos_collaborators",
            joinColumns = @JoinColumn(name = "todo_id"),
            inverseJoinColumns = @JoinColumn(name = "collaborator_id")
    )
    private List<User> collaborators = new ArrayList<>();
}
