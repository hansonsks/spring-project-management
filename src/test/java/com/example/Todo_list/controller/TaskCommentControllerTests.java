package com.example.Todo_list.controller;

import com.example.Todo_list.controller.utils.ControllerTestUtils;
import com.example.Todo_list.entity.Comment;
import com.example.Todo_list.entity.Role;
import com.example.Todo_list.entity.Task;
import com.example.Todo_list.entity.User;
import com.example.Todo_list.security.local.WebSecurityUserDetails;
import com.example.Todo_list.service.impl.CommentServiceImpl;
import com.example.Todo_list.service.impl.RoleServiceImpl;
import com.example.Todo_list.service.impl.UserServiceImpl;
import com.example.Todo_list.utils.PasswordService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
public class TaskCommentControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommentServiceImpl commentService;

    @MockBean
    private UserServiceImpl userService;

    @MockBean
    private RoleServiceImpl roleService;

    @MockBean
    private PasswordService passwordService;

    private final User user = ControllerTestUtils.createUser();
    private final Role role = ControllerTestUtils.createRole();
    private final Task task = ControllerTestUtils.createTask();
    private final Comment comment = ControllerTestUtils.createComment();

    @BeforeEach
    public void beforeEach() {
        user.setRole(role);

        comment.setUser(user);
        comment.setTask(task);

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                new WebSecurityUserDetails(user),
                "password",
                List.of(new SimpleGrantedAuthority(user.getRole().getName()))
        );
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }

    @Test
    @DisplayName("showAllComments() should return comments-all template and all comments")
    public void testShowAllComments() throws Exception {
        when(commentService.findAllComments()).thenReturn(List.of(comment));

        mockMvc.perform(get("/comments/all"))
                .andExpect(status().isOk())
                .andExpect(view().name("comments-all"))
                .andExpect(model().attributeExists("comments"))
                .andExpect(model().attribute("comments", List.of(comment)));
    }

    @Test
    @DisplayName("showAllCommentsByUserId() should return comments-user template and all comments by user with given id")
    void testShowAllCommentsByUserId() throws Exception {
        when(userService.findUserById(user.getId())).thenReturn(user);
        when(commentService.findCommentByUser(user)).thenReturn(List.of(comment));

        mockMvc.perform(get("/comments/all/users/" + user.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("comments-user"))
                .andExpect(model().attributeExists("comments"))
                .andExpect(model().attribute("comments", List.of(comment)));
    }
}
