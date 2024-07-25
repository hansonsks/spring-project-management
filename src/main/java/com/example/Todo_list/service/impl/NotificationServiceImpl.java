package com.example.Todo_list.service.impl;

import com.example.Todo_list.entity.Notification;
import com.example.Todo_list.entity.Task;
import com.example.Todo_list.entity.User;
import com.example.Todo_list.exception.NullEntityException;
import com.example.Todo_list.repository.NotificationRepository;
import com.example.Todo_list.repository.TaskRepository;
import com.example.Todo_list.repository.UserRepository;
import com.example.Todo_list.service.NotificationService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * NotificationServiceImpl is a service class that implements NotificationService interface.
 * It provides methods for sending notifications to users, finding notifications by user, finding all notifications,
 * deleting notifications by id or by object, and checking due tasks.
 */
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationServiceImpl.class);
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;

    /**
     * checkDueTasks() method is a scheduled method that runs every 1 minute to check for tasks that are past due.
     * If a task is past due, a notification is sent to all users assigned to the task.
     */
    @Scheduled(fixedRate = 60000)   // Check every 1 minute
    public void checkDueTasks() {
        for (Task task : taskRepository.findAll()) {
            if (!Objects.equals(task.getState().getName(), "Completed") &&   // Task is not completed
                    task.getDeadline() != null &&                               // Task has a due date
                    task.getDeadline().isBefore(ZonedDateTime.now())) {         // Task is past due
                for (User user : task.getAssignedUsers()) { // Send notification to all users assigned to the task if they don't already have a notification for the task
                    if (user.getNotifications() != null &&
                            user.getNotifications().stream().noneMatch(notification -> notification.getMessage().contains("Task " + task.getName() + " is due"))) {
                        this.sendNotificationToUserId(user.getId(), "Task Due", "Task " + task.getName() + " is due");
                    }
                }
            }
        }
    }

    /**
     * sendNotificationToUserId() method sends a notification to a user with the given userId.
     * If the user is not found, an EntityNotFoundException is thrown.
     *
     * @param userId  the id of the user to send the notification to
     * @param title   the title of the notification
     * @param message the message of the notification
     */
    @Override
    public void sendNotificationToUserId(Long userId, String title, String message) {
        logger.info("NotificationService.sendNotificationToUserId(): Sending notification to userId={} with title={} and message={}", userId, title, message);

        Notification notification = new Notification();
        notification.setTitle(title);
        notification.setMessage(message);

        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            notification.setUser(user.get());
            notificationRepository.save(notification);
        } else {
            throw new EntityNotFoundException("User with id=" + userId + " was not found");
        }
    }

    /**
     * sendNotificationToUser() method sends a notification to a user.
     * If the user is null, a NullEntityException is thrown.
     *
     * @param user    the user to send the notification to
     * @param title   the title of the notification
     * @param message the message of the notification
     */
    @Override
    public void sendNotificationToUser(User user, String title, String message) {
        if (user == null) {
            logger.error("NotificationService.sendNotificationToUser(): User cannot be null");
            throw new NullEntityException(this.getClass().getName(), "User cannot be null");
        }

        logger.info("NotificationService.sendNotificationToUser(): Sending notification to user={} with title={} and message={}", user, title, message);

        Notification notification = new Notification();
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setUser(user);
        notificationRepository.save(notification);
    }

    /**
     * sendNotificationToAllUsers() method sends a notification to all users.
     *
     * @param title   the title of the notification
     * @param message the message of the notification
     */
    @Override
    public void sendNotificationToAllUsers(String title, String message) {
        logger.info("NotificationService.sendNotificationToAllUsers(): Sending notifications to all users with title={}, message={}", title, message);
        List<User> users = userRepository.findAll();
        for (User user : users) {
            Notification notification = new Notification();
            notification.setTitle(title);
            notification.setMessage(message);
            notification.setUser(user);
            notificationRepository.save(notification);
        }
    }

    /**
     * findNotificationById() method finds a notification by its id.
     * If the notification is not found, an EntityNotFoundException is thrown.
     *
     * @param id the id of the notification to find
     * @return the notification with the given id
     */
    @Override
    public Notification findNotificationById(Long id) {
        logger.info("NotificationService.findNotificationById(): Finding notification with id={}", id);

        Optional<Notification> notification = notificationRepository.findById(id);
        if (notification.isPresent()) {
            return notification.get();
        } else {
            throw new EntityNotFoundException("No Notification with id=" + id + " was found");
        }
    }

    /**
     * findNotificationsByUser() method finds all notifications by a user.
     *
     * @param user the user to find notifications by
     * @return a list of notifications by the user
     */
    @Override
    public List<Notification> findNotificationsByUser(User user) {
        if (user == null) {
            logger.error("NotificationService.findNotificationsByUser(): User cannot be null");
            throw new NullEntityException(this.getClass().getName(), "User cannot be null");
        }

        logger.info("NotificationService.findNotificationsByUser(): Finding notifications by user={} ...", user);
        return notificationRepository.findByUser(user);
    }

    /**
     * findAllNotifications() method finds all notifications.
     *
     * @return a list of all notifications
     */
    @Override
    public List<Notification> findAllNotifications() {
        logger.info("NotificationService.findAllNotifications(): Finding all notifications ...");
        return notificationRepository.findAll();
    }

    /**
     * deleteNotificationById() method deletes a notification by its id.
     * If the notification is not found, an EntityNotFoundException is thrown.
     *
     * @param id the id of the notification to delete
     */
    @Override
    public void deleteNotificationById(Long id) {
        logger.info("NotificationService.deleteNotificationById(): Deleting notification with id={}", id);
        Notification notification = this.findNotificationById(id);
        notificationRepository.delete(notification);
    }

    /**
     * deleteNotification() method deletes a notification by its object.
     *
     * @param notification the notification to delete
     */
    @Override
    public void deleteNotification(Notification notification) {
        if (notification == null) {
            logger.error("NotificationService.deleteNotification(): Notification cannot be null");
            throw new NullEntityException(this.getClass().getName(), "Notification cannot be null");
        }

        logger.info("NotificationService.deleteNotification(): Deleting notification={}", notification);
        notificationRepository.delete(notification);
    }
}
