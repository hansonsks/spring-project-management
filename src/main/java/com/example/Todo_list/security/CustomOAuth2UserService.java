package com.example.Todo_list.security;

import com.example.Todo_list.entity.OAuthUser;
import com.example.Todo_list.entity.User;
import com.example.Todo_list.repository.OAuthUserRepository;
import com.example.Todo_list.repository.RoleRepository;
import com.example.Todo_list.repository.UserRepository;
import com.example.Todo_list.utils.PasswordService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Arrays;
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

        // TODO: Transition hard-coding values to using CommonOAuth2Provider values
        System.out.println(Arrays.toString(CommonOAuth2Provider.values()));

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
        User user;
        String email = oAuth2User.getAttribute("email");

        /*
            If an email was found in the database, create an OAuthUser and link it to the old account
            One major flaw in this approach is email verification must be needed when signing up
            Otherwise, you can steal another user's information by possessing the OAuth2 provider account with the
            same email if you did not previously register in the system.
         */
        if (email != null && userRepository.findByEmail(email).isPresent()) {
            logger.info("CustomOAuth2UserService.createGitHubUser(): User with existing email + " + email + " found");
            user = userRepository.findByEmail(email).get();
        } else {
            user = new User();
            user.setFirstName(oAuth2User.getAttribute("login"));
            user.setLastName("");
            user.setEmail(oAuth2User.getAttribute("email"));
            if (user.getEmail() == null || user.getEmail().isEmpty()) {
                user.setEmail(user.getFirstName() + "@placeholder.email");
            }
            user.setPassword(passwordService.generateEncodedPassword(32));
            user.setRole(roleRepository.findByName("ADMIN").get());

            logger.info("CustomOAuth2UserService.createGitHubUser(): Saving " + user);
            userRepository.save(user);
        }

        OAuthUser oAuthUser = new OAuthUser();
        oAuthUser.setProvider(provider);
        oAuthUser.setProviderUserId(oAuth2User.getAttribute("id").toString());
        oAuthUser.setUser(user);

        logger.info("CustomOAuth2UserService.createGitHubUser(): Saving OAuthUser: " + oAuthUser);
        oAuthUserRepository.save(oAuthUser);

        return user;
    }

    private User createGoogleUser() {
        return null;    // TODO: Implement this if needed
    }
}
