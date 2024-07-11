package com.example.Todo_list.unit.controller;

import com.example.Todo_list.controller.UserController;
import com.example.Todo_list.entity.OAuthUser;
import com.example.Todo_list.entity.Role;
import com.example.Todo_list.entity.ToDo;
import com.example.Todo_list.entity.User;
import com.example.Todo_list.security.WebSecurityUserDetails;
import com.example.Todo_list.service.impl.RoleServiceImpl;
import com.example.Todo_list.service.impl.UserServiceImpl;
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

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserServiceImpl userService;

    @MockBean
    private RoleServiceImpl roleService;

    @MockBean
    private PasswordService passwordService;

    private User user;
    private Role role;
    private Role otherRole;

    @BeforeEach
    public void beforeEach() {
        role = new Role();
        role.setId(1L);
        role.setName("Admin");

        otherRole = new Role();
        otherRole.setId(2L);
        otherRole.setName("User");

        user = new User();
        user.setId(1L);
        user.setRole(role);
        user.setFirstName("Test");
        user.setLastName("Doe");
        user.setEmail("test@mail.com");
        user.setPassword("StrongPassword12345?");   // Not encoded for testing purposes

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
    void testShowUserRegistrationForm() throws Exception {
        mockMvc.perform(get("/users/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("user-create"))
                .andExpect(model().attribute("user", hasProperty("id", nullValue())))
                .andExpect(model().attribute("user", hasProperty("role", nullValue())))
                .andExpect(model().attribute("user", hasProperty("firstName", nullValue())))
                .andExpect(model().attribute("user", hasProperty("lastName", nullValue())))
                .andExpect(model().attribute("user", hasProperty("email", nullValue())))
                .andExpect(model().attribute("user", hasProperty("password", nullValue())));
    }

    @Test
    @DisplayName("Test")
    void testCreateUser() throws Exception {
        when(userService.findUserByEmail(any(String.class))).thenThrow(EntityNotFoundException.class);

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("firstName", user.getFirstName());
        formData.add("lastName", user.getLastName());
        formData.add("email", user.getEmail());
        formData.add("password", user.getPassword());

        mockMvc.perform(post("/users/create")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .params(formData)
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/login-form?signUpSuccess=true"));
    }

    @Test
    @DisplayName("Test")
    void testCreateInvalidUser() throws Exception {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("firstName", null);
        formData.add("lastName", null);
        formData.add("email", null);
        formData.add("password", null);

        mockMvc.perform(post("/users/create")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .params(formData)
                .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Test")
    void testDisplayUserInfo() throws Exception {
        when(userService.findUserById(any(long.class))).thenReturn(user);

        mockMvc.perform(get("/users/1/read"))
                .andExpect(status().isOk())
                .andExpect(view().name("user-info"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attribute("user", user));
    }

    @Test
    @DisplayName("Test")
    void testDisplayInvalidUserInfo() throws Exception {
        when(userService.findUserById(any(long.class))).thenThrow(EntityNotFoundException.class);

        mockMvc.perform(get("/user/999/read")).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Test")
    void testUpdateUser() throws Exception {
        when(userService.findUserById(any(long.class))).thenReturn(user);
        when(roleService.findRoleById(any(long.class))).thenReturn(otherRole);

        MultiValueMap<String, String> formData = getValidUserMultiValueMap();

        mockMvc.perform(post("/users/1/update")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .params(formData)
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/logout?updateSuccess=true"));
    }

    @Test
    @DisplayName("Test")
    void testUpdateInvalidUser() throws Exception {
        when(userService.findUserById(any(long.class))).thenReturn(user);
        when(roleService.findRoleById(any(long.class))).thenReturn(otherRole);

        MultiValueMap<String, String> formData = getInvalidUserMultiValueMap();

        mockMvc.perform(post("/users/1/update")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .params(formData)
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/users/1/update?badFirstName=true"));
    }

    @Test
    @DisplayName("Test")
    void testShowOAuthUserUpdatePage() throws Exception {
        OAuthUser oAuthUser = new OAuthUser();
        oAuthUser.setId(1L);
        oAuthUser.setProvider("Provider");
        oAuthUser.setProviderUserId("1234567890");
        oAuthUser.setUser(user);

        when(userService.findUserById(any(long.class))).thenReturn(user);

        MultiValueMap<String, String> formData = getValidUserMultiValueMap();

        mockMvc.perform(post("/users/1/oauth-update")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .params(formData)
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/logout?updateSuccess=true"));
    }

    @Test
    @DisplayName("Test")
    void testShowInvalidOAuthUserUpdatePage() throws Exception {
        OAuthUser oAuthUser = new OAuthUser();
        oAuthUser.setId(1L);
        oAuthUser.setProvider("Provider");
        oAuthUser.setProviderUserId("1234567890");
        oAuthUser.setUser(user);

        when(userService.findUserById(any(long.class))).thenReturn(user);

        MultiValueMap<String, String> formData = getInvalidUserMultiValueMap();

        mockMvc.perform(post("/users/1/oauth-update")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .params(formData)
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/users/1/oauth-update?badFirstName=true"));
    }

    @Test
    @DisplayName("Test")
    void testDeleteUser() throws Exception {
        user.setCollaborators(Collections.emptyList());
        when(userService.findUserById(any(long.class))).thenReturn(user);

        mockMvc.perform(get("/users/1/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users/all"));
    }

    @Test
    @DisplayName("Test")
    void testDeleteInvalidUser() throws Exception {
        user.setCollaborators(List.of(new ToDo(), new ToDo(), new ToDo()));
        when(userService.findUserById(any(long.class))).thenReturn(user);

        mockMvc.perform(get("/users/1/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/users/all?badDeleteUserId=1"));
    }

    @Test
    @DisplayName("Test")
    void testShowUserList() throws Exception {
        when(userService.findAllUsers()).thenReturn(Collections.singletonList(user));

        mockMvc.perform(get("/users/all"))
                .andExpect(status().isOk())
                .andExpect(view().name("user-list"))
                .andExpect(model().attribute("users", hasSize(1)))
                .andExpect(model().attribute("users", hasItem(
                        allOf(
                                hasProperty("id", is(1L)),
                                hasProperty("role", is(role)),
                                hasProperty("firstName", is("Test")),
                                hasProperty("lastName", is("Doe")),
                                hasProperty("email", is("test@mail.com")),
                                hasProperty("password", is("StrongPassword12345?"))
                        ))));
    }

    private MultiValueMap<String, String> getValidUserMultiValueMap() {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("id",          String.valueOf(user.getId()));
        formData.add("firstName",   user.getFirstName());
        formData.add("lastName",    user.getLastName());
        formData.add("email",       user.getEmail());
        formData.add("oldPassword", user.getPassword());
        formData.add("password",    "EvenStrongerPassword12345?");
        formData.add("roleId",      String.valueOf(otherRole.getId()));
        return formData;
    }

    private MultiValueMap<String, String> getInvalidUserMultiValueMap() {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("id",          String.valueOf(user.getId()));
        formData.add("firstName",   "");
        formData.add("lastName",    "");
        formData.add("email",       "");
        formData.add("oldPassword", "");
        formData.add("password",    "");
        formData.add("roleId",      String.valueOf(otherRole.getId()));
        return formData;
    }
}
