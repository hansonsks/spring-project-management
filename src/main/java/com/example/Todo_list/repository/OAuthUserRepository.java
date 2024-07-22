package com.example.Todo_list.repository;

import com.example.Todo_list.entity.OAuthUser;
import com.example.Todo_list.entity.User;
import com.example.Todo_list.security.oauth2.OAuth2Provider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for OAuthUser entity
 */
@Repository
public interface OAuthUserRepository extends JpaRepository<OAuthUser, Long> {

    /**
     * Find OAuthUser by user
     *
     * @param user user
     * @return OAuthUser
     */
    Optional<OAuthUser> findOAuthUserByUser(User user);

    /**
     * Delete OAuthUser by user
     * @param user user
     */
    void deleteOAuthUserByUser(User user);

    /**
     * Find OAuthUser by provider and provider user id
     *
     * @param provider provider
     * @param providerUserId provider user id
     * @return OAuthUser
     */
    Optional<OAuthUser> findByProviderAndProviderUserId(OAuth2Provider provider, String providerUserId);
}
