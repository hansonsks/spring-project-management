package com.example.Todo_list.repository;

import com.example.Todo_list.entity.OAuthUser;
import com.example.Todo_list.entity.User;
import com.example.Todo_list.repository.OAuthUserRepository;
import com.example.Todo_list.repository.UserRepository;
import com.example.Todo_list.security.oauth2.OAuth2Provider;
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
    @DisplayName("findByUser() finds an OAuthUser given a User")
    void testFindOAuthUserByUser() {
        User githubUser = userRepository.findByEmail("githubuser@mail.com").get();
        assertTrue(oAuthUserRepository.findOAuthUserByUser(githubUser).isPresent());

        OAuthUser oAuthGithubUser = oAuthUserRepository.findOAuthUserByUser(githubUser).get();
        assertEquals(OAuth2Provider.GITHUB, oAuthGithubUser.getProvider());
        assertEquals("github12345", oAuthGithubUser.getProviderUserId());
    }

    @Test
    @DisplayName("findByProviderAndProviderUserId() finds an OAuthUser given the Provider name and ID")
    void testFindOAuthUserByProviderAndProviderUserId() {
        assertTrue(oAuthUserRepository.findByProviderAndProviderUserId(OAuth2Provider.GITHUB, "github12345").isPresent());
    }

    @Test
    @DisplayName("deleteByUser() deletes an OAuthUser given a User")
    void testDeleteOAuthUserByUser() {
        User githubUser = userRepository.findByEmail("githubuser@mail.com").get();
        oAuthUserRepository.deleteOAuthUserByUser(githubUser);
        assertTrue(oAuthUserRepository.findOAuthUserByUser(githubUser).isEmpty());
    }
}
