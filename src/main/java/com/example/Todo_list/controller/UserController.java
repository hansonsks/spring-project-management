package com.example.Todo_list.controller;

import com.example.Todo_list.entity.Role;
import com.example.Todo_list.entity.User;
import com.example.Todo_list.service.RoleService;
import com.example.Todo_list.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final static Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;
    private final RoleService roleService;
    private final BCryptPasswordEncoder passwordEncoder;

    @GetMapping("/create")
    public String showUserRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        logger.info("UserController.showUserRegistrationForm(): Displaying account registration page");
        return "user-create";
    }

    @PostMapping("/create")
    public String createUser(
            @Validated @ModelAttribute("user") User user,
            BindingResult result
    ) {
        logger.info("UserController.createUser(): Attempting to create user");

        if (result.hasErrors()) {
            if (result.hasFieldErrors("password")) {
                logger.error("UserController.createUser(): Password too weak");
                return "redirect:/users/create?badPassword=true";
            }

            logger.error("UserController.createUser(): Failed to create user using given information (Unknown Error)");
            return "redirect:/users/create?error=true";
        }

        try {
            userService.findUserByEmail(user.getEmail());
            logger.info("UserController.createUser(): " +
                        "Pre-existing user with email=" + user.getEmail() + " was found, aborting account creation");
            return "redirect:/users/create?badEmail=true";
        } catch (EntityNotFoundException e) {
            logger.info("UserController.createUser(): " +
                        "No user with email=" + user.getEmail() + " was found, continuing account creation");
        }

        user.setRole(roleService.findRoleByName("USER"));
        User newUser = userService.save(user);
        return String.format("redirect:/todos/all/users/%d", newUser.getId());
    }

    @PreAuthorize("hasAuthority('ADMIN') or #id==authentication.principal.id")
    @GetMapping("/{id}/read")
    public String displayUserInfo(@PathVariable Long id, Model model) {
        User user = userService.findUserById(id);
        logger.info("UserController.displayUser(): Found " + user);
        model.addAttribute("user", user);
        return "user-info";
    }

    @PreAuthorize("hasAuthority('ADMIN') or #id==authentication.principal.id")
    @GetMapping("/{id}/update")
    public String showUserUpdatePage(@PathVariable Long id, Model model) {
        User user = userService.findUserById(id);
        model.addAttribute("user", user);
        model.addAttribute("roles", roleService.findAllRoles());
        logger.info("UserController.showUserUpdatePage(): Displaying user update page with " + user);
        return "user-update";
    }

    @PreAuthorize("hasAuthority('ADMIN') or #id==authentication.principal.id")
    @PostMapping("/{id}/update")
    public String updateUser(
            @PathVariable Long id,
            @RequestParam("roleId") Long roleId,
            @RequestParam("oldPassword") String oldPassword,
            Model model,
            @Validated @ModelAttribute("user") User user,
            BindingResult result
    ) {
        User oldUser = userService.findUserById(id);
        Role oldRole = oldUser.getRole();

        logger.info("UserController.updateUser(): Attempting to update " + oldUser);

        if (result.hasErrors()) {
            user.setRole(oldUser.getRole());
            model.addAttribute("roles", roleService.findAllRoles());

            if (result.hasFieldErrors("password")) {
                logger.error("UserController.updateUser(): Password too weak");
                return String.format("redirect:/users/%d/update?weakNewPassword=true", id);
            }

            logger.error("UserController.updateUser(): Error found in data received, aborting user update");
            return "user-update?error=true";
        }

        if (!passwordEncoder.matches(oldPassword, oldUser.getPassword())) {
            logger.error("UserController.updateUser(): Incorrect old password");
            result.rejectValue("password", "error.password", "Old password does not match");
            user.setRole(oldUser.getRole());
            model.addAttribute("roles", roleService.findAllRoles());
            return String.format("redirect:/users/%d/update?incorrectOldPassword=true", id);
        }

        logger.info("UserController.updateUser(): Updating " + user);

        user.setRole(roleService.findRoleById(roleId));
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userService.updateUser(user);

        if (!oldRole.equals(user.getRole())) {
            return "redirect:/logout";
        }
        return String.format("redirect:/users/%d/read", id);
    }

    @PreAuthorize("hasAuthority('ADMIN') and #userId != authentication.principal.id")
    @GetMapping("/{user_id}/delete")
    public String deleteUser(@PathVariable("user_id") Long userId) {
        logger.info("UserController.deleteUser(): Deleting user with userId=" + userId);
        userService.deleteUserById(userId);
        return "redirect:/users/all";
    }

    @GetMapping("/all")
    public String showUserList(Model model) {
        model.addAttribute("users", userService.findAllUser());
        logger.info("UserController.showUserList(): Displaying all users");
        return "user-list";
    }
}
