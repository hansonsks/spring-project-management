package com.example.Todo_list.security;

import com.example.Todo_list.entity.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Getter
public class CustomOAuth2UserDetails extends User implements OAuth2User {

    private final String provider;
    private final List<GrantedAuthority> authorities;
    private final Map<String, Object> attributes;

    public CustomOAuth2UserDetails(User user, OAuth2User oAuth2User, String provider) {
        this.setId(user.getId());
        this.setFirstName(user.getFirstName());
        this.setLastName(user.getLastName());
        this.setEmail(user.getEmail());
        this.setPassword(user.getPassword());
        this.provider = provider;
        this.authorities = List.of(new SimpleGrantedAuthority(user.getRole().getName()));
        this.attributes = oAuth2User.getAttributes();
    }

    @Override
    public Map<String, Object> getAttributes() {
        return this.attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getName() {
        return "login";
    }

    public boolean isGitHubConnected() {
        return this.getProvider().equals("github");
    }

}
