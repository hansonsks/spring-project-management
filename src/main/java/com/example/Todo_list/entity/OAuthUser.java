package com.example.Todo_list.entity;

import com.example.Todo_list.security.oauth2.OAuth2Provider;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Represents a user that has authenticated using OAuth2.
 */
@Entity
@Data
@ToString
@EqualsAndHashCode(of = "id")
@Table(name = "oauth_users")
public class OAuthUser {

    /**
     * The unique identifier of the OAuth user.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The OAuth2 provider that the user authenticated with.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "provider", nullable = false)
    private OAuth2Provider provider;

    /**
     * The unique identifier of the user from the OAuth2 provider.
     */
    @NotBlank
    @Column(name = "provider_user_id", nullable = false, unique = true) // TODO: Should this be unique?
    private String providerUserId;

    /**
     * The user that authenticated using OAuth2.
     */
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}

