package com.example.Todo_list.repository;

import com.example.Todo_list.entity.ToDo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ToDoRepository extends JpaRepository<ToDo, Long> {

    @Query(value =
            "SELECT id, title, created_at, description, owner_id FROM todos " +
            "INNER JOIN " +
            "todos_collaborators ON id = todo_id AND collaborator_id = ?1" +
            "UNION " +
            "SELECT id, title, created_at, description, owner_id FROM todos WHERE owner_id = ?1",
            nativeQuery = true
    )
    List<ToDo> findTodoByOwnerId(Long ownerId);
}
