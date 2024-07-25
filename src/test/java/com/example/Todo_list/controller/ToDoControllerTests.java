package com.example.Todo_list.controller;

import com.example.Todo_list.controller.utils.ControllerTestUtils;
import com.example.Todo_list.entity.*;
import com.example.Todo_list.exception.UserIsToDoOwnerException;
import com.example.Todo_list.security.AccessControlService;
import com.example.Todo_list.security.local.WebSecurityUserDetails;
import com.example.Todo_list.service.impl.*;
import com.example.Todo_list.utils.PasswordService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ToDoController.class)
public class ToDoControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ToDoServiceImpl toDoService;

    @MockBean
    private TaskServiceImpl taskService;

    @MockBean
    private UserServiceImpl userService;

    @MockBean
    private RoleServiceImpl roleService;

    @MockBean
    private PasswordService passwordService;

    @MockBean
    private NotificationServiceImpl notificationService;

    @MockBean
    private AccessControlService accessControlService;

    private final User user = ControllerTestUtils.createUser();
    private final Role role = ControllerTestUtils.createRole();
    private final Task task = ControllerTestUtils.createTask();
    private final ToDo toDo = ControllerTestUtils.createToDo();
    private final State state = ControllerTestUtils.createState();
    private final Priority priority = ControllerTestUtils.createPriority();

    @BeforeEach
    public void beforeEach() {
        user.setRole(role);

        task.setPriority(priority);
        task.setState(state);

        toDo.setOwner(user);

        task.setTodo(toDo);
        toDo.setTasks(Collections.singletonList(task));

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                new WebSecurityUserDetails(user),
                "password",
                List.of(new SimpleGrantedAuthority(user.getRole().getName()))
        );
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        when(accessControlService.hasAccess(any(), anyLong())).thenReturn(true);
        when(accessControlService.hasAccess(any(), anyList())).thenReturn(true);
        when(accessControlService.hasAdminAccess(any())).thenReturn(true);
    }

    @AfterEach
    public void afterEach() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("showToDoCreationForm() should return a form to create a new ToDo")
    void testShowToDoCreationForm() throws Exception {
        mockMvc.perform(get("/todos/create/users/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("todo-create"))
                .andExpect(model().attributeExists("todo", "ownerId"));
    }

    @Test
    @DisplayName("createToDo() should create a new ToDo and redirect to the ToDo list page")
    void testCreateToDo() throws Exception {
        when(userService.findUserById(any(long.class))).thenReturn(user);

        MultiValueMap<String, String> formData = getValidToDoMultiValueMap();

        mockMvc.perform(post("/todos/create/users/1")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .params(formData)
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/todos/all/users/1"));
    }

    @Test
    @DisplayName("createToDo() should return the ToDo creation form if the ToDo is invalid")
    void testCreateInvalidToDo() throws Exception {
        MultiValueMap<String, String> formData = getInvalidToDoMultiValueMap();

        mockMvc.perform(post("/todos/create/users/1")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .params(formData)
                .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("displayToDo() should return the ToDo and its tasks")
    void testDisplayToDo() throws Exception {
        when(toDoService.findToDoById(any(long.class))).thenReturn(toDo);
        when(taskService.findAllTasksOfToDo(any(long.class))).thenReturn(List.of(task));
        when(userService.findAllUsers()).thenReturn(List.of(user));

        mockMvc.perform(get("/todos/1/tasks"))
                .andExpect(status().isOk())
                .andExpect(view().name("todo-tasks"))
                .andExpect(model().attributeExists("todo", "tasks", "users"));
    }

    @Test
    @DisplayName("showToDoUpdateForm() should return the form to update a ToDo")
    void testShowToDoUpdateForm() throws Exception {
        when(toDoService.findToDoById(any(long.class))).thenReturn(toDo);

        mockMvc.perform(get("/todos/1/update/users/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("todo-update"))
                .andExpect(model().attributeExists("todo"));
    }

    @Test
    @DisplayName("updateToDo() should update the ToDo and redirect to the ToDo list page")
    void testUpdateToDo() throws Exception {
        when(toDoService.findToDoById(any(long.class))).thenReturn(toDo);

        MultiValueMap<String, String> formData = getValidToDoMultiValueMap();

        mockMvc.perform(post("/todos/1/update/users/1")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .params(formData)
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/todos/all/users/1"));
    }

    @Test
    @DisplayName("updateToDo() should return the ToDo update form if the ToDo is invalid")
    void testUpdateInvalidToDo() throws Exception {
        when(userService.findUserById(any(long.class))).thenReturn(user);
        when(toDoService.findToDoById(any(long.class))).thenReturn(toDo);

        MultiValueMap<String, String> formData = getInvalidToDoMultiValueMap();

        mockMvc.perform(post("/todos/1/update/users/1")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .params(formData)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("todo-update"));
    }

    @Test
    @DisplayName("deleteToDo() should delete the ToDo and redirect to the ToDo list page")
    void testDeleteToDo() throws Exception {
        when(toDoService.findToDoById(any(long.class))).thenReturn(toDo);

        mockMvc.perform(post("/todos/1/delete/users/1")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/todos/all/users/1"));
    }

    @Test
    @DisplayName("deleteToDo() should redirect to the ToDo list page if the ToDo is invalid")
    void testDeleteInvalidToDo() throws Exception {
        when(toDoService.findToDoById(any(long.class))).thenThrow(EntityNotFoundException.class);

        mockMvc.perform(post("/todos/1/delete/users/1")
                .with(csrf()))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("displayAllToDo() should return all the ToDos of a user")
    void testDisplayAllToDo() throws Exception {
        when(toDoService.findAllToDoOfUserId(any(long.class))).thenReturn(List.of(toDo));
        when(userService.findUserById(any(long.class))).thenReturn(user);

        mockMvc.perform(get("/todos/all/users/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("todos-user"))
                .andExpect(model().attributeExists("todos", "user"));
    }

    @Test
    @DisplayName("addCollaborator() should add a collaborator to the ToDo and redirect to the ToDo tasks page")
    void testAddCollaborator() throws Exception {
        User otherUser = new User();
        otherUser.setId(2L);
        otherUser.setFirstName("Other User as Collaborator");

        when(toDoService.findToDoById(any(long.class))).thenReturn(toDo);
        when(userService.findUserById(any(long.class))).thenReturn(otherUser);

        mockMvc.perform(post("/todos/1/add?user_id=2")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/todos/1/tasks"));
    }

    @Test
    @DisplayName("addCollaborator() should return the ToDo tasks page if the owner is being added as a collaborator")
    void testAddOwnerAsCollaborator() throws Exception {
        when(toDoService.findToDoById(any(long.class))).thenReturn(toDo);
        when(userService.findUserById(any(long.class))).thenReturn(user);
        doThrow(UserIsToDoOwnerException.class).when(toDoService).addCollaborator(toDo.getId(), user.getId());

        mockMvc.perform(post("/todos/1/add?user_id=1")
                .with(csrf()))
                .andExpect(status().is4xxClientError())
                .andExpect(content().string(containsString("406")))
                .andExpect(content().string(containsString("Not Acceptable")));
    }

    @Test
    @DisplayName("removeCollaborator() should remove a collaborator from the ToDo and redirect to the ToDo tasks page")
    void testRemoveCollaborator() throws Exception {
        User otherUser = new User();
        otherUser.setId(2L);
        otherUser.setFirstName("Other User as Collaborator");

        toDo.setCollaborators(List.of(otherUser));

        when(toDoService.findToDoById(any(long.class))).thenReturn(toDo);
        when(userService.findUserById(any(long.class))).thenReturn(user);
        doThrow(UserIsToDoOwnerException.class).when(toDoService).removeCollaborator(toDo.getId(), user.getId());

        mockMvc.perform(post("/todos/1/remove?user_id=2")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/todos/1/tasks"));
    }

    @Test
    @DisplayName("removeCollaborator() should return the ToDo tasks page if the owner is being removed as a collaborator")
    void testRemoveOwnerFromCollaborators() throws Exception {
        when(toDoService.findToDoById(any(long.class))).thenReturn(toDo);
        when(userService.findUserById(any(long.class))).thenReturn(user);
        doThrow(UserIsToDoOwnerException.class).when(toDoService).removeCollaborator(toDo.getId(), user.getId());

        mockMvc.perform(post("/todos/1/remove?user_id=1")
                .with(csrf()))
                .andExpect(status().is4xxClientError())
                .andExpect(content().string(containsString("406")))
                .andExpect(content().string(containsString("Not Acceptable")));
    }

    private static MultiValueMap<String, String> getValidToDoMultiValueMap() {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("id",          "1");
        formData.add("title",       "ToDo Title");
        formData.add("description", "ToDo Description");
        formData.add("createdAt",   ZonedDateTime.now().toString());
        return formData;
    }

    private static MultiValueMap<String, String> getInvalidToDoMultiValueMap() {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("id",          "1");
        formData.add("title",       "");
        formData.add("description", "");
        formData.add("createdAt",   "");
        return formData;
    }
}
