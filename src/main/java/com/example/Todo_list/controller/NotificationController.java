package com.example.Todo_list.controller;

import com.example.Todo_list.dto.NotificationDTO;
import com.example.Todo_list.dto.NotificationTransformer;
import com.example.Todo_list.entity.Notification;
import com.example.Todo_list.service.NotificationService;
import com.example.Todo_list.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final Logger logger = LoggerFactory.getLogger(NotificationController.class);
    private final NotificationService notificationService;
    private final UserService userService;

    @GetMapping("/user/{userId}")
    public List<NotificationDTO> getUserNotifications(@PathVariable Long userId) {
        logger.info("Fetching notifications for user with ID: {}", userId);
        return notificationService.findNotificationsByUser(userService.findUserById(userId))
                .stream()
                .sorted(Comparator.comparing(Notification::getCreatedAt).reversed())
                .map(NotificationTransformer::convertEntityToDTO)
                .collect(Collectors.toList());
    }

    @PostMapping("/user/{user_id}/delete/{notification_id}")
    public void markNotificationsAsRead(@PathVariable("notification_id") Long notificationId,
                                        @PathVariable("user_id") Long userId) {
        logger.info("Marking notification with ID: {} as read", notificationId);
        notificationService.deleteNotificationById(notificationId);
    }
}
