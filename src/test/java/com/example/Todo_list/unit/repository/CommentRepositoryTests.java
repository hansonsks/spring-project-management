package com.example.Todo_list.unit.repository;

import com.example.Todo_list.entity.Comment;
import com.example.Todo_list.entity.User;
import com.example.Todo_list.repository.CommentRepository;
import com.example.Todo_list.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class CommentRepositoryTests {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Test")
    void testFindByUser() {
        User user = userRepository.findById(1L).get();
        List<Comment> actual = commentRepository.findByUser(user);

        assertNotNull(actual);
        assertFalse(actual.isEmpty());
        assertEquals(1, actual.size());
        assertEquals(user, actual.get(0).getUser());
    }

    @Test
    @DisplayName("Test")
    void testFindByUserNoComments() {
        commentRepository.deleteById(1L);

        List<Comment> actual = commentRepository.findByUser(userRepository.findById(1L).get());
        assertNotNull(actual);
        assertTrue(actual.isEmpty());
    }

    @Test
    @DisplayName("Test")
    void testFindByInvalidUser() {
        assertTrue(commentRepository.findByUser(null).isEmpty());
    }
}
