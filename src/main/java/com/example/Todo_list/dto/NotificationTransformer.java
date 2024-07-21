package com.example.Todo_list.dto;

import com.example.Todo_list.entity.Notification;
import com.example.Todo_list.entity.User;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NotificationTransformer {

    public static NotificationDTO convertEntityToDTO(Notification notification) {
        NotificationDTO dto = new NotificationDTO();
        dto.setId(notification.getId());
        dto.setTitle(notification.getTitle());
        dto.setMessage(notification.getMessage());
        dto.setUserId(notification.getUser().getId());

        return dto;
    }

    public static Notification convertDTOToEntity(NotificationDTO dto, User user) {
        Notification notification = new Notification();
        notification.setId(dto.getId());
        notification.setTitle(dto.getTitle());
        notification.setMessage(dto.getMessage());
        notification.setUser(user);

        return notification;
    }
}
