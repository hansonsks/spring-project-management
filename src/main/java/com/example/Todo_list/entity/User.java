package com.example.Todo_list.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@ToString(exclude = {"todoList", "collaborators"})
@EqualsAndHashCode(of = "id")
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Your first name must not be empty")
    @Pattern(
            regexp = "[A-Z][a-z]+",
            message = "Your first name must begin with a capital letter and contain letters only"
    )
    @Column(name = "first_name", nullable = false)
    @Size(
            min = 3,
            max = 255,
            message = "Your first name must have a minimum of 3 characters and a maximum of 255 characters"
    )
    private String firstName;

    @NotBlank(message = "Your last name must not be empty")
    @Pattern(
            regexp = "[A-Z][a-z]+",
            message = "Your last name must begin with a capital letter and contain letters only"
    )
    @Column(name = "last_name", nullable = false)
    @Size(
            min = 3,
            max = 255,
            message = "Your last name must have a minimum of 3 characters and a maximum of 255 characters"
    )
    private String lastName;

    @Email
    @NotBlank(message = "Your email must not be empty")
    @Column(name = "email", nullable = false, unique = true)
    @Size(max = 255, message = "Your email must not exceed 255 characters")
    private String email;

    @NotBlank(message = "Your password must not be empty")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{8,}$",
            message = "Your password must contain at least 8 characters, one uppercase letter, one lowercase letter, " +
                    "one digit, and one special character"
    )
    @Column(name = "password", nullable = false)
    @Size(
            min = 8,
            max = 255,
            message = "Your password must have a minimum of 8 characters and a maximum of 255 characters"
    )
    private String password;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.REMOVE)
    private List<ToDo> todoList = new ArrayList<>();

    @ManyToMany(mappedBy = "collaborators", fetch = FetchType.LAZY)
    private List<ToDo> collaborators = new ArrayList<>();
}
