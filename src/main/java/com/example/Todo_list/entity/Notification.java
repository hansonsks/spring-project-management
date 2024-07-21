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

@Entity
@Data
@ToString(exclude = {"user"})
@EqualsAndHashCode(of = "id")
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Notification title must not be empty")
    @Size(min = 1, max = 255, message = "Notification title must be between 1 and 255 characters")
    @Column(name = "title", nullable = false)
    private String title;

    @NotBlank(message = "Notification message must not be empty")
    @Size(min = 1, max = 255, message = "Notification message must be between 1 and 255 characters")
    @Column(name = "message", nullable = false)
    private String message;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "created_at", nullable = false)
    @CreationTimestamp
    private ZonedDateTime createdAt = ZonedDateTime.now();
}
