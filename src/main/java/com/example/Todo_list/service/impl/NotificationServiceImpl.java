package com.example.Todo_list.service.impl;

import com.example.Todo_list.entity.Notification;
import com.example.Todo_list.entity.Task;
import com.example.Todo_list.entity.User;
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

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

// TODO: Add the logging statements

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final Logger logger = LoggerFactory.getLogger(NotificationServiceImpl.class);
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;

    @Scheduled(fixedRate = 60000)   // Check every 1 minute
    public void checkDueTasks() {
        for (Task task : taskRepository.findAll()) {
            if (!Objects.equals(task.getState().getName(), "Completed") &&   // Task is not completed
                    task.getDeadline() != null &&                               // Task has a due date
                    task.getDeadline().isBefore(ZonedDateTime.now())) {         // Task is past due
                for (User user : task.getAssignedUsers()) {
                    this.sendNotificationToUserId(user.getId(), "Task Due", "Task " + task.getName() + " is due");
                }
            }
        }
    }

    @Override
    public void sendNotificationToUserId(Long userId, String title, String message) {
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

    @Override
    public void sendNotificationToAllUsers(String title, String message) {
        List<User> users = userRepository.findAll();
        for (User user : users) {
            Notification notification = new Notification();
            notification.setTitle(title);
            notification.setMessage(message);
            notification.setUser(user);
            notificationRepository.save(notification);
        }
    }

    @Override
    public Notification findNotificationById(Long id) {
        Optional<Notification> notification = notificationRepository.findById(id);

        if (notification.isPresent()) {
            return notification.get();
        } else {
            throw new EntityNotFoundException("No Notification with id=" + id + " was found");
        }
    }

    @Override
    public List<Notification> findNotificationsByUser(User user) {
        return notificationRepository.findByUser(user);
    }

    @Override
    public List<Notification> findAllNotifications() {
        return notificationRepository.findAll();
    }

    @Override
    public void deleteNotificationById(Long id) {
        Notification notification = this.findNotificationById(id);
        notificationRepository.delete(notification);
    }

    @Override
    public void deleteNotification(Notification notification) {
        notificationRepository.delete(notification);
    }
}
