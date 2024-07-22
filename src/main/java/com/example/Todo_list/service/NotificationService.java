package com.example.Todo_list.service;

import com.example.Todo_list.entity.Notification;
import com.example.Todo_list.entity.User;

import java.util.List;

/**
 * Service class for managing Notification entities.
 */
public interface NotificationService {

    /**
     * Sends a notification to a user with the given userId.
     *
     * @param userId    The id of the user to send the notification to.
     * @param title     The title of the notification.
     * @param message   The message of the notification.
     */
    void sendNotificationToUserId(Long userId, String title, String message);

    /**
     * Sends a notification to a user with the given user.
     *
     * @param user      The user to send the notification to.
     * @param title     The title of the notification.
     * @param message   The message of the notification.
     */
    void sendNotificationToUser(User user, String title, String message);

    /**
     * Sends a notification to all users.
     *
     * @param title     The title of the notification.
     * @param message   The message of the notification.
     */
    void sendNotificationToAllUsers(String title, String message);

    /**
     * Finds a notification by its id.
     *
     * @param id    The id of the notification to find.
     * @return      The notification with the given id.
     */
    Notification findNotificationById(Long id);

    /**
     * Finds all notifications sent to a user.
     *
     * @param user  The user to find notifications for.
     * @return      A list of notifications sent to the user.
     */
    List<Notification> findNotificationsByUser(User user);

    /**
     * Finds all notifications.
     *
     * @return  A list of all notifications.
     */
    List<Notification> findAllNotifications();

    /**
     * Deletes a notification by its id.
     *
     * @param id    The id of the notification to delete.
     */
    void deleteNotificationById(Long id);

    /**
     * Deletes a notification.
     *
     * @param notification  The notification to delete.
     */
    void deleteNotification(Notification notification);
}
