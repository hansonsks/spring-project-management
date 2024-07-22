package com.example.Todo_list.controller;

import com.example.Todo_list.dto.TaskDTO;
import com.example.Todo_list.dto.TaskTransformer;
import com.example.Todo_list.entity.*;
import com.example.Todo_list.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * Controller class for handling Task-related operations

 */
@Controller
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final static Logger logger = LoggerFactory.getLogger(TaskController.class);
    private final TaskService taskService;
    private final ToDoService toDoService;
    private final StateService stateService;
    private final UserService userService;
    private final CommentService commentService;
    private final NotificationService notificationService;  // TODO: Scan comments for @mentions and send notifications

    /**
     * Display all tasks of a specific ToDo
     * @param todoId
     * @param model
     * @return
     */
    @PreAuthorize("hasAuthority('ADMIN') or " +
                "principal.id == @toDoServiceImpl.findToDoById(#todoId).owner.id or " +
                "@toDoServiceImpl.findToDoById(#todoId).collaborators.contains(@userServiceImpl.findUserById(principal.id))")
    @GetMapping("/create/todos/{todo_id}")
    public String showTaskCreationForm(@PathVariable("todo_id") Long todoId, Model model) {
        model.addAttribute("task", new TaskDTO());
        model.addAttribute("todo", toDoService.findToDoById(todoId));
        model.addAttribute("priorities", Priority.values());
        logger.info("TaskController.showTaskCreationForm(): Displaying task creation form");
        return "task-create";
    }

    /**
     * Create a new task
     * @param todoId
     * @param model
     * @param taskDTO
     * @param result
     * @return
     */
    @PreAuthorize("hasAuthority('ADMIN') or " +
                "principal.id == @toDoServiceImpl.findToDoById(#todoId).owner.id or " +
                "@toDoServiceImpl.findToDoById(#todoId).collaborators.contains(@userServiceImpl.findUserById(principal.id))")
    @PostMapping("/create/todos/{todo_id}")
    public String createTask(
            @PathVariable("todo_id") Long todoId,
            Model model,
            @Valid @ModelAttribute("task") TaskDTO taskDTO,
            BindingResult result
    ) {
        logger.info("TaskController.createTask(): Attempting to create task for todoId=" + todoId);

        // Check if the deadline is valid (not null and not before the current deadline)
        if (taskDTO.getDeadline() == null || taskDTO.getDeadline().isBefore(LocalDateTime.now())) {
            logger.error("TaskController.createTask(): Deadline is invalid, aborting task creation");
            model.addAttribute("todo", toDoService.findToDoById(todoId));
            model.addAttribute("priorities", Priority.values());
            // TODO: Add error <div> for invalid deadline
            return "task-update";
        }

        if (result.hasErrors()) {
            logger.error("TaskController.createTask(): Error found in data received, aborting task creation");
            model.addAttribute("todo", toDoService.findToDoById(todoId));
            model.addAttribute("priorities", Priority.values());
            return "task-create";
        }

        Task task = TaskTransformer.convertDTOToEntity(
                taskDTO,
                toDoService.findToDoById(taskDTO.getToDoId()),
                stateService.findStateByName("New")
        );

        logger.info("TaskController.createTask(): Saving " + task);
        taskService.save(task);
        return String.format("redirect:/todos/%d/tasks", todoId);
    }

    /**
     * Display the task update form
     * @param taskId
     * @param model
     * @return
     */
    @PreAuthorize("hasAuthority('ADMIN') or " +
                "principal.id == @taskServiceImpl.findTaskById(taskId).todo.owner.id or " +
                "@taskServiceImpl.findTaskById(taskId).todo.collaborators.contains(@userServiceImpl.findUserById(principal.id))")
    @GetMapping("/{task_id}/update")
    public String showTaskUpdateForm(@PathVariable("task_id") Long taskId, Model model) {
        logger.info("TaskController.showTaskUpdateForm(): Displaying task update form");
        prepareModelForTaskUpdate(taskId, model);
        return "task-update";
    }

    /**
     * Update a task
     * @param taskId
     * @param model
     * @param taskDTO
     * @param result
     * @return
     */
    // TODO: Handle task update errors by rejecting them and displaying an error <div>
    @PreAuthorize("hasAuthority('ADMIN') or " +
                "principal.id == @taskServiceImpl.findTaskById(taskId).todo.owner.id or " +
                "@taskServiceImpl.findTaskById(taskId).todo.collaborators.contains(@userServiceImpl.findUserById(principal.id))")
    @PostMapping("/{task_id}/update")
    public String updateTask(
            @PathVariable("task_id") Long taskId,
            Model model,
            @Valid @ModelAttribute TaskDTO taskDTO,
            BindingResult result
    ) {
        logger.info("TaskController.updateTask(): Attempting to update task with taskId=" + taskId);

        // Check if the deadline is valid (not null and not before the current deadline)
        if (taskDTO.getDeadline() == null || taskDTO.getDeadline().isBefore(LocalDateTime.now())) {
            logger.error("TaskController.updateTask(): Deadline is invalid, aborting task update");
            prepareModelForTaskUpdate(taskId, model);
            return "task-update";
        }

        if (result.hasErrors()) {
            logger.error("TaskController.updateTask(): Error found in data received, aborting task update");
            prepareModelForTaskUpdate(taskId, model);
            return "task-update";
        }

        taskDTO.setId(taskId);
        Task task = TaskTransformer.convertDTOToEntity(
                taskDTO,
                toDoService.findToDoById(taskDTO.getToDoId()),
                stateService.findStateByName(taskDTO.getState())
        );

        logger.info("TaskController.updateTask(): Updating " + task);
        taskService.save(task);
        return String.format("redirect:/tasks/%d/update", task.getId());
    }

    /**
     * Prepare the model for task update
     * @param id
     * @param model
     */
    private void prepareModelForTaskUpdate(Long id, Model model) {
        Task task = taskService.findTaskById(id);
        User owner = task.getTodo().getOwner();
        TaskDTO taskDTO = TaskTransformer.convertEntityToDTO(task);
        model.addAttribute("task", taskDTO);
        model.addAttribute("toDoId", taskDTO.getToDoId());
        model.addAttribute("priorities", Priority.values());
        model.addAttribute("states", stateService.findAllStates());
        model.addAttribute("assignedUsers", taskDTO.getAssignedUsers());

        List<User> allUsers = userService.findAllUsers();
        List<User> assignedUsers = taskService.findTaskById(id).getAssignedUsers();
        List<User> availableUsers = new ArrayList<>(allUsers.stream().filter(
                user -> !assignedUsers.contains(user) && toDoService.findToDoById(taskDTO.getToDoId()).getCollaborators().contains(user)
        ).toList());

        if (!assignedUsers.contains(owner)) {
            availableUsers.add(owner);
        }

        model.addAttribute("availableUsers", availableUsers);

        logger.error("TaskController.prepareModelForTaskUpdate(): Prepared model for task update {}", task);
        logger.error("TaskController.prepareModelForTaskUpdate(): Prepared model for task update {}", taskDTO);
    }

    /**
     * Display a task
     * @param taskId
     * @param model
     * @return
     */
    @PreAuthorize("hasAuthority('ADMIN') or " +
                "principal.id == @taskServiceImpl.findTaskById(#taskId).todo.owner.id or " +
                "@taskServiceImpl.findTaskById(#taskId).todo.collaborators.contains(@userServiceImpl.findUserById(principal.id))")
    @GetMapping("/{task_id}/read")
    public String displayTask(@PathVariable("task_id") Long taskId, Model model) {
        Task task = taskService.findTaskById(taskId);
        List<Comment> comments = task.getComments().stream()
                                                    .sorted(Comparator.comparing(Comment::getCreatedAt).reversed())
                                                    .toList();
        TaskDTO taskDTO = TaskTransformer.convertEntityToDTO(task);
        model.addAttribute("task", taskDTO);
        model.addAttribute("comments", comments);
        logger.info("TaskController.displayTask(): Displaying " + task);
        return "task-info";
    }

    /**
     * Delete a task
     * @param taskId
     * @param todoId
     * @return
     */
    @PreAuthorize("hasAuthority('ADMIN') or " +
                "principal.id == @toDoServiceImpl.findToDoById(#todoId).owner.id or " +
                "@toDoServiceImpl.findToDoById(#todoId).collaborators.contains(@userServiceImpl.findUserById(principal.id))")
    @PostMapping("/{task_id}/delete/todos/{todo_id}")
    public String deleteTask(@PathVariable("task_id") Long taskId, @PathVariable("todo_id") Long todoId) {
        logger.info("TaskController.deleteTask(): Deleting task with taskId=" + taskId + " of todo with todoId=" + todoId);
        taskService.deleteTaskById(taskId);
        return String.format("redirect:/todos/%d/tasks", todoId);
    }

    /**
     * Add a user to a task
     * @param taskId
     * @param userId
     * @return
     */
    @PreAuthorize("hasAuthority('ADMIN') or principal.id == @taskServiceImpl.findTaskById(#taskId).todo.owner.id")
    @PostMapping("/{task_id}/update/add-user")
    public String addAssignedUser(@PathVariable("task_id") Long taskId, @RequestParam("user_id") Long userId) {
        if (userId == -1) {
            logger.error("TaskController.addAssignedUser(): userId is invalid: " + userId);
            return String.format("redirect:/tasks/%d/update", taskId);
        }

        notificationService.sendNotificationToUserId(
                userId,
                "You have been assigned to a Task",
                String.format("You have been assigned to Task [%s] in Project [%s]",
                        taskService.findTaskById(taskId).getName(), taskService.findTaskById(taskId).getTodo().getTitle())
        );

        logger.info("TaskController.addAssignedUser(): Adding user with userId=" + userId + " to task with taskId=" + taskId);
        taskService.assignTaskToUser(taskId, userId);
        return String.format("redirect:/tasks/%d/update", taskId);
    }

    /**
     * Remove a user from a task
     * @param taskId
     * @param userId
     * @return
     */
    @PreAuthorize("hasAuthority('ADMIN') or principal.id == @taskServiceImpl.findTaskById(#taskId).todo.owner.id")
    @PostMapping("/{task_id}/update/remove-user")
    public String removeAssignedUser(@PathVariable("task_id") Long taskId, @RequestParam("user_id") Long userId) {
        if (userId == -1) {
            logger.error("TaskController.removeAssignedUser(): userId is invalid: " + userId);
            return String.format("redirect:/tasks/%d/update", taskId);
        }

        Task task = taskService.findTaskById(taskId);
        notificationService.sendNotificationToUserId(
                userId,
                "You have been removed from a Task",
                String.format("You have been removed from Task [%s] in Project [%s]",
                        task.getName(), task.getTodo().getTitle())
        );

        logger.info("TaskController.removeAssignedUser(): Removing user with userId=" + userId + " from task with taskId=" + taskId);
        taskService.removeTaskFromUser(taskId, userId);
        return String.format("redirect:/tasks/%d/update", taskId);
    }

    /**
     * Create a comment
     * @param taskId
     * @param content
     * @param userId
     * @return
     */
    @PreAuthorize("hasAuthority('ADMIN') or " +
                "principal.id == @taskServiceImpl.findTaskById(#taskId).todo.owner.id or " +
                "@taskServiceImpl.findTaskById(#taskId).todo.collaborators.contains(@userServiceImpl.findUserById(principal.id))")
    @PostMapping("/{task_id}/comments/create")
    public String createComment(@PathVariable("task_id") Long taskId,
                                @RequestParam("comment") String content,
                                @RequestParam("user_id") Long userId) {
        // Check if comment is empty or too long
        if (content == null || content.isEmpty() || content.length() > 255) {
            logger.error("TaskController.createComment(): Comment content is empty or too long, aborting update");
            return String.format("redirect:/tasks/%d/read?invalidComment=true", taskId);
        }

        Task task = taskService.findTaskById(taskId);
        logger.info("TaskController.createComment(): Creating comment for task with taskId=" + taskId);

        Comment comment = new Comment();
        comment.setContent(content);
        comment.setUser(userService.findUserById(userId));
        comment.setTask(task);

        commentService.updateComment(comment);
        logger.info("TaskController.createComment(): Saved " + comment + " for task with taskId=" + taskId);

        checkTaggedUsers(comment, taskId);

        return String.format("redirect:/tasks/%d/read", taskId);
    }

    /**
     * Update a comment
     * @param taskId
     * @param commentId
     * @param content
     * @return
     */
    @PreAuthorize("hasAuthority('ADMIN') or principal.id == @commentServiceImpl.findCommentById(#commentId).user.id")
    @PostMapping("/{task_id}/comments/{comment_id}/update")
    public String updateComment(@PathVariable("task_id") Long taskId,
                                @PathVariable("comment_id") Long commentId,
                                @RequestParam("comment") String content) {
        if (content == null || content.isEmpty() || content.length() > 255) {
            logger.error("TaskController.updateComment(): Comment content is empty or too long, aborting update");
            return String.format("redirect:/tasks/%d/read?invalidComment=true", taskId);
        }

        logger.info("TaskController.updateComment(): " +
                    "Updating comment with commentId=" + commentId + " of task with taskId=" + taskId);

        Comment comment = commentService.findCommentById(commentId);
        comment.setContent(content);
        comment.setIsEdited(true);

        commentService.save(comment);
        logger.info("TaskController.updateComment(): Updated " + comment);

        checkTaggedUsers(comment, taskId);

        return String.format("redirect:/tasks/%d/read", taskId);
    }

    /**
     * Check for tagged users in a comment
     * @param comment
     * @param taskId
     */
    private void checkTaggedUsers(Comment comment, Long taskId) {
        logger.info("TaskController.checkTaggedUsers(): Checking for tagged users in comment");

        Task task = taskService.findTaskById(taskId);
        commentService.findTaggedUserInComment(comment).forEach(user -> {
            notificationService.sendNotificationToUser(
                    user,
                    "You have been mentioned in a comment",
                    String.format("You have been mentioned in a comment by [%s] in Task [%s] in Project [%s]",
                            task.getTodo().getOwner().getFirstName(), task.getName(), task.getTodo().getTitle())
            );
        });
    }

    /**
     * Delete a comment
     * @param taskId
     * @param commentId
     * @return
     */
    @PreAuthorize("hasAuthority('ADMIN') or principal.id == @commentServiceImpl.findCommentById(#commentId).user.id")
    @PostMapping("/{task_id}/comments/{comment_id}/delete")
    public String deleteComment(@PathVariable("task_id") Long taskId, @PathVariable("comment_id") Long commentId) {
        logger.info("TaskController.deleteComment(): " +
                    "Deleting comment with commentId=" + commentId + " of task with taskId=" + taskId);
        taskService.findTaskById(taskId); // Check if task exists (will throw exception if not
        commentService.deleteCommentById(commentId);
        return String.format("redirect:/tasks/%d/read", taskId);
    }

    /**
     * Display all tasks of a specific user
     * @param userId
     * @param model
     * @return
     */
    @GetMapping("/all/users/{user_id}")
    public String displayAllTasksOfUser(@PathVariable("user_id") Long userId, Model model) {
        List<Task> tasks = taskService.findAssignedTasksByUserId(userId);
        model.addAttribute("user", userService.findUserById(userId));
        model.addAttribute("tasks", tasks);
        return "user-tasks";
    }
}
