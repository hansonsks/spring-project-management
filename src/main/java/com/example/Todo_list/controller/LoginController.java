package com.example.Todo_list.controller;

import com.example.Todo_list.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class LoginController {

    private final UserService userService;

    @GetMapping("/login-form")
    public String login() {
        return "login-page";
    }

    @GetMapping({"/", "home"})
    public String home(Model model) {
        model.addAttribute("users", userService.findAllUsers());
        return "home";
    }
}
