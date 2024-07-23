package com.example.Todo_list.unit.controller;

import com.example.Todo_list.controller.TaskCommentController;
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
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.example.Todo_list.unit.controller.utils.ControllerTestUtils.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskCommentController.class)
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

    private final User user = createUser();
    private final Role role = createRole();
    private final Task task = createTask();
    private final Comment comment = createComment();

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
