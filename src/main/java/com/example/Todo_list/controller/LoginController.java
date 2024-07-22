package com.example.Todo_list.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class LoginController {

    @GetMapping("/login-form")
    public String login() {
        return "login-page";
    }

    @GetMapping({"/", "home"})
    public String home() {
        return "home";
    }

    @GetMapping("/about")
    public String about() {
        return "about";
    }

    @GetMapping("/javadoc")
    public String javadoc() {
        return "forward:/javadoc/index.html";
    }
}
