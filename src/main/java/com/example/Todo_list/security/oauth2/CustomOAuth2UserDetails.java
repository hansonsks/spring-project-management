package com.example.Todo_list.security.oauth2;

import com.example.Todo_list.entity.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * CustomOAuth2UserDetails class is a custom implementation of the OAuth2User interface.
 * It extends the User class and implements the OAuth2User interface.
 * It is used to store the user details and attributes from the OAuth2 provider.
 */
@Getter
public class CustomOAuth2UserDetails extends User implements OAuth2User {

    private final OAuth2Provider provider;
    private final List<GrantedAuthority> authorities;
    private final Map<String, Object> attributes;

    public CustomOAuth2UserDetails(User user, OAuth2User oAuth2User, OAuth2Provider provider) {
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

    public boolean isOAuthUser() {
        return true;
    }

    public boolean isGitHubConnected() {
        return this.getProvider().equals(OAuth2Provider.GITHUB);
    }

    public boolean isGoogleConnected() {
        return this.getProvider().equals(OAuth2Provider.GOOGLE);
    }

    public boolean isFacebookConnected() {
        return this.getProvider().equals(OAuth2Provider.FACEBOOK);
    }
}
