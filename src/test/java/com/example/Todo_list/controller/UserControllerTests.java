package com.example.Todo_list.controller;

import com.example.Todo_list.controller.utils.ControllerTestUtils;
import com.example.Todo_list.entity.OAuthUser;
import com.example.Todo_list.entity.Role;
import com.example.Todo_list.entity.ToDo;
import com.example.Todo_list.entity.User;
import com.example.Todo_list.security.local.WebSecurityUserDetails;
import com.example.Todo_list.security.oauth2.OAuth2Provider;
import com.example.Todo_list.service.impl.RoleServiceImpl;
import com.example.Todo_list.service.impl.UserServiceImpl;
import com.example.Todo_list.utils.PasswordService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
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

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserServiceImpl userService;

    @MockBean
    private RoleServiceImpl roleService;

    @MockBean
    private PasswordService passwordService;

    private final User user = ControllerTestUtils.createUser();
    private final Role role = ControllerTestUtils.createRole();
    private Role otherRole;

    @BeforeEach
    public void beforeEach() {
        otherRole = new Role();
        otherRole.setId(2L);
        otherRole.setName("User");

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
    @DisplayName("showUserRegistrationForm() should return user-create view with empty user object")
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
    @DisplayName("createUser() should create a new user and redirect to login page with success message")
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
    @DisplayName("createUser() should return user-create view with error message if user information is invalid")
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
    @DisplayName("displayUserInfo() should return user-info view with user information")
    void testDisplayUserInfo() throws Exception {
        when(userService.findUserById(any(long.class))).thenReturn(user);

        mockMvc.perform(get("/users/1/read"))
                .andExpect(status().isOk())
                .andExpect(view().name("user-info"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attribute("user", user));
    }

    @Test
    @DisplayName("displayUserInfo() should return 404 Not Found if user information is invalid")
    void testDisplayInvalidUserInfo() throws Exception {
        when(userService.findUserById(any(long.class))).thenThrow(EntityNotFoundException.class);

        mockMvc.perform(get("/user/999/read")).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("showUserUpdatePage() should return user-update view with user information")
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
    @DisplayName("showUserUpdatePage() should return user-update view with error message if user information is invalid")
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
    @DisplayName("showOAuthUserUpdatePage() should return user-oauth-update view with user information")
    void testShowOAuthUserUpdatePage() throws Exception {
        OAuthUser oAuthUser = new OAuthUser();
        oAuthUser.setId(1L);
        oAuthUser.setProvider(OAuth2Provider.GITHUB);
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
    @DisplayName("showOAuthUserUpdatePage() should return user-oauth-update view with error message if user information is invalid")
    void testShowInvalidOAuthUserUpdatePage() throws Exception {
        OAuthUser oAuthUser = new OAuthUser();
        oAuthUser.setId(1L);
        oAuthUser.setProvider(OAuth2Provider.GITHUB);
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
    @DisplayName("deleteUser() should delete the user and redirect to user list page with success message")
    void testDeleteUser() throws Exception {
        user.setCollaborators(Collections.emptyList());
        when(userService.findUserById(any(long.class))).thenReturn(user);

        mockMvc.perform(post("/users/1/delete")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users/all"));
    }

    @Test
    @DisplayName("deleteUser() should return user list page with error message if user has collaborators")
    void testDeleteInvalidUser() throws Exception {
        user.setCollaborators(List.of(new ToDo(), new ToDo(), new ToDo()));
        when(userService.findUserById(any(long.class))).thenReturn(user);

        mockMvc.perform(post("/users/1/delete")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/users/all?badDeleteUserId=1"));
    }

    @Test
    @DisplayName("showUserList() should return user-list view with a list of users")
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
