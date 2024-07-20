package com.example.Todo_list.security;

import com.example.Todo_list.entity.OAuthUser;
import com.example.Todo_list.entity.User;
import com.example.Todo_list.repository.OAuthUserRepository;
import com.example.Todo_list.repository.RoleRepository;
import com.example.Todo_list.repository.UserRepository;
import com.example.Todo_list.utils.PasswordService;
import com.example.Todo_list.utils.SampleTodoInitializer;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final static Logger logger = LoggerFactory.getLogger(CustomOAuth2UserService.class);
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final OAuthUserRepository oAuthUserRepository;
    private final PasswordService passwordService;
    private final SampleTodoInitializer todoInitializer;

    // TODO: This could be refactored to a consolidated attribute class instead of multiple maps
    private final Map<OAuth2Provider, String> providerToUniqueId = Map.of(
            OAuth2Provider.GITHUB, "id",
            OAuth2Provider.GOOGLE, "sub",
            OAuth2Provider.FACEBOOK, "TODO: Please fill this in"
    );

    private final Map<OAuth2Provider, String> providerToFirstName = Map.of(
            OAuth2Provider.GITHUB, "login",
            OAuth2Provider.GOOGLE, "given_name",
            OAuth2Provider.FACEBOOK, "TODO: Please fill this in"
    );

    private final Map<OAuth2Provider, String> providerToLastName = Map.of(
            OAuth2Provider.GITHUB, "",
            OAuth2Provider.GOOGLE, "family_name",
            OAuth2Provider.FACEBOOK, "TODO: Please fill this in"
    );

    private final Map<OAuth2Provider, String> providerToEmail = Map.of(
            OAuth2Provider.GITHUB, "email",
            OAuth2Provider.GOOGLE, "email",
            OAuth2Provider.FACEBOOK, "TODO: Please fill this in"
    );

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2Provider provider;
        try {
            provider = OAuth2Provider.valueOf(userRequest.getClientRegistration().getClientName().toUpperCase());
            logger.info("CustomOAuth2UserService.loadUser(): OAuth2 login found Provider: " + provider);
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("Unexpected provider: " + userRequest.getClientRegistration().getClientName());
        }

        OAuth2User oAuth2User = super.loadUser(userRequest);
        logger.info(String.format("CustomOAuth2UserService.loadUser(): %s loaded successfully", oAuth2User));

        String uniqueId = oAuth2User.getAttribute(providerToUniqueId.get(provider)).toString();
        Optional<OAuthUser> oAuthUser = oAuthUserRepository.findByProviderAndProviderUserId(provider, uniqueId);
        if (oAuthUser.isPresent()) {
            logger.info("CustomOAuth2UserService.loadUser(): User found in database: " + oAuthUser.get().getUser());
            return new CustomOAuth2UserDetails(oAuthUser.get().getUser(), oAuth2User, provider);
        } else {
            logger.info("CustomOAuth2UserService.loadUser(): User not found in database. Creating new user...");
            return new CustomOAuth2UserDetails(createUser(oAuth2User, provider, uniqueId), oAuth2User, provider);
        }
    }

    private User createUser(OAuth2User oAuth2User, OAuth2Provider provider, String uniqueId) {
        User user = new User();

        String firstName = oAuth2User.getAttribute(providerToFirstName.get(provider));
        user.setFirstName(firstName == null ? String.format("%s User (%s)", provider.name(), uniqueId) : firstName);

        String lastName = oAuth2User.getAttribute(providerToLastName.get(provider));
        user.setLastName(lastName == null ? "" : lastName);

        String email = oAuth2User.getAttribute(providerToEmail.get(provider));
        user.setEmail(email == null ? user.getFirstName() + "@placeholder.email" : email);

        // Bad practice: Generate an "uncrackable" password for users who log in with OAuth
        user.setPassword(passwordService.generateEncodedPassword(64));
        user.setRole(roleRepository.findByName("ADMIN").get());

        logger.info("CustomOAuth2UserService.createUser(): Saving " + user);
        userRepository.save(user);

        logger.info("CustomOAuth2UserService.createUser(): Initializing user's todo list...");
        todoInitializer.initUserToDo(user);

        OAuthUser oAuthUser = new OAuthUser();
        oAuthUser.setProvider(provider);
        oAuthUser.setProviderUserId(oAuth2User.getAttribute(providerToUniqueId.get(provider)).toString());
        oAuthUser.setUser(user);

        logger.info("CustomOAuth2UserService.createGitHubUser(): Saving OAuthUser: " + oAuthUser);
        oAuthUserRepository.save(oAuthUser);

        return user;
    }
}
