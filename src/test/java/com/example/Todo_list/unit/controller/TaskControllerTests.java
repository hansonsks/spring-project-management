package com.example.Todo_list.unit.controller;

import com.example.Todo_list.controller.TaskController;
import com.example.Todo_list.entity.*;
import com.example.Todo_list.security.WebSecurityUserDetails;
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
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.example.Todo_list.unit.controller.utils.ControllerTestUtils.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
public class TaskControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ToDoServiceImpl toDoService;

    @MockBean
    private TaskServiceImpl taskService;

    @MockBean
    private StateServiceImpl stateService;

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
    private final ToDo toDo = createToDo();
    private final State state = createState();
    private final Priority priority = createPriority();
    private final Comment comment = createComment();

    @BeforeEach
    public void beforeEach() {
        user.setRole(role);

        task.setPriority(priority);
        task.setState(state);

        toDo.setOwner(user);

        task.setTodo(toDo);
        toDo.setTasks(Collections.singletonList(task));

        comment.setUser(user);

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
    @DisplayName("Test")
    void testShowTaskCreationForm() throws Exception {
        when(toDoService.findToDoById(any(long.class))).thenReturn(toDo);

        mockMvc.perform(get("/tasks/create/todos/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("task-create"))
                .andExpect(model().attributeExists("task"))
                .andExpect(model().attributeExists("todo"))
                .andExpect(model().attributeExists("priorities"));
    }

    @Test
    @DisplayName("Test")
    void testCreateTask() throws Exception {
        when(toDoService.findToDoById(any(long.class))).thenReturn(toDo);
        when(stateService.findStateByName(any(String.class))).thenReturn(state);

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("id", "1");
        formData.add("name", "Task Name");
        formData.add("description", "Task Description");
        formData.add("priority", "TRIVIAL");
        formData.add("state", "Test");

        mockMvc.perform(post("/tasks/create/todos/1")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .params(formData)
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/todos/1/tasks"));
    }

    @Test
    @DisplayName("Test")
    void testCreateInvalidTask() throws Exception {
        when(toDoService.findToDoById(any(long.class))).thenReturn(toDo);

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("id",          "1");
        formData.add("name",        "");
        formData.add("description", "");
        formData.add("priority",    "");
        formData.add("state",       "");

        mockMvc.perform(post("/tasks/create/todos/1")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .params(formData)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("task-create"))
                .andExpect(model().attributeExists("todo"))
                .andExpect(model().attributeExists("priorities"));
    }

    @Test
    @DisplayName("Test")
    void testShowTaskUpdateForm() throws Exception {
        when(taskService.findTaskById(any(long.class))).thenReturn(task);

        mockMvc.perform(get("/tasks/1/update"))
                .andExpect(status().isOk())
                .andExpect(view().name("task-update"))
                .andExpect(model().attributeExists("task"))
                .andExpect(model().attributeExists("toDoId"))
                .andExpect(model().attributeExists("priorities"))
                .andExpect(model().attributeExists("states"));
    }

    @Test
    @DisplayName("Test")
    void testUpdateTask() throws Exception {
        when(taskService.findTaskById(any(long.class))).thenReturn(task);
        when(toDoService.findToDoById(any(long.class))).thenReturn(toDo);
        when(stateService.findStateByName(any(String.class))).thenReturn(state);

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("id",          "1");
        formData.add("name",        "Task Name Updated");
        formData.add("description", "Task Description");
        formData.add("priority",    "TRIVIAL");
        formData.add("toDoId",      "1");
        formData.add("state",       "Test");

        mockMvc.perform(post("/tasks/1/update")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .params(formData)
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/todos/1/tasks"));
    }

    @Test
    @DisplayName("Test")
    void testUpdateInvalidTask() throws Exception {
        when(taskService.findTaskById(any(long.class))).thenReturn(task);

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("id",          "1");
        formData.add("name",        "");
        formData.add("description", "");
        formData.add("priority",    "");
        formData.add("toDoId",      "1");
        formData.add("state",       "");

        mockMvc.perform(post("/tasks/1/update")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .params(formData)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("task-update"))
                .andExpect(model().attributeExists("task"))
                .andExpect(model().attributeExists("toDoId"))
                .andExpect(model().attributeExists("priorities"))
                .andExpect(model().attributeExists("states"));
    }

    @Test
    @DisplayName("Test")
    void testDisplayTask() throws Exception {
        when(taskService.findTaskById(any(long.class))).thenReturn(task);

        mockMvc.perform(get("/tasks/1/read"))
                .andExpect(status().isOk())
                .andExpect(view().name("task-info"))
                .andExpect(model().attributeExists("task", "comments"))
                .andExpect(model().attribute("task", hasProperty("id",          is(1L))))
                .andExpect(model().attribute("task", hasProperty("name",        is("Task Name"))))
                .andExpect(model().attribute("task", hasProperty("description", is("Task Description"))))
                .andExpect(model().attribute("task", hasProperty("priority",    is(String.valueOf(Priority.TRIVIAL)))))
                .andExpect(model().attribute("task", hasProperty("state",       is(state.toString()))));
    }

    @Test
    @DisplayName("Test")
    void testDeleteTask() throws Exception {
        mockMvc.perform(post("/tasks/1/delete/todos/1")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/todos/1/tasks"));

        verify(taskService).deleteTaskById(1L);
    }

    @Test
    @DisplayName("Test")
    void testDeleteInvalidTask() throws Exception {
        mockMvc.perform(post("/tasks/999/delete/todos/1")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/todos/1/tasks"));
    }

    // Tests for Task Comments
    @Test
    @DisplayName("Test")
    void testCreateComment() throws Exception {
        when(taskService.findTaskById(any(long.class))).thenReturn(task);

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("id",      "1");
        formData.add("comment", "Test Comment");
        formData.add("user",    user.toString());

        mockMvc.perform(post("/tasks/1/comments/create")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .params(formData)
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tasks/1/read"));
    }

    @Test
    @DisplayName("Test")
    void testCreateInvalidComment() throws Exception {
        when(taskService.findTaskById(any(long.class))).thenReturn(task);

        char[] veryLongComment = new char[1000];
        Arrays.fill(veryLongComment, 'a');

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("id",      "1");
        formData.add("comment", Arrays.toString(veryLongComment));
        formData.add("user",    user.toString());

        mockMvc.perform(post("/tasks/1/comments/create")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .params(formData)
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tasks/1/read?invalidComment=true"));
    }

    @Test
    @DisplayName("Test")
    void testCreateCommentForInvalidTask() throws Exception {
        doThrow(EntityNotFoundException.class).when(taskService).findTaskById(any(long.class));

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("id",      "1");
        formData.add("comment", "Test Comment");
        formData.add("user",    user.toString());

        mockMvc.perform(post("/tasks/999/comments/create")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .params(formData)
                .with(csrf()))
                .andExpect(status().is4xxClientError())
                .andExpect(content().string(containsString("404")))
                .andExpect(content().string(containsString("Not Found")));
    }

    @Test
    @DisplayName("Test")
    void testUpdateComment() throws Exception {
        when(commentService.findCommentById(any(long.class))).thenReturn(comment);

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("id",      "1");
        formData.add("comment", "Updated Test Comment");
        formData.add("user",    user.toString());

        mockMvc.perform(post("/tasks/1/comments/1/update")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .params(formData)
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tasks/1/read"));
    }

    @Test
    @DisplayName("Test")
    void testUpdateInvalidComment() throws Exception {
        when(commentService.findCommentById(any(long.class))).thenReturn(comment);

        char[] veryLongComment = new char[1000];
        Arrays.fill(veryLongComment, 'a');

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("id",      "1");
        formData.add("comment", Arrays.toString(veryLongComment));
        formData.add("user",    user.toString());

        mockMvc.perform(post("/tasks/1/comments/1/update")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .params(formData)
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tasks/1/read?invalidComment=true"));
    }

    @Test
    @DisplayName("Test")
    void testDeleteComment() throws Exception {
        mockMvc.perform(post("/tasks/1/comments/1/delete")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tasks/1/read"));
    }

    @Test
    @DisplayName("Test")
    void testDeleteInvalidComment() throws Exception {
        doThrow(EntityNotFoundException.class).when(commentService).deleteCommentById(any(long.class));

        mockMvc.perform(post("/tasks/1/comments/999/delete")
                .with(csrf()))
                .andExpect(status().is4xxClientError())
                .andExpect(content().string(containsString("404")))
                .andExpect(content().string(containsString("Not Found")));
    }

    @Test
    @DisplayName("Test")
    void testDeleteCommentForInvalidTask() throws Exception {
        doThrow(EntityNotFoundException.class).when(taskService).findTaskById(any(long.class));

        mockMvc.perform(post("/tasks/999/comments/1/delete")
                .with(csrf()))
                .andExpect(status().is4xxClientError())
                .andExpect(content().string(containsString("404")))
                .andExpect(content().string(containsString("Not Found")));
    }
}
