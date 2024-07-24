package com.example.Todo_list.repository;

import com.example.Todo_list.entity.Notification;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class NotificationRepositoryTests {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("findByUser() returns a non-empty List<Notification> given a valid User")
    void testFindByUserFound() {
        Long userId = 1L;
        List<Notification> notifications = notificationRepository.findByUser(userRepository.findById(userId).get());
        assertNotNull(notifications);
        assertFalse(notifications.isEmpty());
        assertEquals(3, notifications.size());

        Notification notification = notifications.get(0);
        assertEquals(1, notification.getId());
        assertEquals("Sample Notification", notification.getTitle());
        assertEquals("Sample Message", notification.getMessage());
        assertEquals(1, notification.getUser().getId());
    }
}
