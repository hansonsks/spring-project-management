package com.example.Todo_list.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class NotificationDTO {

    private Long id;

    @NotBlank(message = "Notification title must not be empty")
    @Size(min = 1, max = 255, message = "Notification title must be between 1 and 255 characters")
    private String title;

    @NotBlank(message = "Notification title must not be empty")
    @Size(min = 1, max = 255, message = "Notification title must be between 1 and 255 characters")
    private String message;

    private Long userId;
}
