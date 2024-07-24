package com.example.Todo_list.controller;

import com.example.Todo_list.controller.utils.ControllerTestUtils;
import com.example.Todo_list.entity.Role;
import com.example.Todo_list.entity.User;
import com.example.Todo_list.security.local.WebSecurityUserDetails;
import com.example.Todo_list.service.impl.NotificationServiceImpl;
import com.example.Todo_list.service.impl.UserServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NotificationController.class)
public class NotificationControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationServiceImpl notificationService;

    @MockBean
    private UserServiceImpl userService;

    private final User user = ControllerTestUtils.createUser();
    private final Role role = ControllerTestUtils.createRole();

    @BeforeEach
    public void beforeEach() {
        user.setRole(role);

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                new WebSecurityUserDetails(user),
                "password",
                List.of(new SimpleGrantedAuthority(user.getRole().getName()))
        );
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }

    @AfterEach
    public void afterEach() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("getUserNotifications() should return a list of notifications")
    public void testGetUserNotifications() throws Exception {
        mockMvc.perform(get("/notifications/user/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());

        verify(notificationService, times(1)).findNotificationsByUser(userService.findUserById(1L));
    }

    @Test
    @DisplayName("markNotiifcationsAsRead() should delete the notification marked as read and return status 200")
    public void testMarkNotificationsAsRead() throws Exception {
        mockMvc.perform(post("/notifications/user/1/delete/1")
                .with(csrf()))
                .andExpect(status().isOk());

        verify(notificationService, times(1)).deleteNotificationById(1L);
    }
}
