package com.example.Todo_list.dto;

import com.example.Todo_list.entity.Priority;
import com.example.Todo_list.entity.State;
import com.example.Todo_list.entity.Task;
import com.example.Todo_list.entity.ToDo;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TaskTransformer {

    public static Task convertDTOToEntity(TaskDTO dto, ToDo todo, State state) {
        Task task = new Task();
        task.setId(dto.getId());
        task.setName(dto.getName());
        task.setDescription(dto.getDescription());
        task.setPriority(Priority.valueOf(dto.getPriority()));
        task.setTodo(todo);
        task.setState(state);
        task.setAssignedUsers(dto.getAssignedUsers());

        // Convert LocalDateTime to ZonedDateTime
        if (dto.getDeadline() != null) {
            // TODO: Ideally, you should use the time zone of the user rather than where the system is based
            task.setDeadline(dto.getDeadline().atZone(ZoneId.systemDefault()));
        }

        return task;
    }

    public static TaskDTO convertEntityToDTO(Task task) {
        TaskDTO dto = new TaskDTO();
        dto.setId(task.getId());
        dto.setName(task.getName());
        dto.setDescription(task.getDescription());
        dto.setPriority(task.getPriority().toString());
        dto.setToDoId(task.getTodo().getId());
        dto.setState(task.getState().toString());
        dto.setAssignedUsers(task.getAssignedUsers());

        // Convert ZonedDateTime to LocalDateTime
        if (task.getDeadline() != null) {
            dto.setDeadline(task.getDeadline().toLocalDateTime());
        }

        return dto;
    }
}
