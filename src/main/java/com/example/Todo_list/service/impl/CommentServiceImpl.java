package com.example.Todo_list.service.impl;

import com.example.Todo_list.entity.Comment;
import com.example.Todo_list.entity.User;
import com.example.Todo_list.exception.NullEntityException;
import com.example.Todo_list.repository.CommentRepository;
import com.example.Todo_list.repository.UserRepository;
import com.example.Todo_list.service.CommentService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Service class for managing comments.
 */
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private static final Logger logger = LoggerFactory.getLogger(CommentServiceImpl.class);
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    /**
     * Finds tagged users in a comment.
     *
     * @param comment the comment to search for tagged users
     * @return a list of tagged users in the comment
     */
    @Override
    public List<User> findTaggedUserInComment(Comment comment) {
        if (comment == null) {
            logger.error("CommentService.findTaggedUserInComment(): Comment cannot be null");
            throw new NullEntityException(this.getClass().getName(), "Comment cannot be null");
        }

        // Check if comment contains the tagging symbol '@'
        if (!comment.getContent().contains("@")) {
            logger.info("CommentService.findTaggedUserInComment(): No tagged users found in " + comment);
            return Collections.emptyList();
        }

        // Find tagged users in the comment
        logger.info("CommentService.findTaggedUserInComment(): Finding tagged users in " + comment + " ...");
        List<User> taggedUsers = new ArrayList<>();
        for (User user : userRepository.findAll()) {
            if (comment.getContent().contains("@" + user.getFirstName())) {
                logger.info("CommentService.findTaggedUserInComment(): Found tagged user called " + user.getFirstName());
                taggedUsers.add(user);
            }
        }

        return taggedUsers;
    }

    /**
     * Saves a comment.
     *
     * @param comment the comment to save
     * @return the saved comment
     */
    @Override
    public Comment save(Comment comment) {
        if (comment == null) {
            logger.error("CommentService.save(): Comment cannot be null");
            throw new NullEntityException(this.getClass().getName(), "Comment cannot be null");
        }

        logger.info("CommentService.save(): Saving " + comment + " ...");
        return commentRepository.save(comment);
    }

    /**
     * Finds a comment by its ID.
     *
     * @param id the ID of the comment to find
     * @return the found comment
     */
    @Override
    public Comment findCommentById(Long id) {
        Optional<Comment> comment = commentRepository.findById(id);

        if (comment.isPresent()) {
            logger.info("CommentService.findCommentById(): Found " + comment.get());
            return comment.get();
        } else {
            logger.error("CommentService.findCommentById(): No Comment with id=" + id + " was found");
            throw new EntityNotFoundException("No Comment with id=" + id + " was found");
        }
    }

    /**
     * Finds comments by a user.
     *
     * @param user the user to search for comments
     * @return a list of comments by the user
     */
    @Override
    public List<Comment> findCommentByUser(User user) {
        if (user == null) {
            logger.error("CommentService.findCommentByUser(): User cannot be null");
            throw new NullEntityException(this.getClass().getName(), "User cannot be null");
        }

        logger.info("CommentService.findCommentByUser(): Finding comments by " + user + " ...");
        return commentRepository.findByUser(user);
    }

    /**
     * Updates a comment.
     *
     * @param comment the comment to update
     * @return the updated comment
     */
    @Override
    public Comment updateComment(Comment comment) {
        if (comment == null) {
            logger.error("CommentService.updateComment(): Comment cannot be null");
            throw new NullEntityException(this.getClass().getName(), "Comment cannot be null");
        }

        logger.info("CommentService.updateComment(): Updating " + comment + " ...");
        return commentRepository.save(comment);
    }

    /**
     * Deletes a comment by its ID.
     *
     * @param id the ID of the comment to delete
     */
    @Override
    public void deleteCommentById(Long id) {
        Comment comment = this.findCommentById(id);
        logger.info("CommentService.deleteCommentById(): Deleting comment with id=" + id + " ...");
        commentRepository.delete(comment);
    }

    /**
     * Deletes a comment.
     *
     * @param comment the comment to delete
     */
    @Override
    public void deleteComment(Comment comment) {
        if (comment == null) {
            logger.error("CommentService.deleteComment(): Comment cannot be null");
            throw new NullEntityException(this.getClass().getName(), "Comment cannot be null");
        }

        logger.info("CommentService.deleteComment(): Deleting " + comment + " ...");
        commentRepository.delete(comment);
    }

    /**
     * Finds all comments.
     *
     * @return a list of all comments
     */
    @Override
    public List<Comment> findAllComments() {
        logger.info("CommentService.findAllComments(): Finding all comments ...");
        return commentRepository.findAll();
    }
}
