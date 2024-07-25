package com.example.Todo_list.controller;

import com.example.Todo_list.service.CommentService;
import com.example.Todo_list.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;


/**
 * Controller class for handling Task Comments
 */
@Controller
@RequestMapping("/comments")
@RequiredArgsConstructor
public class TaskCommentController {

    private final static Logger logger = LoggerFactory.getLogger(TaskCommentController.class);
    private final CommentService commentService;
    private final UserService userService;

    /**
     * Display all comments by when they were created
     *
     * @param model Model object
     * @return comments-all.html
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/all")
    public String showAllComments(Model model) {
        logger.info("TaskCommentController.showAllComments(): Displaying all comments ...");
        model.addAttribute("comments", commentService.findAllComments()
                                                     .stream()
                                                     .sorted((c1, c2) -> c2.getCreatedAt().compareTo(c1.getCreatedAt()))
                                                     .toList());
        return "comments-all";
    }

    /**
     * Display all comments by user in order of creation
     *
     * @param userId User ID
     * @param model Model object
     * @return comments-user.html
     */
    @PreAuthorize("hasAuthority('ADMIN') or #userId == authentication.principal.id")
    @GetMapping("/all/users/{user_id}")
    public String showAllCommentsByUserId(@PathVariable("user_id") Long userId, Model model) {
        logger.info("TaskCommentController.showAllCommentsByUserId(): Displaying all comments by userId=" + userId);
        model.addAttribute("comments", commentService.findCommentByUser(userService.findUserById(userId))
                                                     .stream()
                                                     .sorted((c1, c2) -> c2.getCreatedAt().compareTo(c1.getCreatedAt()))
                                                     .toList());
        return "comments-user";
    }
}
