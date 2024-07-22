package com.example.Todo_list.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

/**
 * A comment entity that represents a comment made by a user on a task.
 */
@Entity
@Data
@EqualsAndHashCode(of = "id")
@Table(name = "comments")
@ToString(exclude = {"user", "task"})
public class Comment {

    /**
     * The unique identifier of the comment.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The user who made the comment.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    /**
     * The task on which the comment was made.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id")
    private Task task;

    /**
     * The content of the comment.
     */
    @NotBlank(message = "Your comment must not be empty")
    @Size(min = 1, max = 255, message = "Your comment must be between 1 and 255 characters")
    @Column(name = "comment", nullable = false)
    private String content;

    /**
     * The time the comment was created.
     */
    @Column(name = "created_at", nullable = false)
    @CreationTimestamp
    private ZonedDateTime createdAt = ZonedDateTime.now();

    /**
     * A flag to indicate if the comment has been edited.
     */
    @Column(name = "is_edited", nullable = false)
    private Boolean isEdited = false;

    // TODO: Optional: Add a field to store the last time the comment was edited and display in the view
}
