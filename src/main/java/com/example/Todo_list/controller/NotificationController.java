package com.example.Todo_list.controller;

import com.example.Todo_list.dto.NotificationDTO;
import com.example.Todo_list.dto.NotificationTransformer;
import com.example.Todo_list.entity.Notification;
import com.example.Todo_list.service.NotificationService;
import com.example.Todo_list.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller class for handling notifications
 */
@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final Logger logger = LoggerFactory.getLogger(NotificationController.class);
    private final NotificationService notificationService;
    private final UserService userService;

    /**
     * Fetches all notifications for a user
     *
     * @param userId ID of the user
     * @return List of notifications
     */
    @PreAuthorize("hasAuthority('ADMIN') or #userId == authentication.principal.id")
    @GetMapping("/user/{userId}")
    public List<NotificationDTO> getUserNotifications(@PathVariable Long userId) {
        logger.info("NotificationController.getUserNotifications(): Fetching notifications for user with ID: {}", userId);
        return notificationService.findNotificationsByUser(userService.findUserById(userId))
                .stream()
                .sorted(Comparator.comparing(Notification::getCreatedAt).reversed())
                .map(NotificationTransformer::convertEntityToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Marks a notification as read
     *
     * @param notificationId ID of the notification
     * @param userId         ID of the user
     */
    @PreAuthorize("hasAuthority('ADMIN') or #userId == authentication.principal.id")
    @PostMapping("/user/{user_id}/delete/{notification_id}")
    public void markNotificationsAsRead(@PathVariable("user_id") Long userId,
                                        @PathVariable("notification_id") Long notificationId) {
        logger.info("Marking notification with ID: {} as read", notificationId);
        notificationService.deleteNotificationById(notificationId);
    }
}
