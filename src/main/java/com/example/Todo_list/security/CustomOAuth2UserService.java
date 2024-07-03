package com.example.Todo_list.security;

import com.example.Todo_list.entity.OAuthUser;
import com.example.Todo_list.entity.Role;
import com.example.Todo_list.entity.User;
import com.example.Todo_list.repository.OAuthUserRepository;
import com.example.Todo_list.repository.RoleRepository;
import com.example.Todo_list.repository.UserRepository;
import com.example.Todo_list.utils.PasswordService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final static Logger logger = LoggerFactory.getLogger(CustomOAuth2UserService.class);
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final OAuthUserRepository oAuthUserRepository;
    private final PasswordService passwordService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // Hard-coded GitHub only, should have switch-cases for Facebook, Google, etc.
        String provider = "github";
        String uniqueId = oAuth2User.getAttribute("id").toString();

        Optional<OAuthUser> oAuthUser = oAuthUserRepository.findByProviderAndProviderUserId(provider, uniqueId);
        if (oAuthUser.isPresent()) {
            return new CustomOAuth2UserDetails(oAuthUser.get().getUser(), oAuth2User, provider);
        } else {
            return new CustomOAuth2UserDetails(createGitHubUser(provider, oAuth2User), oAuth2User, provider);
        }
    }

    private User createGitHubUser(String provider, OAuth2User oAuth2User) {
        User user = new User();
        Role role = roleRepository.findByName("ADMIN").get();
        user.setFirstName(oAuth2User.getAttribute("login"));
        user.setLastName("");
        user.setEmail(oAuth2User.getAttribute("email"));
        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            user.setEmail(user.getFirstName() + "@placeholder.email");
        }
        user.setPassword(passwordService.generateEncodedPassword(32));
        user.setRole(role);

        logger.info("CustomOAuth2UserService.createUser(): Saving " + user);
        userRepository.save(user);

        OAuthUser oAuthUser = new OAuthUser();
        oAuthUser.setProvider(provider);
        oAuthUser.setProviderUserId(oAuth2User.getAttribute("id").toString());
        oAuthUser.setUser(user);

        logger.info("CustomOAuth2UserService.createUser(): Saving " + oAuthUser);
        oAuthUserRepository.save(oAuthUser);

        return user;
    }

    private User createGoogleUser() {
        return null;    // TODO: Implement this if needed
    }
}
