package com.example.Todo_list.service;


import com.example.Todo_list.entity.Notification;
import com.example.Todo_list.entity.User;

import java.util.List;

public interface NotificationService {

    void sendNotificationToUserId(Long userId, String title, String message);

    void sendNotificationToUser(User user, String title, String message);

    void sendNotificationToAllUsers(String title, String message);

    Notification findNotificationById(Long id);

    List<Notification> findNotificationsByUser(User user);

    List<Notification> findAllNotifications();

    void deleteNotificationById(Long id);

    void deleteNotification(Notification notification);
}
