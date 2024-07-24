package com.example.Todo_list.service;

import com.example.Todo_list.entity.Comment;
import com.example.Todo_list.entity.User;
import com.example.Todo_list.exception.NullEntityException;
import com.example.Todo_list.repository.CommentRepository;
import com.example.Todo_list.service.impl.CommentServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class CommentServiceTests {

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private CommentServiceImpl commentService;

    private Comment comment;

    @BeforeEach
    public void beforeEach() {
        comment = new Comment();
        comment.setId(1L);
        comment.setContent("Expected Comment");
    }

    @Test
    @DisplayName("save() should save a comment and return it")
    void testSaveComment() {
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        Comment actual = commentService.save(comment);

        verify(commentRepository).save(any(Comment.class));
        assertEquals(comment, actual);
    }

    @Test
    @DisplayName("save() should throw NullEntityException when saving a null comment")
    void testSaveInvalidComment() {
        assertThrows(NullEntityException.class, () -> commentService.save(null));
    }

    @Test
    @DisplayName("findCommentById() should return a comment when given a valid id")
    void testFindCommentById() {
        when(commentRepository.findById(any(long.class))).thenReturn(Optional.of(comment));

        Comment actual = commentService.findCommentById(1L);

        verify(commentRepository).findById(1L);
        assertEquals(comment, actual);
    }

    @Test
    @DisplayName("findCommentById() should throw EntityNotFoundException when given an invalid id")
    void testFindCommentByInvalidId() {
        when(commentRepository.findById(any(long.class))).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> commentService.findCommentById(999L));
    }

    @Test
    @DisplayName("findCommentByUser() should return a list of comments by a user")
    void testFindCommentByUser() {
        when(commentRepository.findByUser(any(User.class))).thenReturn(List.of(comment));

        List<Comment> actual = commentService.findCommentByUser(new User());

        assertNotNull(actual);
        assertFalse(actual.isEmpty());
        assertEquals(1, actual.size());
        assertEquals(comment, actual.get(0));
    }

    @Test
    @DisplayName("findCommentByUser() should throw NullEntityException when given an invalid user")
    void testFindCommentByInvalidUser() {
        assertThrows(NullEntityException.class, () -> commentService.findCommentByUser(null));
        verify(commentRepository, times(0)).findByUser(any(User.class));
    }

    @Test
    @DisplayName("findAllComments() should return a list of all comments")
    void testFindAllComments() {
        when(commentRepository.findAll()).thenReturn(List.of(comment));

        List<Comment> actual = commentService.findAllComments();

        assertNotNull(actual);
        assertFalse(actual.isEmpty());
        assertEquals(1, actual.size());
        assertEquals(comment, actual.get(0));
    }

    @Test
    @DisplayName("updateComment() should update a comment and return it")
    void testUpdateComment() {
        comment.setIsEdited(true);
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        Comment actual = commentService.updateComment(comment);

        verify(commentRepository).save(any(Comment.class));
        assertEquals(comment, actual);
    }

    @Test
    @DisplayName("updateComment() should throw NullEntityException when updating a null comment")
    void testUpdateInvalidComment() {
        assertThrows(NullEntityException.class, () -> commentService.updateComment(null));
        verify(commentRepository, times(0)).save(any(Comment.class));
    }

    @Test
    @DisplayName("deleteCommentById() should delete a comment by its id")
    void testDeleteCommentById() {
        when(commentRepository.findById(any(long.class))).thenReturn(Optional.of(comment));

        commentService.deleteCommentById(1L);

        verify(commentRepository).delete(any(Comment.class));
    }

    @Test
    @DisplayName("deleteCommentById() should throw EntityNotFoundException when given an invalid id")
    void testDeleteCommentByInvalidId() {
        assertThrows(EntityNotFoundException.class, () -> commentService.deleteCommentById(999L));
        verify(commentRepository, times(0)).delete(any(Comment.class));
    }

    @Test
    @DisplayName("deleteComment() should delete a comment")
    void testDeleteComment() {
        commentService.deleteComment(comment);
        verify(commentRepository).delete(any(Comment.class));
    }

    @Test
    @DisplayName("deleteComment() should throw NullEntityException when deleting a null comment")
    void testDeleteInvalidComment() {
        assertThrows(NullEntityException.class, () -> commentService.deleteComment(null));
        verify(commentRepository, times(0)).delete(any(Comment.class));
    }
}
