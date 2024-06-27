package com.example.Todo_list.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
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

    @NotBlank
    @Column(name = "provider", nullable = false)
    private String provider;

    @NotBlank
    @Column(name = "provider_user_id", nullable = false, unique = true)
    private String providerUserId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}

