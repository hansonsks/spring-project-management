package com.example.Todo_list.repository;

import com.example.Todo_list.entity.Notification;
import com.example.Todo_list.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for Notification entity
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    /**
     * Find all notifications by user
     * @param user user
     * @return list of notifications
     */
    List<Notification> findByUser(User user);
}
