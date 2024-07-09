package com.example.Todo_list.controller;

import com.example.Todo_list.entity.Task;
import com.example.Todo_list.entity.ToDo;
import com.example.Todo_list.entity.User;
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
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping("/todos")
@RequiredArgsConstructor
public class ToDoController {

    private final static Logger logger = LoggerFactory.getLogger(ToDoController.class);
    private final ToDoService toDoService;
    private final TaskService taskService;
    private final UserService userService;

    @PreAuthorize("hasAuthority('ADMIN') or #ownerId == authentication.principal.id")
    @GetMapping("/create/users/{owner_id}")
    public String showToDoCreationForm(@PathVariable("owner_id") Long id, Model model) {
        model.addAttribute("todo", new ToDo());
        model.addAttribute("ownerId", id);
        logger.info("ToDoController.showToDoCreationForm(): Displaying ToDo creation form");
        return "todo-create";
    }

    @PreAuthorize("hasAuthority('ADMIN') or #ownerId == authentication.principal.id")
    @PostMapping("/create/users/{owner_id}")
    public String createToDo(
            @PathVariable("owner_id") Long id,
            @Valid @ModelAttribute("todo") ToDo toDo,
            BindingResult result
    ) {
        logger.info("ToDoController.createToDo(): Attempting to create a ToDo item for user with userId=" + id);

        if (result.hasErrors()) {
            logger.info("ToDoController.createToDo(): Error found in data received, aborting ToDo creation");
            return "todo-create";
        }

        toDo.setCreatedAt(LocalDateTime.now());
        toDo.setOwner(userService.findUserById(id));

        logger.info("ToDoController.createToDo(): Saving " + toDo);
        toDoService.save(toDo);
        return String.format("redirect:/todos/all/users/%d", id);
    }

    @PreAuthorize("hasAuthority('ADMIN') or " +
            "principal.id == @toDoServiceImpl.findToDoById(#id).owner.id or " +
            "@toDoServiceImpl.findToDoById(#id).collaborators.contains(@userServiceImpl.findUserById(principal.id))")
    @GetMapping("/{id}/tasks")
    public String displayToDo(@PathVariable("id") Long id, Model model) {
        ToDo todo = toDoService.findToDoById(id);
        List<Task> tasks = taskService.findAllTaskOfToDo(id);
        List<User> users = userService.findAllUsers()
                                        .stream()
                                        .filter(user -> !Objects.equals(user.getId(), todo.getOwner().getId()))
                                        .toList();
        List<Task> sortedTasks = tasks.stream().sorted(Comparator.comparing(Task::getPriority).reversed()).toList();

        model.addAttribute("todo", todo);
        model.addAttribute("tasks", sortedTasks);
        model.addAttribute("users", users);

        logger.info("ToDoController.displayToDo(): Displaying ToDo with toDoId=" + id);

        return "todo-tasks";
    }

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

    @GetMapping("/{todo_id}/delete/users/{owner_id}")
    public String deleteToDo(@PathVariable("todo_id") Long todoId, @PathVariable("owner_id") Long ownerId) {
        logger.info("ToDoController.deleteToDo(): Deleting ToDo with toDoId=" + todoId);
        toDoService.deleteToDoById(todoId);
        return String.format("redirect:/todos/all/users/%d", ownerId);
    }

    @PreAuthorize("hasAuthority('ADMIN') or #userId == authentication.principal.id")
    @GetMapping("/all/users/{user_id}")
    public String displayAllToDo(@PathVariable("user_id") Long userId, Model model) {
        List<ToDo> todos = toDoService.findAllToDoOfUserId(userId);
        List<ToDo> sortedTodos = todos.stream().sorted(Comparator.comparingLong(ToDo::getId)).toList();
        model.addAttribute("todos", sortedTodos);
        model.addAttribute("user", userService.findUserById(userId));
        logger.info("ToDoController.displayAllToDo(): Displaying all ToDos of user with userId=" + userId);
        return "todos-user";
    }

    @PreAuthorize("hasAuthority('ADMIN') or authentication.principal.id == @toDoServiceImpl.findToDoById(#todoId).owner.id")
    @GetMapping("/{todoId}/add")
    public String addCollaborator(@PathVariable Long todoId, @RequestParam("user_id") Long userId) {
        if (userId == -1) {
            logger.info("ToDoController.addCollaborator(): userId is invalid: " + userId);
            return String.format("redirect:/todos/%d/tasks", todoId);
        }

        logger.info("ToDoController.addCollaborator(): " +
                    "Adding " + toDoService.findToDoById(todoId) + " to" + userService.findUserById(userId));
        toDoService.addCollaborator(todoId, userId);
        return String.format("redirect:/todos/%d/tasks", todoId);
    }

    @PreAuthorize("hasAuthority('ADMIN') or authentication.principal.id == @toDoServiceImpl.findToDoById(#todoId).owner.id")
    @GetMapping("/{todoId}/remove")
    public String removeCollaborator(@PathVariable Long todoId, @RequestParam("user_id") Long userId) {
        logger.info("ToDoController.removeCollaborator(): " +
                    "Removing " + toDoService.findToDoById(todoId) + " from " + userService.findUserById(userId));
        toDoService.removeCollaborator(todoId, userId);
        return String.format("redirect:/todos/%d/tasks", todoId);
    }

}
