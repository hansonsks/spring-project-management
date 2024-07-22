package com.example.Todo_list.service;

import com.example.Todo_list.entity.Comment;
import com.example.Todo_list.entity.User;

import java.util.List;

/**
 * Service class for managing Comment entities.
 */
public interface CommentService {

    /**
     * Find all comments in the database.
     *
     * @return List of all comments in the database.
     */
    List<User> findTaggedUserInComment(Comment comment);

    /**
     * Save a comment in the database.
     *
     * @param comment Comment to be saved in the database.
     * @return Comment saved in the database.
     */
    Comment save(Comment comment);

    /**
     * Find a comment by its ID.
     *
     * @param id ID of the comment to be found.
     * @return Comment with the given ID.
     */
    Comment findCommentById(Long id);

    /**
     * Find all comments by a user.
     *
     * @param user User whose comments are to be found.
     * @return List of comments by the given user.
     */
    List<Comment> findCommentByUser(User user);

    /**
     * Update a comment in the database.
     *
     * @param comment Comment to be updated in the database.
     * @return Comment updated in the database.
     */
    Comment updateComment(Comment comment);

    /**
     * Delete a comment by its ID.
     *
     * @param id ID of the comment to be deleted.
     */
    void deleteCommentById(Long id);

    /**
     * Delete a comment from the database.
     *
     * @param comment Comment to be deleted from the database.
     */
    void deleteComment(Comment comment);

    /**
     * Find all comments in the database.
     *
     * @return List of all comments in the database.
     */
    List<Comment> findAllComments();
}
