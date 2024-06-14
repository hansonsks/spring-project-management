package com.example.Todo_list.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@Entity
@Data
@ToString(exclude = {"users"})
@EqualsAndHashCode(of = "id")
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Your role name must not be empty")
    @Column(name = "name", nullable = false, unique = true)
    @Size(
            min = 3,
            max = 255,
            message = "Your role name must have a minimum of 3 characters and a maximum of 255 characters"
    )
    private String name;

    @OneToMany(mappedBy = "role", cascade = CascadeType.REMOVE)
    private List<User> users;
}
