package com.example.Todo_list.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Data
@ToString(exclude = {"todo"})
@EqualsAndHashCode(of = "id")
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Your task's name must not be empty")
    @Column(name = "name", nullable = false)
    @Size(
            min = 3,
            max = 255,
            message = "Your task's name must have a minimum of 3 characters and a maximum of 255 characters"
    )
    private String name;

    @Column(name = "description")
    @Size(
            max = 255,
            message = "Your task's description must have a maximum of 255 characters"
    )
    private String description;

    @Column(name = "priority")
    @Enumerated(EnumType.STRING)
    private Priority priority;

    @ManyToOne
    @JoinColumn(name = "state_id")
    private State state;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "todo_id")
    private ToDo todo;
}
