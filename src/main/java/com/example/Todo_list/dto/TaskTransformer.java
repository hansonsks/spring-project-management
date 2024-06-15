package com.example.Todo_list.dto;

import com.example.Todo_list.entity.Priority;
import com.example.Todo_list.entity.State;
import com.example.Todo_list.entity.Task;
import com.example.Todo_list.entity.ToDo;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

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

        return dto;
    }
}
