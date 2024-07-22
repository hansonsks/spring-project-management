package com.example.Todo_list.repository;

import com.example.Todo_list.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for {@link Task} class.
 */
@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    /**
     * Finds all tasks by todo id.
     *
     * @param todoId the todo id
     * @return the list of tasks
     */
    List<Task> findByTodoId(Long todoId);

    /**
     * Finds all tasks by user id.
     *
     * @param userId the user id
     * @return the list of tasks
     */
    @Query(value =
            "SELECT id, name, description, priority, state_id, todo_id, assigned_user_id, deadline FROM tasks WHERE assigned_user_id = ?1 " +
            "UNION " +
            "SELECT id, name, description, priority, state_id, todo_id, assigned_user_id, deadline FROM tasks " +
            "INNER JOIN tasks_collaborators ON id = task_id AND collaborator_id = ?1", nativeQuery = true
    )
    List<Task> findAssignedTasksByUserId(Long userId);
}
