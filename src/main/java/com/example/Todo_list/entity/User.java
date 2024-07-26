package com.example.Todo_list.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a user in the system.
 */
@Entity
@Data
@ToString(exclude = {"todoList", "collaborators", "assignedTasks"})
@EqualsAndHashCode(of = "id")
@Table(name = "users")
public class User {

    /**
     * The unique identifier of the user.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The first name of the user.
     */
    @NotBlank(message = "Your first name must not be empty.")
//    @Pattern(
//            regexp = "[a-zA-Z]+",
//            message = "Your first name must contain letters only"
//    )
    @Column(name = "first_name", nullable = false)
    @Size(
            // min = 3,     // Commented out to allow for names like "Al"
            max = 255,
            message = "Your first name must have a minimum of 3 characters and a maximum of 255 characters."
    )
    private String firstName;

    /**
     * The last name of the user.
     */
//    @Pattern(
//            regexp = "[a-zA-Z]*",
//            message = "Your last name must contain letters only"
//    )
    @Column(name = "last_name", nullable = false)
    @Size(
            max = 255,
            message = "Your last name must have a minimum of 3 characters and a maximum of 255 characters."
    )
    private String lastName;

    /**
     * The email of the user.
     */
    @Email(message = "Your email must be valid.")
    @Column(name = "email", unique = true)
    @Size(max = 255, message = "Your email must not exceed 255 characters.")
    private String email;

    /**
     * The password of the user.
     */
    @NotBlank(message = "Your password must not be empty.")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{8,}$",
            message = "Your password must contain one uppercase letter, one lowercase letter, one digit, and one special character."
    )
    @Column(name = "password")
    @Size(
            min = 8,
            max = 255,
            message = "Your password must have a minimum of 8 characters and a maximum of 255 characters."
    )
    private String password;

    /**
     * The role of the user.
     */
    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    /**
     * The list of to-dos owned by the user.
     */
    @OneToMany(mappedBy = "owner", cascade = CascadeType.REMOVE)
    private List<ToDo> todoList = new ArrayList<>();

    /**
     * The list of to-dos the user is collaborating on.
     */
    // TODO: Give this a better name
    @ManyToMany(mappedBy = "collaborators", fetch = FetchType.LAZY)
    private List<ToDo> collaborators = new ArrayList<>();

    /**
     * The list of comments made by the user.
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    private List<Comment> comments = new ArrayList<>();

    /**
     * The list of tasks assigned to the user.
     */
    @ManyToMany(mappedBy = "assignedUsers", fetch = FetchType.LAZY)
    private List<Task> assignedTasks = new ArrayList<>();

    /**
     * The list of notifications sent to the user.
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    private List<Notification> notifications;

    @CreationTimestamp
    @Column(name = "created_at")
    private ZonedDateTime createdAt;

    /**
     * Whether the user is a guest.
     */
    @Column(name = "is_guest")
    private Boolean isGuest = false;
}
