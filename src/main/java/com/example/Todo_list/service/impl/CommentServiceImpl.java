package com.example.Todo_list.service.impl;

import com.example.Todo_list.entity.Comment;
import com.example.Todo_list.entity.User;
import com.example.Todo_list.exception.NullEntityException;
import com.example.Todo_list.repository.CommentRepository;
import com.example.Todo_list.service.CommentService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private static final Logger logger = LoggerFactory.getLogger(CommentServiceImpl.class);
    private final CommentRepository commentRepository;

    @Override
    public Comment save(Comment comment) {
        if (comment == null) {
            logger.error("CommentService.save(): Comment cannot be null");
            throw new NullEntityException(this.getClass().getName(), "Comment cannot be null");
        }

        logger.info("CommentService.save(): Saving " + comment + " ...");
        return commentRepository.save(comment);
    }

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

    @Override
    public List<Comment> findCommentByUser(User user) {
        if (user == null) {
            logger.error("CommentService.findCommentByUser(): User cannot be null");
            throw new NullEntityException(this.getClass().getName(), "User cannot be null");
        }

        logger.info("CommentService.findCommentByUser(): Finding comments by " + user + " ...");
        return commentRepository.findByUser(user);
    }

    @Override
    public Comment updateComment(Comment comment) {
        if (comment == null) {
            logger.error("CommentService.updateComment(): Comment cannot be null");
            throw new NullEntityException(this.getClass().getName(), "Comment cannot be null");
        }

        logger.info("CommentService.updateComment(): Updating " + comment + " ...");
        return commentRepository.save(comment);
    }

    @Override
    public void deleteCommentById(Long id) {
        Comment comment = this.findCommentById(id);
        logger.info("CommentService.deleteCommentById(): Deleting comment with id=" + id + " ...");
        commentRepository.delete(comment);
    }

    @Override
    public void deleteComment(Comment comment) {
        if (comment == null) {
            logger.error("CommentService.deleteComment(): Comment cannot be null");
            throw new NullEntityException(this.getClass().getName(), "Comment cannot be null");
        }

        logger.info("CommentService.deleteComment(): Deleting " + comment + " ...");
        commentRepository.delete(comment);
    }

    @Override
    public List<Comment> findAllComments() {
        logger.info("CommentService.findAllComments(): Finding all comments ...");
        return commentRepository.findAll();
    }
}
