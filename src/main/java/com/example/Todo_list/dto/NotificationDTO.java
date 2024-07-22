package com.example.Todo_list.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A DTO class for the Notification entity.
 */
@Data
@NoArgsConstructor
public class NotificationDTO {

    /**
     * The unique identifier of the notification.
     */
    private Long id;

    /**
     * The title of the notification.
     */
    @NotBlank(message = "Notification title must not be empty")
    @Size(min = 1, max = 255, message = "Notification title must be between 1 and 255 characters")
    private String title;

    /**
     * The message of the notification.
     */
    @NotBlank(message = "Notification title must not be empty")
    @Size(min = 1, max = 255, message = "Notification title must be between 1 and 255 characters")
    private String message;

    /**
     * The unique identifier of the user who the notification is for.
     */
    private Long userId;
}
