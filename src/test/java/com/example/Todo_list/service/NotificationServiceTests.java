package com.example.Todo_list.service;

import com.example.Todo_list.entity.Notification;
import com.example.Todo_list.entity.State;
import com.example.Todo_list.entity.Task;
import com.example.Todo_list.entity.User;
import com.example.Todo_list.exception.NullEntityException;
import com.example.Todo_list.repository.NotificationRepository;
import com.example.Todo_list.repository.TaskRepository;
import com.example.Todo_list.repository.UserRepository;
import com.example.Todo_list.service.impl.NotificationServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceTests {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    private User user;
    private Task task;
    private State state;
    private Notification notification;

    @BeforeEach
    public void beforeEach() {
        user = new User();
        user.setId(1L);

        state = new State();
        state.setId(1L);
        state.setName("Completed");

        task = new Task();
        task.setId(1L);
        task.setName("Task 1");
        task.setState(state);

        notification = new Notification();
        notification.setId(1L);
        notification.setTitle("Task Due");
        notification.setMessage("Task 1 is due");
    }

    @Test
    @DisplayName("checkDueTasks() does not send notifications for tasks that are not past due")
    public void testCheckDueTasksBeforeDeadline() {
        task.setDeadline(ZonedDateTime.now().plusHours(1));

        when(taskRepository.findAll()).thenReturn(Collections.singletonList(task));

        notificationService.checkDueTasks();

        verify(taskRepository, times(1)).findAll();
        verify(notificationRepository, times(0)).save(any(Notification.class));
    }

    @Test
    @DisplayName("checkDueTasks() checks for tasks that are past due and sends notifications to users assigned to the task")
    public void testCheckDueTasksAfterDeadline() {
        state.setName("In Progress");  // Task is not completed
        task.setState(state);
        task.setDeadline(ZonedDateTime.now().minusHours(12));
        task.setAssignedUsers(Collections.singletonList(user));

        user.setNotifications(Collections.singletonList(notification));
        notification.setUser(user);

        when(taskRepository.findAll()).thenReturn(Collections.singletonList(task));
        when(userRepository.findById(any(long.class))).thenReturn(java.util.Optional.of(user));

        notificationService.checkDueTasks();

        verify(taskRepository, times(1)).findAll();
        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    @DisplayName("sendNotificationToUserId() sends a notification to a user with the given userId")
    public void testSendNotificationToUserId() {
        when(userRepository.findById(any(long.class))).thenReturn(java.util.Optional.of(user));
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);

        notificationService.sendNotificationToUserId(1L, "Task Due", "Task Task 1 is due");

        verify(userRepository, times(1)).findById(any(long.class));
        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    @DisplayName("sendNotificationToUserId() throws EntityNotFoundException if the user with the given userId is not found")
    public void testSendNotificationToUserIdNotFound() {
        when(userRepository.findById(any(long.class))).thenReturn(java.util.Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> notificationService.sendNotificationToUserId(1L, notification.getTitle(), notification.getMessage()));

        verify(userRepository, times(1)).findById(any(long.class));
        verify(notificationRepository, times(0)).save(any(Notification.class));
    }

    @Test
    @DisplayName("sendNotificationToUser() throws NullEntityException if the User is null")
    public void testSendNotificationToNullUser() {
        assertThrows(NullEntityException.class, () -> notificationService.sendNotificationToUser(null, notification.getTitle(), notification.getMessage()));
        verify(notificationRepository, times(0)).save(any(Notification.class));
    }

    @Test
    @DisplayName("sendNotificationToUser() sends a notification to a user")
    public void testSendNotificationToUser() {
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);

        notificationService.sendNotificationToUser(user, notification.getTitle(), notification.getMessage());

        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    @DisplayName("sendNotificationToAllUsers() sends a notification to all users")
    public void testSendNotificationToAllUsers() {
        when(userRepository.findAll()).thenReturn(Collections.singletonList(user));
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);

        notificationService.sendNotificationToAllUsers(notification.getTitle(), notification.getMessage());

        verify(userRepository, times(1)).findAll();
        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    @DisplayName("findNotificationById() returns a notification given the notification's ID")
    public void testFindNotificationById() {
        when(notificationRepository.findById(any(long.class))).thenReturn(java.util.Optional.of(notification));

        Notification actual = notificationService.findNotificationById(notification.getId());

        assertNotNull(actual);
        assertEquals(notification, actual);
        assertEquals(notification.getId(), actual.getId());
        assertEquals(notification.getTitle(), actual.getTitle());
        assertEquals(notification.getMessage(), actual.getMessage());
        verify(notificationRepository, times(1)).findById(any(long.class));
    }

    @Test
    @DisplayName("findNotificationById() throws EntityNotFoundException if a notification with the given ID was not found")
    public void testFindNotificationByIdNotFound() {
        when(notificationRepository.findById(any(long.class))).thenReturn(java.util.Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> notificationService.findNotificationById(notification.getId()));

        verify(notificationRepository, times(1)).findById(any(long.class));
    }

    @Test
    @DisplayName("findNotificationByUser() returns a list of notifications for a user")
    public void testFindNotificationByUser() {
        when(notificationRepository.findByUser(any(User.class))).thenReturn(Collections.singletonList(notification));

        List<Notification> actual = notificationService.findNotificationsByUser(user);

        assertNotNull(actual);
        assertEquals(1, actual.size());
        assertEquals(notification, actual.get(0));
        verify(notificationRepository, times(1)).findByUser(any(User.class));
    }

    @Test
    @DisplayName("findNotificationByUser() returns an empty list if no notifications are found for a user")
    public void testFindNotificationByUserEmpty() {
        when(notificationRepository.findByUser(any(User.class))).thenReturn(Collections.emptyList());

        List<Notification> actual = notificationService.findNotificationsByUser(user);

        assertNotNull(actual);
        assertTrue(actual.isEmpty());
        verify(notificationRepository, times(1)).findByUser(any(User.class));
    }

    @Test
    @DisplayName("findNotificationByUser() throws NullEntityException if the User is null")
    public void testFindNotificationByNullUser() {
        assertThrows(NullEntityException.class, () -> notificationService.findNotificationsByUser(null));
        verify(notificationRepository, times(0)).findByUser(any(User.class));
    }

    @Test
    @DisplayName("findAllNotifications() returns a list of all notifications")
    public void testFindAllNotifications() {
        when(notificationRepository.findAll()).thenReturn(Collections.singletonList(notification));

        List<Notification> actual = notificationService.findAllNotifications();

        assertNotNull(actual);
        assertEquals(1, actual.size());
        assertEquals(notification, actual.get(0));
        verify(notificationRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("deleteNotificationById() deletes a notification given the notification's ID")
    public void testDeleteNotificationById() {
        when(notificationRepository.findById(any(long.class))).thenReturn(java.util.Optional.of(notification));

        notificationService.deleteNotificationById(notification.getId());

        verify(notificationRepository, times(1)).findById(any(long.class));
        verify(notificationRepository, times(1)).delete(any(Notification.class));
    }

    @Test
    @DisplayName("deleteNotificationById() throws EntityNotFoundException if a notification with the given ID was not found")
    public void testDeleteInvalidNotification() {
        when(notificationRepository.findById(any(long.class))).thenReturn(java.util.Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> notificationService.deleteNotificationById(notification.getId()));

        verify(notificationRepository, times(1)).findById(any(long.class));
        verify(notificationRepository, times(0)).delete(any(Notification.class));
    }

    @Test
    @DisplayName("deleteNotification() deletes a notification given the notification object")
    public void testDeleteNotification() {
        notificationService.deleteNotification(notification);

        verify(notificationRepository, times(1)).delete(any(Notification.class));
    }

    @Test
    @DisplayName("deleteNotification() throws NullEntityException if the Notification is null")
    public void testDeleteNullNotification() {
        assertThrows(NullEntityException.class, () -> notificationService.deleteNotification(null));
        verify(notificationRepository, times(0)).delete(any(Notification.class));
    }

}
