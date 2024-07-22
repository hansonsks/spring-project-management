package com.example.Todo_list.service;

import com.example.Todo_list.entity.Comment;
import com.example.Todo_list.entity.User;

import java.util.List;

public interface CommentService {

    List<User> findTaggedUserInComment(Comment comment);

    Comment save(Comment comment);

    Comment findCommentById(Long id);

    List<Comment> findCommentByUser(User user);

    Comment updateComment(Comment comment);

    void deleteCommentById(Long id);

    void deleteComment(Comment comment);

    List<Comment> findAllComments();
}
