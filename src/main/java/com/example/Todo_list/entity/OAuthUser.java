package com.example.Todo_list.entity;

import com.example.Todo_list.security.OAuth2Provider;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Data
@ToString
@EqualsAndHashCode(of = "id")
@Table(name = "oauth_users")
public class OAuthUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "provider", nullable = false)
    private OAuth2Provider provider;

    @NotBlank
    @Column(name = "provider_user_id", nullable = false, unique = true) // TODO: Should this be unique?
    private String providerUserId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}

