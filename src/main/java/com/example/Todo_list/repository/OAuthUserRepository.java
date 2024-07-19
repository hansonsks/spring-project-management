package com.example.Todo_list.repository;

import com.example.Todo_list.entity.OAuthUser;
import com.example.Todo_list.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OAuthUserRepository extends JpaRepository<OAuthUser, Long> {

    Optional<OAuthUser> findOAuthUserByUser(User user);

    void deleteOAuthUserByUser(User user);

    Optional<OAuthUser> findByProviderAndProviderUserId(String provider, String providerUserId);
}
