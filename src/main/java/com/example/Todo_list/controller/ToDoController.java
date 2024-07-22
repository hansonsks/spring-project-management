package com.example.Todo_list.controller;

import com.example.Todo_list.entity.Task;
import com.example.Todo_list.entity.ToDo;
import com.example.Todo_list.entity.User;
import com.example.Todo_list.service.NotificationService;
import com.example.Todo_list.service.TaskService;
import com.example.Todo_list.service.ToDoService;
import com.example.Todo_list.service.UserService;
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
import java.time.ZonedDateTime;
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

    /**
     * Display the form for creating a new ToDo item
     * @param ownerId
     * @param model
     * @return todo-create.html
     */
    @PreAuthorize("hasAuthority('ADMIN') or #ownerId == authentication.principal.id")
    @GetMapping("/create/users/{owner_id}")
    public String showToDoCreationForm(@PathVariable("owner_id") Long ownerId, Model model) {
        model.addAttribute("todo", new ToDo());
        model.addAttribute("ownerId", ownerId);
        logger.info("ToDoController.showToDoCreationForm(): Displaying ToDo creation form");
        return "todo-create";
    }

    /**
     * Create a new ToDo item
     * @param ownerId
     * @param toDo
     * @param result
     * @return redirect to all ToDos of the user
     */
    @PreAuthorize("hasAuthority('ADMIN') or #ownerId == authentication.principal.id")
    @PostMapping("/create/users/{owner_id}")
    public String createToDo(
            @PathVariable("owner_id") Long ownerId,
            @Valid @ModelAttribute("todo") ToDo toDo,
            BindingResult result
    ) {
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
     * @param todoId
     * @param model
     * @return task-create.html
     */
    @PreAuthorize("hasAuthority('ADMIN') or " +
            "principal.id == @toDoServiceImpl.findToDoById(#todoId).owner.id or " +
            "@toDoServiceImpl.findToDoById(#todoId).collaborators.contains(@userServiceImpl.findUserById(principal.id))")
    @GetMapping("/{todo_id}/tasks")
    public String displayToDo(@PathVariable("todo_id") Long todoId, Model model) {
        ToDo todo = toDoService.findToDoById(todoId);
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
     * @param todoId
     * @param ownerId
     * @param model
     * @return redirect to the ToDo item
     */
    @GetMapping("/{todo_id}/update/users/{owner_id}")
    public String showToDoUpdateForm(
            @PathVariable("todo_id") Long todoId,
            @PathVariable("owner_id") Long ownerId,
            Model model
    ) {
        ToDo toDo = toDoService.findToDoById(todoId);
        model.addAttribute("todo", toDo);
        logger.info("ToDoController.showToDoUpdateForm(): Displaying update form of " + toDo);
        return "todo-update";
    }

    /**
     * Update a ToDo item
     * @param todoId
     * @param ownerId
     * @param todo
     * @param result
     * @return redirect to all ToDos of the user
     */
    @PreAuthorize("hasAuthority('ADMIN') or " +
                "principal.id == @toDoServiceImpl.findToDoById(#todoId).owner.id or " +
                "@toDoServiceImpl.findToDoById(#todoId).collaborators.contains(@userServiceImpl.findUserById(principal.id))")
    @PostMapping("/{todo_id}/update/users/{owner_id}")
    public String updateToDo(
            @PathVariable("todo_id") Long todoId,
            @PathVariable("owner_id") Long ownerId,
            @Valid @ModelAttribute("todo") ToDo todo,
            BindingResult result)
    {
        logger.info("ToDoController.updateToDo(): Attempting to update ToDo with toDoId=" + todoId);

        if (result.hasErrors()) {
            todo.setOwner(userService.findUserById(ownerId));
            logger.info("ToDoController.updateToDo(): Error found in data received, aborting ToDo update");
            return "todo-update";
        }

        ToDo oldTodo = toDoService.findToDoById(todoId);
        todo.setOwner(oldTodo.getOwner());
        todo.setCollaborators(oldTodo.getCollaborators());

        logger.info("ToDoController.updateToDo(): Updating " + todo);
        toDoService.save(todo);
        return String.format("redirect:/todos/all/users/%d", ownerId);
    }

    /**
     * Delete a ToDo item
     * @param todoId
     * @param ownerId
     * @return redirect to all ToDos of the user
     */
    @PreAuthorize("hasAuthority('ADMIN') or " +
                "principal.id == @toDoServiceImpl.findToDoById(#todoId).owner.id or " +
                "@toDoServiceImpl.findToDoById(#todoId).collaborators.contains(@userServiceImpl.findUserById(principal.id))")
    @PostMapping("/{todo_id}/delete/users/{owner_id}")
    public String deleteToDo(@PathVariable("todo_id") Long todoId, @PathVariable("owner_id") Long ownerId) {
        logger.info("ToDoController.deleteToDo(): Deleting ToDo with toDoId=" + todoId);
        toDoService.deleteToDoById(todoId);
        return String.format("redirect:/todos/all/users/%d", ownerId);
    }

    /**
     * Display all ToDo items of a user
     * @param userId
     * @param model
     * @return todos-user.html
     */
    @PreAuthorize("hasAuthority('ADMIN') or #userId == authentication.principal.id")
    @GetMapping("/all/users/{user_id}")
    public String displayAllToDosOfUser(@PathVariable("user_id") Long userId, Model model) {
        List<ToDo> todos = toDoService.findAllToDoOfUserId(userId);
        List<ToDo> sortedTodos = todos.stream().sorted(Comparator.comparingLong(ToDo::getId)).toList();
        model.addAttribute("todos", sortedTodos);
        model.addAttribute("user", userService.findUserById(userId));
        logger.info("ToDoController.displayAllToDo(): Displaying all ToDos of user with userId=" + userId);
        return "todos-user";
    }

    /**
     * Display all ToDo items
     * @param userId
     * @param model
     * @return todos-all.html
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/all")
    public String displayAllToDos(@RequestParam("user_id") Long userId, Model model) {
        List<ToDo> todos = toDoService.findAllToDos();
        List<ToDo> sortedTodos = todos.stream().sorted(Comparator.comparingLong(ToDo::getId)).toList();
        model.addAttribute("todos", sortedTodos);
        model.addAttribute("user", userService.findUserById(userId));
        logger.info("ToDoController.displayAllToDo(): Displaying all ToDos");
        return "todos-all";
    }

    /**
     * Add a collaborator to a ToDo item
     * @param todoId
     * @param userId
     * @return redirect to the ToDo item
     */
    @PreAuthorize("hasAuthority('ADMIN') or authentication.principal.id == @toDoServiceImpl.findToDoById(#todoId).owner.id")
    @PostMapping("/{todo_id}/add")
    public String addCollaborator(@PathVariable("todo_id") Long todoId, @RequestParam("user_id") Long userId) {
        if (userId == -1) {
            logger.info("ToDoController.addCollaborator(): userId is invalid: " + userId);
            return String.format("redirect:/todos/%d/tasks", todoId);
        }

        ToDo todo = toDoService.findToDoById(todoId);
        logger.info("ToDoController.addCollaborator(): " +
                "Adding " + todo + " to" + userService.findUserById(userId));

        notificationService.sendNotificationToUserId(
                userId,
                "Removed from Project",
                String.format("You have been removed from a Project [%s]", todo.getTitle())
        );

        toDoService.addCollaborator(todoId, userId);
        logger.info("ToDoController.addCollaborator(): Updated ToDo=" + todo);
        return String.format("redirect:/todos/%d/tasks", todoId);
    }

    /**
     * Remove a collaborator from a ToDo item
     * @param todoId
     * @param userId
     * @return redirect to the ToDo item
     */
    @PreAuthorize("hasAuthority('ADMIN') or authentication.principal.id == @toDoServiceImpl.findToDoById(#todoId).owner.id")
    @PostMapping("/{todo_id}/remove")
    public String removeCollaborator(@PathVariable("todo_id") Long todoId, @RequestParam("user_id") Long userId) {
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
