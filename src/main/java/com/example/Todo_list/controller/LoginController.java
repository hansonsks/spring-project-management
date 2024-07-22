package com.example.Todo_list.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller for handling login and home page requests.
 */
@Controller
@RequiredArgsConstructor
public class LoginController {

    /**
     * Returns the login page.
     * @return the login page
     */
    @GetMapping("/login-form")
    public String login() {
        return "login-page";
    }

    /**
     * Returns the home page.
     * @return the home page
     */
    @GetMapping({"/", "home"})
    public String home() {
        return "home";
    }

    /**
     * Returns the about page.
     * @return the about page
     */
    @GetMapping("/about")
    public String about() {
        return "about";
    }

    /**
     * Returns the javadoc page.
     * @return the javadoc page
     */
    @GetMapping("/javadoc")
    public String javadoc() {
        return "forward:/javadoc/index.html";
    }
}
