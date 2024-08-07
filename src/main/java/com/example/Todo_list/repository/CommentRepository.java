package com.example.Todo_list.repository;

import com.example.Todo_list.entity.Comment;
import com.example.Todo_list.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for the {@link Comment} entity.
 */
@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    /**
     * Find all comments by user.
     * @param user
     * @return List of comments
     */
    List<Comment> findByUser(User user);
}
