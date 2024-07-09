package com.example.Todo_list.unit.service;

import com.example.Todo_list.entity.OAuthUser;
import com.example.Todo_list.entity.Role;
import com.example.Todo_list.entity.User;
import com.example.Todo_list.exception.NullEntityException;
import com.example.Todo_list.repository.OAuthUserRepository;
import com.example.Todo_list.repository.UserRepository;
import com.example.Todo_list.service.impl.UserServiceImpl;
import com.example.Todo_list.utils.PasswordService;
import com.example.Todo_list.utils.SampleTodoInitializer;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private OAuthUserRepository oAuthUserRepository;

    @Mock
    private PasswordService passwordService;

    @Mock
    private SampleTodoInitializer todoInitializer;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private User userWithOAuth;
    private OAuthUser oAuthUser;
    private Role role;

    @BeforeEach
    public void beforeEach() {
        role = new Role();
        role.setId(1L);
        role.setName("Admin");

        user = new User();
        user.setId(1L);
        user.setFirstName("Test");
        user.setLastName("Test");
        user.setEmail("test@mail.com");
        user.setPassword("test123");
        user.setRole(role);

        userWithOAuth = new User();
        userWithOAuth.setId(2L);
        userWithOAuth.setFirstName("OAuth");
        userWithOAuth.setLastName("OAuth");
        userWithOAuth.setEmail("oauth@mail.com");
        userWithOAuth.setPassword("oauth123");
        userWithOAuth.setRole(role);

        oAuthUser = new OAuthUser();
        oAuthUser.setId(1L);
        oAuthUser.setProvider("OAuth Provider");
        oAuthUser.setProviderUserId("OAuth User Id 123");
        oAuthUser.setUser(userWithOAuth);
    }

    @Test
    @DisplayName("save() saves a new User")
    void testSaveUser() {
        when(userRepository.save(any(User.class))).thenReturn(user);

        User actualUser = userService.save(user);
        assertEquals(user, actualUser);
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("save() throws a NullEntityException when trying to save a null User")
    void testSaveNullUser() {
        assertThrows(NullEntityException.class, () -> userService.save(null));
    }

    @Test
    @DisplayName("findUserById() returns an existing User given an existing ID")
    void testFindUserById() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        User actualUser = userService.findUserById(user.getId());
        assertEquals(user, actualUser);
        verify(userRepository).findById(any(long.class));
    }

    @Test
    @DisplayName("findUserById() throws EntityNotFoundException if a User with the given ID was not found")
    void testFindUserByInvalidId() {
        when(userRepository.findById(any(long.class))).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.findUserById(999L));
        verify(userRepository).findById(any(long.class));
    }

    @Test
    @DisplayName("findUserByEmail() returns an existing given an existing email")
    void testFindUserByEmail() {
        when(userRepository.findByEmail(any(String.class))).thenReturn(Optional.of(user));

        User actualUser = userService.findUserByEmail(user.getEmail());
        assertEquals(user, actualUser);
        verify(userRepository).findByEmail(any(String.class));
    }

    @Test
    @DisplayName("findUserByEmail() returns an existing User given an existing email even if they use OAuth2 to login")
    void testFindOAuthUserByEmail() {
        when(userRepository.findByEmail(any(String.class))).thenReturn(Optional.of(userWithOAuth));

        User actualUser = userService.findUserByEmail(userWithOAuth.getEmail());
        assertEquals(userWithOAuth, actualUser);
        verify(userRepository).findByEmail(any(String.class));
    }

    @Test
    @DisplayName("findUserByEmail() throws EntityNotFoundException if a User with the given email was not found")
    void testFindUserByInvalidEmail() {
        when(userRepository.findByEmail(any(String.class))).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.findUserByEmail("Invalid email"));
        verify(userRepository).findByEmail(any(String.class));
    }

    @Test
    @DisplayName("updateUser() updates an existing User")
    void testUpdateUser() {
        when(userRepository.findById(any(long.class))).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        assertEquals(user, userService.updateUser(user));
        verify(userRepository).save(any(User.class));
        verify(userRepository).findById(any(long.class));
    }

    @Test
    @DisplayName("updateUser() updates an existing User even if they use OAuth2 to login")
    void testUpdateOAuthUser() {
        when(userRepository.findById(any(long.class))).thenReturn(Optional.of(userWithOAuth));
        when(userRepository.save(any(User.class))).thenReturn(userWithOAuth);

        assertEquals(userWithOAuth, userService.updateUser(userWithOAuth));
        verify(userRepository).save(any(User.class));
        verify(userRepository).findById(any(long.class));
    }

    @Test
    @DisplayName("updateUser() throws EntityNotFoundException if the User given to update did not exist before")
    void testUpdateInvalidUser() {
        assertThrows(EntityNotFoundException.class, () -> userService.updateUser(user));
        verify(userRepository, times(0)).save(any(User.class));
    }

    @Test
    @DisplayName("updateUser() throws NullEntityException if the User is null")
    void testUpdateNullUser() {
        assertThrows(NullEntityException.class, () -> userService.updateUser(null));
        verify(userRepository, times(0)).save(any(User.class));
    }

    @Test
    @DisplayName("deleteUserById() deletes a User given the user's ID")
    void testDeleteUser() {
        when(userRepository.findById(any(long.class))).thenReturn(Optional.of(user));

        userService.deleteUserById(user.getId());
        verify(userRepository).findById(any(long.class));
        verify(userRepository, times(1)).delete(any(User.class));
    }

    @Test
    @DisplayName("deleteUserById() deletes a User even if they used OAuth2 to login")
    void testDeleteOAuthUser() {
        when(userRepository.findById(any(long.class))).thenReturn(Optional.of(userWithOAuth));

        userService.deleteUserById(userWithOAuth.getId());
        verify(userRepository).findById(any(long.class));
        verify(userRepository, times(1)).delete(any(User.class));
        verify(oAuthUserRepository, times(1)).deleteByUser(any(User.class));
    }

    @Test
    @DisplayName("deleteUserById() throws EntityNotFoundException if a User with the given ID was not found")
    void testDeleteInvalidUser() {
        assertThrows(EntityNotFoundException.class, () -> userService.deleteUserById(user.getId()));
        verify(userRepository, times(0)).delete(any(User.class));
    }

    @Test
    @DisplayName("findAllUser() returns the list of all User when it is empty")
    void testFindAllUsersEmpty() {
        List<User> expected = new ArrayList<>();
        when(userRepository.findAll()).thenReturn(expected);

        List<User> actual = userService.findAllUser();
        assertEquals(actual, expected);

        verify(userRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("findAllUser() returns the list of all User when it is non-empty")
    void testFindAllUsersNonEmpty() {
        List<User> expected = new ArrayList<>(List.of(user, userWithOAuth));
        when(userRepository.findAll()).thenReturn(expected);

        List<User> actual = userService.findAllUser();
        assertEquals(actual, expected);

        verify(userRepository, times(1)).findAll();
    }
}
