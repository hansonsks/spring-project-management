package com.example.Todo_list.unit.repository;

import com.example.Todo_list.entity.OAuthUser;
import com.example.Todo_list.entity.User;
import com.example.Todo_list.repository.OAuthUserRepository;
import com.example.Todo_list.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class OAuthUserRepositoryTests {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OAuthUserRepository oAuthUserRepository;

    @Test
    @DisplayName("OAuthUserRepository.findByUser() finds an OAuthUser given a User")
    void testFindOAuthUserByUser() {
        User githubUser = userRepository.findByEmail("githubuser@mail.com").get();
        assertTrue(oAuthUserRepository.findByUser(githubUser).isPresent());

        OAuthUser oAuthGithubUser = oAuthUserRepository.findByUser(githubUser).get();
        assertEquals("github", oAuthGithubUser.getProvider());
        assertEquals("github12345", oAuthGithubUser.getProviderUserId());
    }

    @Test
    @DisplayName("OAuthUserRepository.findByProviderAndProviderUserId() finds an OAuthUser given the Provider name and ID")
    void testFindOAuthUserByProviderAndProviderUserId() {
        assertTrue(oAuthUserRepository.findByProviderAndProviderUserId("github", "github12345").isPresent());
    }

    @Test
    @DisplayName("OAuthUserRepository.deleteByUser() deletes an OAuthUser given a User")
    void testDeleteOAuthUserByUser() {
        User githubUser = userRepository.findByEmail("githubuser@mail.com").get();
        oAuthUserRepository.deleteByUser(githubUser);
        assertTrue(oAuthUserRepository.findByUser(githubUser).isEmpty());
    }
}
