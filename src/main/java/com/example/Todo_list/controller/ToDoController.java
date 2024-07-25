package com.example.Todo_list.controller;

import com.example.Todo_list.entity.Task;
import com.example.Todo_list.entity.ToDo;
import com.example.Todo_list.entity.User;
import com.example.Todo_list.security.access.AccessControlService;
import com.example.Todo_list.service.NotificationService;
import com.example.Todo_list.service.TaskService;
import com.example.Todo_list.service.ToDoService;
import com.example.Todo_list.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * Controller class for handling ToDo related operations
 */
@Controller
@RequestMapping("/todos")
@RequiredArgsConstructor
public class ToDoController {

    private final static Logger logger = LoggerFactory.getLogger(ToDoController.class);
    private final ToDoService toDoService;
    private final TaskService taskService;
    private final UserService userService;
    private final NotificationService notificationService;
    private final AccessControlService accessControlService;

    /**
     * Display the form for creating a new ToDo item
     * @param ownerId the id of the user who will own the ToDo item
     * @param model the model to be used in the view
     * @return the view to be displayed
     */
    @PreAuthorize("hasAuthority('ADMIN') or #ownerId == authentication.principal.id")
    @GetMapping("/create/users/{owner_id}")
    public String showToDoCreationForm(Authentication authentication, @PathVariable("owner_id") Long ownerId, Model model) {
        if (!accessControlService.hasAccess(authentication, ownerId)) {
            logger.info("ToDoController.showToDoCreationForm(): User does not have access to create ToDo");
            throw new AccessDeniedException("You do not have access to create a Project for this user");
        }

        model.addAttribute("todo", new ToDo());
        model.addAttribute("ownerId", ownerId);
        logger.info("ToDoController.showToDoCreationForm(): Displaying ToDo creation form");
        return "todo-create";
    }

    /**
     * Create a new ToDo item
     * @param ownerId the id of the user who will own the ToDo item
     * @param toDo the ToDo item to be created
     * @param result the result of the binding
     * @return the view to be displayed
     */
    @PreAuthorize("hasAuthority('ADMIN') or #ownerId == authentication.principal.id")
    @PostMapping("/create/users/{owner_id}")
    public String createToDo(
            Authentication authentication,
            @PathVariable("owner_id") Long ownerId,
            @Valid @ModelAttribute("todo") ToDo toDo,
            BindingResult result
    ) {
        if (!accessControlService.hasAccess(authentication, ownerId)) {
            logger.info("ToDoController.createToDo(): User does not have access to create ToDo");
            throw new AccessDeniedException("You do not have access to create a Project for this user");
        }

        logger.info("ToDoController.createToDo(): Attempting to create a ToDo item for user with userId=" + ownerId);

        if (result.hasErrors()) {
            logger.info("ToDoController.createToDo(): Error found in data received, aborting ToDo creation");
            return "todo-create";
        }

        toDo.setCreatedAt(ZonedDateTime.now());
        toDo.setOwner(userService.findUserById(ownerId));

        logger.info("ToDoController.createToDo(): Saving " + toDo);
        toDoService.save(toDo);
        return String.format("redirect:/todos/all/users/%d", ownerId);
    }

    /**
     * Display the form for creating a new Task item
     * @param todoId the id of the ToDo item to which the Task item will belong
     * @param model the model to be used in the view
     * @return the view to be displayed
     */
    @PreAuthorize("hasAuthority('ADMIN') or " +
            "principal.id == @toDoServiceImpl.findToDoById(#todoId).owner.id or " +
            "@toDoServiceImpl.findToDoById(#todoId).collaborators.contains(@userServiceImpl.findUserById(principal.id))")
    @GetMapping("/{todo_id}/tasks")
    public String displayToDo(Authentication authentication, @PathVariable("todo_id") Long todoId, Model model) {
        ToDo todo = toDoService.findToDoById(todoId);
        List<Long> permittedUserIds = new ArrayList<>(List.of(todo.getOwner().getId()));
        permittedUserIds.addAll(todo.getCollaborators().stream().map(User::getId).toList());

        if (!accessControlService.hasAccess(authentication, permittedUserIds)) {
            logger.info("ToDoController.displayToDo(): User does not have access to view ToDo");
            throw new AccessDeniedException("You do not have access to view this Project");
        }

        List<Task> tasks = taskService.findAllTasksOfToDo(todoId);
        List<User> users = userService.findAllUsers()
                                        .stream()
                                        .filter(user -> !Objects.equals(user.getId(), todo.getOwner().getId()))
                                        .toList();
        List<Task> sortedTasks = tasks.stream().sorted(Comparator.comparing(Task::getPriority).reversed()).toList();

        model.addAttribute("todo", todo);
        model.addAttribute("tasks", sortedTasks);
        model.addAttribute("users", users);

        logger.info("ToDoController.displayToDo(): Displaying ToDo with toDoId=" + todoId);

        return "todo-tasks";
    }

    /**
     * Create a new Task item
     * @param todoId the id of the ToDo item to which the Task item will belong
     * @param ownerId the id of the user who owns the ToDo ite
     * @param model the model to be used in the view
     * @return the view to be displayed
     */
    @PreAuthorize("hasAuthority('ADMIN') or " +
            "principal.id == @toDoServiceImpl.findToDoById(#todoId).owner.id or " +
            "@toDoServiceImpl.findToDoById(#todoId).collaborators.contains(@userServiceImpl.findUserById(principal.id))")
    @GetMapping("/{todo_id}/update/users/{owner_id}")
    public String showToDoUpdateForm(
            Authentication authentication,
            @PathVariable("todo_id") Long todoId,
            @PathVariable("owner_id") Long ownerId,
            Model model
    ) {
        ToDo todo = toDoService.findToDoById(todoId);
        List<Long> permittedUserIds = new ArrayList<>(List.of(todo.getOwner().getId()));
        permittedUserIds.addAll(todo.getCollaborators().stream().map(User::getId).toList());

        if (!accessControlService.hasAccess(authentication, permittedUserIds)) {
            logger.info("ToDoController.showToDoUpdateForm(): User does not have access to update ToDo");
            throw new AccessDeniedException("You do not have access to update this Project");
        }

        ToDo toDo = toDoService.findToDoById(todoId);
        model.addAttribute("todo", toDo);
        logger.info("ToDoController.showToDoUpdateForm(): Displaying update form of " + toDo);
        return "todo-update";
    }

    /**
     * Update a ToDo item
     * @param todoId the id of the ToDo item to be updated
     * @param ownerId the id of the user who owns the ToDo item
     * @param todo the ToDo item to be updated
     * @param result the result of the binding
     * @return the view to be displayed
     */
    @PreAuthorize("hasAuthority('ADMIN') or " +
                "principal.id == @toDoServiceImpl.findToDoById(#todoId).owner.id or " +
                "@toDoServiceImpl.findToDoById(#todoId).collaborators.contains(@userServiceImpl.findUserById(principal.id))")
    @PostMapping("/{todo_id}/update/users/{owner_id}")
    public String updateToDo(
            Authentication authentication,
            @PathVariable("todo_id") Long todoId,
            @PathVariable("owner_id") Long ownerId,
            @Valid @ModelAttribute("todo") ToDo todo,
            BindingResult result)
    {
        ToDo oldTodo = toDoService.findToDoById(todoId);
        List<Long> permittedUserIds = new ArrayList<>(List.of(oldTodo.getOwner().getId()));
        permittedUserIds.addAll(todo.getCollaborators().stream().map(User::getId).toList());

        if (!accessControlService.hasAccess(authentication, permittedUserIds)) {
            logger.info("ToDoController.updateToDo(): User does not have access to update ToDo");
            throw new AccessDeniedException("You do not have access to update this Project");
        }

        logger.info("ToDoController.updateToDo(): Attempting to update ToDo with toDoId=" + todoId);

        if (result.hasErrors()) {
            todo.setOwner(userService.findUserById(ownerId));
            logger.info("ToDoController.updateToDo(): Error found in data received, aborting ToDo update");
            return "todo-update";
        }

        todo.setOwner(oldTodo.getOwner());
        todo.setCollaborators(oldTodo.getCollaborators());

        logger.info("ToDoController.updateToDo(): Updating " + todo);
        toDoService.save(todo);
        return String.format("redirect:/todos/all/users/%d", ownerId);
    }

    /**
     * Delete a ToDo item
     * @param todoId the id of the ToDo item to be deleted
     * @param ownerId the id of the user who owns the ToDo item
     * @return the view to be displayed
     */
    @PreAuthorize("hasAuthority('ADMIN') or " +
                "principal.id == @toDoServiceImpl.findToDoById(#todoId).owner.id or " +
                "@toDoServiceImpl.findToDoById(#todoId).collaborators.contains(@userServiceImpl.findUserById(principal.id))")
    @PostMapping("/{todo_id}/delete/users/{owner_id}")
    public String deleteToDo(Authentication authentication, @PathVariable("todo_id") Long todoId,
                             @PathVariable("owner_id") Long ownerId) {
        ToDo todo = toDoService.findToDoById(todoId);
        List<Long> permittedUserIds = new ArrayList<>(List.of(todo.getOwner().getId()));
        permittedUserIds.addAll(todo.getCollaborators().stream().map(User::getId).toList());

        if (!accessControlService.hasAccess(authentication, permittedUserIds)) {
            logger.info("ToDoController.deleteToDo(): User does not have access to delete ToDo");
            throw new AccessDeniedException("You do not have access to delete this Project");
        }

        logger.info("ToDoController.deleteToDo(): Deleting ToDo with toDoId=" + todoId);
        toDoService.deleteToDoById(todoId);
        return String.format("redirect:/todos/all/users/%d", ownerId);
    }

    /**
     * Display the form for creating a new Task item
     * @param userId the id of the user who will own the Task item
     * @param model the model to be used in the view
     * @return the view to be displayed
     */
    @PreAuthorize("hasAuthority('ADMIN') or #userId == authentication.principal.id")
    @GetMapping("/all/users/{user_id}")
    public String displayAllToDosOfUser(Authentication authentication, @PathVariable("user_id") Long userId, Model model) {
        if (!accessControlService.hasAccess(authentication, userId)) {
            logger.info("ToDoController.displayAllToDo(): User does not have access to view ToDos");
            throw new AccessDeniedException("You do not have access to view Projects for this user");
        }

        List<ToDo> todos = toDoService.findAllToDoOfUserId(userId);
        List<ToDo> sortedTodos = todos.stream().sorted(Comparator.comparingLong(ToDo::getId)).toList();
        model.addAttribute("todos", sortedTodos);
        model.addAttribute("user", userService.findUserById(userId));
        logger.info("ToDoController.displayAllToDo(): Displaying all ToDos of user with userId=" + userId);
        return "todos-user";
    }

    /**
     * Display all ToDo items
     * @param userId the id of the user who owns the ToDo items
     * @param model the model to be used in the view
     * @return the view to be displayed
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/all")
    public String displayAllToDos(Authentication authentication, @RequestParam("user_id") Long userId, Model model) {
        if (!accessControlService.hasAdminAccess(authentication)) {
            logger.info("ToDoController.displayAllToDo(): User does not have access to view all ToDos");
            throw new AccessDeniedException("You do not have access to view all Projects");
        }

        List<ToDo> todos = toDoService.findAllToDos();
        List<ToDo> sortedTodos = todos.stream().sorted(Comparator.comparingLong(ToDo::getId)).toList();
        model.addAttribute("todos", sortedTodos);
        model.addAttribute("user", userService.findUserById(userId));
        logger.info("ToDoController.displayAllToDo(): Displaying all ToDos");
        return "todos-all";
    }

    /**
     * Display all ToDo items
     * @param userId the id of the user who owns the ToDo items
     * @param todoId the id of the ToDo item to which the Task item will belong
     * @return the view to be displayed
     */
    @PreAuthorize("hasAuthority('ADMIN') or authentication.principal.id == @toDoServiceImpl.findToDoById(#todoId).owner.id")
    @PostMapping("/{todo_id}/add")
    public String addCollaborator(Authentication authentication, @PathVariable("todo_id") Long todoId,
                                  @RequestParam("user_id") Long userId) {
        if (!accessControlService.hasAccess(authentication, toDoService.findToDoById(todoId).getOwner().getId())) {
            logger.info("ToDoController.addCollaborator(): User does not have access to add collaborator");
            throw new AccessDeniedException("You do not have access to add a collaborator to this Project");
        }

        if (userId == -1) {
            logger.info("ToDoController.addCollaborator(): userId is invalid: " + userId);
            return String.format("redirect:/todos/%d/tasks", todoId);
        }

        ToDo todo = toDoService.findToDoById(todoId);
        logger.info("ToDoController.addCollaborator(): " +
                "Adding " + todo + " to" + userService.findUserById(userId));

        notificationService.sendNotificationToUserId(
                userId,
                "Added to Project",
                String.format("You have been added to a Project [%s]", todo.getTitle())
        );

        toDoService.addCollaborator(todoId, userId);
        logger.info("ToDoController.addCollaborator(): Updated ToDo=" + todo);
        return String.format("redirect:/todos/%d/tasks", todoId);
    }

    /**
     * Display all ToDo items
     * @param todoId the id of the ToDo item to which the Task item will belong
     * @param userId the id of the user who owns the ToDo items
     * @return the view to be displayed
     */
    @PreAuthorize("hasAuthority('ADMIN') or authentication.principal.id == @toDoServiceImpl.findToDoById(#todoId).owner.id")
    @PostMapping("/{todo_id}/remove")
    public String removeCollaborator(Authentication authentication, @PathVariable("todo_id") Long todoId,
                                     @RequestParam("user_id") Long userId) {
        if (!accessControlService.hasAccess(authentication, toDoService.findToDoById(todoId).getOwner().getId())) {
            logger.info("ToDoController.addCollaborator(): User does not have access to add collaborator");
            throw new AccessDeniedException("You do not have access to add a collaborator to this Project");
        }

        ToDo todo = toDoService.findToDoById(todoId);
        logger.info("ToDoController.removeCollaborator(): " +
                    "Removing " + todo + " from " + userService.findUserById(userId));

        notificationService.sendNotificationToUserId(
                userId,
                "Removed from Project",
                String.format("You have been removed from a Project [%s]", todo.getTitle())
        );

        toDoService.removeCollaborator(todoId, userId);
        logger.info("ToDoController.addCollaborator(): Updated ToDo=" + toDoService.findToDoById(todoId));
        return String.format("redirect:/todos/%d/tasks", todoId);
    }

}
