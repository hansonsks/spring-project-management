package com.example.Todo_list.unit.controller;

import com.example.Todo_list.controller.LoginController;
import com.example.Todo_list.entity.User;
import com.example.Todo_list.security.WebSecurityUserDetails;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static com.example.Todo_list.unit.controller.utils.ControllerTestUtils.createRole;
import static com.example.Todo_list.unit.controller.utils.ControllerTestUtils.createUser;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(LoginController.class)
public class LoginControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Test")
    void testUnauthorizedUserLoginPageView() throws Exception {
        MvcResult result = mockMvc.perform(get("/login-form"))
                .andExpect(status().is3xxRedirection())
                .andReturn();

        String redirectedUrl = result.getResponse().getRedirectedUrl();
        assert redirectedUrl != null;
        assertTrue(redirectedUrl.matches(".*/oauth2/authorization/github") || "/login-page".equals(redirectedUrl));
    }

    @Test
    @DisplayName("Test")
    void testUnauthorizedUserHomeView() throws Exception {
        MvcResult result = mockMvc.perform(get("/home"))
                .andExpect(status().is3xxRedirection())
                .andReturn();

        String redirectedUrl = result.getResponse().getRedirectedUrl();
        assert redirectedUrl != null;
        assertTrue(redirectedUrl.matches(".*/oauth2/authorization/github") || "/home".equals(redirectedUrl));
    }

    @Test
    @DisplayName("Test")
    void testAuthorizedUserHomeView() throws Exception {
        User user = createUser();
        user.setRole(createRole());

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                new WebSecurityUserDetails(user),
                "password",
                List.of(new SimpleGrantedAuthority(user.getRole().getName()))
        );
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        mockMvc.perform(get("/home"))
                .andExpect(status().isOk())
                .andExpect(view().name("home"));
    }
}
