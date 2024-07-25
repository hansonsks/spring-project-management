package com.example.Todo_list.controller;

import com.example.Todo_list.entity.User;
import com.example.Todo_list.service.RoleService;
import com.example.Todo_list.service.UserService;
import com.example.Todo_list.utils.PasswordService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

/**
 * Controller class for handling User operations
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final static Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;
    private final RoleService roleService;
    private final PasswordService passwordService;

    /**
     * Display the user registration form
     * @param model Model
     * @return user-create.html
     */
    @GetMapping("/create")
    public String showUserRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        logger.info("UserController.showUserRegistrationForm(): Displaying account registration page");
        return "user-create";
    }

    /**
     * Create a new user
     * @param user User
     * @param result BindingResult
     * @return user-create.html or redirect:/login-form?signUpSuccess=true
     */
    @PostMapping("/create")
    public String createUser(
            @Valid @ModelAttribute("user") User user,
            BindingResult result
    ) {
        logger.info("UserController.createUser(): Attempting to create user");

        if (result.hasErrors()) {
            if (result.hasFieldErrors("firstName")) {
                logger.error("UserController.createUser(): Bad first name");
                result.reject("error.firstName");
            }

            if (result.hasFieldErrors("lastName")) {
                logger.error("UserController.createUser(): Bad last name");
                result.reject("error.lastName");
            }

            if (result.hasFieldErrors("email")) {
                logger.error("UserController.createUser(): Invalid Email");
                result.rejectValue("email", "error.email", "Email is already in use.");
            }

            if (result.hasFieldErrors("password")) {
                logger.error("UserController.createUser(): Password too weak");
                result.reject("error.password");
            }

            logger.error("UserController.createUser(): Failed to create user using given information");
            return "user-create";
        }

        try {
            userService.findUserByEmail(user.getEmail());
            logger.info("UserController.createUser(): " +
                        "Pre-existing user with email=" + user.getEmail() + " was found, aborting account creation");
            result.rejectValue("email", "error.email", "Email is already in use.");
            return "user-create";
        } catch (EntityNotFoundException e) {
            logger.info("UserController.createUser(): " +
                        "No user with email=" + user.getEmail() + " was found, continuing account creation");
        }

        user.setRole(roleService.findRoleByName("USER"));
        userService.save(user);
        return "redirect:/login-form?signUpSuccess=true";
    }

    /**
     * Display user information
     * @param userId the user id
     * @param model Model
     * @return user-info.html
     */
    @PreAuthorize("hasAuthority('ADMIN') or #userId == authentication.principal.id")
    @GetMapping("/{user_id}/read")
    public String displayUserInfo(@PathVariable("user_id") Long userId, Model model) {
        User user = userService.findUserById(userId);
        logger.info("UserController.displayUser(): Found " + user);
        model.addAttribute("user", user);
        return "user-info";
    }

    /**
     * Display user update page
     * @param userId the user id
     * @param model Model
     * @return user-update.html
     */
    @PreAuthorize("hasAuthority('ADMIN') or #userId == authentication.principal.id")
    @GetMapping("/{user_id}/update")
    public String showUserUpdatePage(@PathVariable("user_id") Long userId, Model model) {
        User user = userService.findUserById(userId);
        model.addAttribute("user", user);
        model.addAttribute("roles", roleService.findAllRoles());
        logger.info("UserController.showUserUpdatePage(): Displaying user update page with " + user);
        return "user-update";
    }

    /**
     * Update user information
     * @param userId the user id
     * @param roleId the role id
     * @param oldPassword the old password
     * @param model Model
     * @param newUser the new user information
     * @param result BindingResult
     * @return user-update.html or redirect:/users/{id}/read
     */
    // TODO: Refactor this method to result.reject() instead of redirecting to the same page
    @PreAuthorize("hasAuthority('ADMIN') or #userId == authentication.principal.id")
    @PostMapping("/{user_id}/update")
    public String updateUser(
            @PathVariable("user_id") Long userId,
            @RequestParam("roleId") Long roleId,
            @RequestParam("oldPassword") String oldPassword,
            Model model,
            @Valid @ModelAttribute("user") User newUser,
            BindingResult result
    ) {
        User oldUser = userService.findUserById(userId);
        logger.info("UserController.updateUser(): Attempting to update " + oldUser);

        if (result.hasErrors()) {
            newUser.setRole(oldUser.getRole());
            model.addAttribute("roles", roleService.findAllRoles());

            if (result.hasFieldErrors("firstName")) {
                logger.error("UserController.updateUser(): Your first name must " +
                             "have a minimum of 3 characters and a maximum of 255 characters");
                return String.format("redirect:/users/%d/update?badFirstName=true", userId);
            }

            if (result.hasFieldErrors("lastName")) {
                logger.error("UserController.updateUser(): Your last name must have a maximum of 255 characters");
                return String.format("redirect:/users/%d/update?badLastName=true", userId);
            }

            if (result.hasFieldErrors("password")) {
                logger.error("UserController.updateUser(): Password too weak");
                return String.format("redirect:/users/%d/update?weakNewPassword=true", userId);
            }

            if (result.hasFieldErrors("email")) {
                logger.error("UserController.updateUser(): Invalid email");
                return String.format("redirect:/users/%d/update?badEmail=true", userId);
            }

            if (!passwordService.matches(oldPassword, oldUser.getPassword())) {
                logger.error("UserController.updateUser(): Incorrect old password");
                result.rejectValue("password", "error.password", "Old password does not match");

                newUser.setRole(oldUser.getRole());
                model.addAttribute("roles", roleService.findAllRoles());
                return String.format("redirect:/users/%d/update?incorrectOldPassword=true", userId);
            }

            if (newUser.getRole() == null) {
                logger.error("UserController.updateUser(): Error found in data received, aborting user update");
                return String.format("redirect:/users/%d/update?error=true", userId);
            }
        }

        logger.info("UserController.updateUser(): Updating to " + newUser);

        newUser.setRole(roleService.findRoleById(roleId));
        newUser.setPassword(passwordService.encodePassword(newUser.getPassword()));
        userService.updateUser(newUser);

        return "redirect:/logout?updateSuccess=true";
        // return String.format("redirect:/users/%d/read", id);
    }

    /**
     * Delete a user
     * @param userId the user id
     * @return redirect:/users/all
     */
    @PreAuthorize("hasAuthority('ADMIN') or #userId == authentication.principal.id")
    @GetMapping("/{user_id}/oauth-update")
    public String showOAuthUserUpdatePage(@PathVariable("user_id") Long userId, Model model) {
        User user = userService.findUserById(userId);
        model.addAttribute("user", user);
        model.addAttribute("roles", roleService.findAllRoles());
        logger.info("UserController.showOAuthUserUpdatePage(): Displaying user update page with " + user);
        return "oauth-user-update";
    }


    // TODO: Refactor this method to result.reject() instead of redirecting to the same page
    @PreAuthorize("hasAuthority('ADMIN') or #userId == authentication.principal.id")
    @PostMapping("/{user_id}/oauth-update")
    public String updateOAuthUser(
            @PathVariable("user_id") Long userId,
            @RequestParam("roleId") Long roleId,
            Model model,
            @Valid @ModelAttribute("user") User newUser,
            BindingResult result
    ) {
        User oldUser = userService.findUserById(userId);
        logger.info("UserController.updateOAuthUser(): Attempting to update " + oldUser);

        if (result.hasErrors()) {
            newUser.setRole(oldUser.getRole());
            model.addAttribute("roles", roleService.findAllRoles());

            if (result.hasFieldErrors("firstName")) {
                logger.error("UserController.updateOAuthUser(): Your first name must have a minimum of 3 characters " +
                        "and a maximum of 255 characters");
                return String.format("redirect:/users/%d/oauth-update?badFirstName=true", userId);
            }

            if (result.hasFieldErrors("email")) {
                logger.error("UserController.updateOAuthUser(): Invalid email");
                return String.format("redirect:/users/%d/oauth-update?badEmail=true", userId);
            }
        }

        logger.info("UserController.updateOAuthUser(): Updating to " + newUser);

        newUser.setPassword(oldUser.getPassword());
        newUser.setRole(roleService.findRoleById(roleId));
        userService.updateUser(newUser);

        return "redirect:/logout?updateSuccess=true";
        // return String.format("redirect:/users/%d/read", id);
    }

    /**
     * Delete a user
     * @param userId the user id
     * @return redirect:/users/all
     */
    @PreAuthorize("hasAuthority('ADMIN') and #userId != authentication.principal.id")
    @PostMapping("/{user_id}/delete")
    public String deleteUser(@PathVariable("user_id") Long userId) {
        if (userService.findUserById(userId).getCollaborators().isEmpty()) {
            logger.info("UserController.deleteUser(): Deleting user with userId=" + userId);
            userService.deleteUserById(userId);
            return "redirect:/users/all";
        }

        logger.error("UserController.deleteUser(): Unable to delete user with collaborators with userId= " + userId);
        return String.format("redirect:/users/all?badDeleteUserId=%d", userId);
    }

    /**
     * Display all users
     * @param model Model
     * @param badDeleteUserId the user id
     * @return user-list.html
     */
    @GetMapping("/all")
    public String showUserList(Model model, @RequestParam(name = "badDeleteUserId", required = false) Long badDeleteUserId) {
        model.addAttribute("users", userService.findAllUsers());
        model.addAttribute("badDeleteUserId", badDeleteUserId);

        logger.info("UserController.showUserList(): Displaying all users");
        return "user-list";
    }
}