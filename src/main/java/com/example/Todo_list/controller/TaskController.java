package com.example.Todo_list.controller;

import com.example.Todo_list.dto.TaskDTO;
import com.example.Todo_list.dto.TaskTransformer;
import com.example.Todo_list.entity.Comment;
import com.example.Todo_list.entity.Priority;
import com.example.Todo_list.entity.Task;
import com.example.Todo_list.service.CommentService;
import com.example.Todo_list.service.StateService;
import com.example.Todo_list.service.TaskService;
import com.example.Todo_list.service.ToDoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;

// TODO: Change some mappings to be POST instead of GET!
@Controller
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final static Logger logger = LoggerFactory.getLogger(TaskController.class);
    private final TaskService taskService;
    private final ToDoService toDoService;
    private final StateService stateService;
    private final CommentService commentService;

    @PreAuthorize("hasAuthority('ADMIN') or " +
                "principal.id == @toDoServiceImpl.findToDoById(#todoId).owner.id or " +
                "@toDoServiceImpl.findToDoById(#todoId).collaborators.contains(@userServiceImpl.findUserById(principal.id))")
    @GetMapping("/create/todos/{todo_id}")
    public String showTaskCreationForm(@PathVariable("todo_id") Long id, Model model) {
        model.addAttribute("task", new TaskDTO());
        model.addAttribute("todo", toDoService.findToDoById(id));
        model.addAttribute("priorities", Priority.values());
        logger.info("TaskController.showTaskCreationForm(): Displaying task creation form");
        return "task-create";
    }

    @PreAuthorize("hasAuthority('ADMIN') or " +
                "principal.id == @toDoServiceImpl.findToDoById(#todoId).owner.id or " +
                "@toDoServiceImpl.findToDoById(#todoId).collaborators.contains(@userServiceImpl.findUserById(principal.id))")
    @PostMapping("/create/todos/{todo_id}")
    public String createTask(
            @PathVariable("todo_id") Long id,
            Model model,
            @Valid @ModelAttribute("task") TaskDTO taskDTO,
            BindingResult result
    ) {
        logger.info("TaskController.createTask(): Attempting to create task for todoId=" + id);

        if (result.hasErrors()) {
            logger.error("TaskController.createTask(): Error found in data received, aborting task creation");
            model.addAttribute("todo", toDoService.findToDoById(id));
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
        return String.format("redirect:/todos/%d/tasks", id);
    }

    @PreAuthorize("hasAuthority('ADMIN') or " +
                "principal.id == @toDoServiceImpl.findToDoById(#todoId).owner.id or " +
                "@toDoServiceImpl.findToDoById(#todoId).collaborators.contains(@userServiceImpl.findUserById(principal.id))")
    @GetMapping("/{task_id}/update")
    public String showTaskUpdateForm(@PathVariable("task_id") Long id, Model model) {
        TaskDTO taskDTO = TaskTransformer.convertEntityToDTO(taskService.findTaskById(id));
        model.addAttribute("task", taskDTO);
        model.addAttribute("toDoId", taskDTO.getToDoId());
        model.addAttribute("priorities", Priority.values());
        model.addAttribute("states", stateService.findAllStates());
        logger.info("TaskController.showTaskUpdateForm(): Displaying task update form");
        return "task-update";
    }

    @PreAuthorize("hasAuthority('ADMIN') or " +
                "principal.id == @toDoServiceImpl.findToDoById(#todoId).owner.id or " +
                "@toDoServiceImpl.findToDoById(#todoId).collaborators.contains(@userServiceImpl.findUserById(principal.id))")
    @PostMapping("/{task_id}/update")
    public String updateTask(
            @PathVariable("task_id") Long id,
            Model model,
            @Valid @ModelAttribute TaskDTO taskDTO,
            BindingResult result
    ) {
        logger.info("TaskController.updateTask(): Attempting to update task with taskId=" + id);

        if (result.hasErrors()) {
            logger.error("TaskController.updateTask(): Error found in data received, aborting task update");

            TaskDTO oldTaskDTO = TaskTransformer.convertEntityToDTO(taskService.findTaskById(id));
            model.addAttribute("task", oldTaskDTO);
            model.addAttribute("toDoId", oldTaskDTO.getToDoId());
            model.addAttribute("priorities", Priority.values());
            model.addAttribute("states", stateService.findAllStates());

            return "task-update";
        }

        taskDTO.setId(id);
        Task task = TaskTransformer.convertDTOToEntity(
                taskDTO,
                toDoService.findToDoById(taskDTO.getToDoId()),
                stateService.findStateByName(taskDTO.getState())
        );

        logger.info("TaskController.updateTask(): Updating " + task);
        taskService.save(task);
        return String.format("redirect:/todos/%d/tasks", task.getTodo().getId());
    }

    @PreAuthorize("hasAuthority('ADMIN') or principal.id == @taskServiceImpl.findTaskById(#id).todo.owner.id")
    @GetMapping("/{task_id}/read")
    public String displayTask(@PathVariable("task_id") Long id, Model model) {
        Task task = taskService.findTaskById(id);
        List<Comment> comments = task.getComments().stream()
                                                    .sorted(Comparator.comparing(Comment::getCreatedAt).reversed())
                                                    .toList();
        TaskDTO taskDTO = TaskTransformer.convertEntityToDTO(task);
        model.addAttribute("task", taskDTO);
        model.addAttribute("comments", comments);
        logger.info("TaskController.displayTask(): Displaying " + task);
        return "task-info";
    }

    @PreAuthorize("hasAuthority('ADMIN') or " +
                "principal.id == @toDoServiceImpl.findToDoById(#todoId).owner.id or " +
                "@toDoServiceImpl.findToDoById(#todoId).collaborators.contains(@userServiceImpl.findUserById(principal.id))")
    @GetMapping("/{task_id}/delete/todos/{todo_id}")
    public String deleteTask(@PathVariable("task_id") Long taskId, @PathVariable("todo_id") Long todoId) {
        logger.info("TaskController.deleteTask(): Deleting task with taskId=" + taskId + " of todo with todoId=" + todoId);
        taskService.deleteTaskById(taskId);
        return String.format("redirect:/todos/%d/tasks", todoId);
    }

    @PreAuthorize("hasAuthority('ADMIN') or " +
                "principal.id == @toDoServiceImpl.findToDoById(#todoId).owner.id or " +
                "@toDoServiceImpl.findToDoById(#todoId).collaborators.contains(@userServiceImpl.findUserById(principal.id))")
    @PostMapping("/{task_id}/comments/create")
    public String createComment(@PathVariable("task_id") Long taskId, @RequestParam("comment") String content) {
        if (content == null || content.isEmpty() || content.length() > 255) {
            logger.error("TaskController.createComment(): Comment content is empty or too long, aborting update");
            return String.format("redirect:/tasks/%d/read?invalidComment=true", taskId);
        }

        Task task = taskService.findTaskById(taskId);
        logger.info("TaskController.createComment(): Creating comment for task with taskId=" + taskId);

        Comment comment = new Comment();
        comment.setContent(content);
        comment.setUser(task.getTodo().getOwner());
        comment.setTask(task);

        commentService.updateComment(comment);
        logger.info("TaskController.createComment(): Saved " + comment + " for task with taskId=" + taskId);

        return String.format("redirect:/tasks/%d/read", taskId);
    }

    @PreAuthorize("hasAuthority('ADMIN') or " +
                "principal.id == @toDoServiceImpl.findToDoById(#todoId).owner.id or " +
                "@toDoServiceImpl.findToDoById(#todoId).collaborators.contains(@userServiceImpl.findUserById(principal.id))")
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

        return String.format("redirect:/tasks/%d/read", taskId);
    }

    @PreAuthorize("hasAuthority('ADMIN') or " +
                "principal.id == @toDoServiceImpl.findToDoById(#todoId).owner.id or " +
                "@toDoServiceImpl.findToDoById(#todoId).collaborators.contains(@userServiceImpl.findUserById(principal.id))")
    @PostMapping("/{task_id}/comments/{comment_id}/delete")
    public String deleteComment(@PathVariable("task_id") Long taskId, @PathVariable("comment_id") Long commentId) {
        logger.info("TaskController.deleteComment(): " +
                    "Deleting comment with commentId=" + commentId + " of task with taskId=" + taskId);
        taskService.findTaskById(taskId); // Check if task exists (will throw exception if not
        commentService.deleteCommentById(commentId);
        return String.format("redirect:/tasks/%d/read", taskId);
    }
}
