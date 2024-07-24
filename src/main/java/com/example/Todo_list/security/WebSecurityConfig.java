package com.example.Todo_list.security;

import com.example.Todo_list.dto.StringToStateConverter;
import com.example.Todo_list.security.local.WebSecurityUserDetailsService;
import com.example.Todo_list.security.oauth2.CustomOAuth2AuthenticationSuccessHandler;
import com.example.Todo_list.security.oauth2.CustomOAuth2UserService;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

/**
 * Web security configuration class.
 */
@Configuration
@EnableWebSecurity
@Slf4j
@RequiredArgsConstructor
public class WebSecurityConfig implements WebMvcConfigurer {

    private final WebSecurityUserDetailsService userDetailsService;
    private final CustomOAuth2UserService oAuth2UserService;
    private final OAuth2AuthorizedClientService authorizedClientService;
    private final StringToStateConverter stringToStateConverter;

    /**
     * Access denied handler.
     *
     * @return AccessDeniedHandler
     */
    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (request, response, accessDeniedException) -> {
            RequestDispatcher requestDispatcher = request.getRequestDispatcher("error.html");
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            request.setAttribute("code", "403 / Forbidden");
            request.setAttribute("message", accessDeniedException.getMessage());
            requestDispatcher.forward(request, response);
        };
    }

    /**
     * Security filter chain.
     *
     * @param http HttpSecurity
     * @return SecurityFilterChain
     * @throws Exception Exception
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/login-form", "/users/create", "/error", "/about", "/javadoc/**", "/images/**").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login-form")
                        .defaultSuccessUrl("/", true)
                        .failureUrl("/login-form?error=true")
                        .permitAll()
                )
                .logout(logout -> logout
                        // .logoutSuccessUrl("/login-form?logout=true")
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                        .logoutSuccessHandler(new CustomLogoutSuccessHandler())
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )
                .exceptionHandling(exception -> exception
                        .accessDeniedHandler(accessDeniedHandler())
                )
                .rememberMe(rememberMe -> rememberMe
                        .key("uniqueAndSecret")
                        .tokenValiditySeconds(86400)    // Token is valid for 24 hours
                )
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login-form")
                        .userInfoEndpoint(userInfo -> userInfo.userService(oAuth2UserService))
                        .successHandler(new CustomOAuth2AuthenticationSuccessHandler(authorizedClientService))
                );

        return http.build();
    }

    /**
     * Configure global.
     *
     * @param auth AuthenticationManagerBuilder
     * @throws Exception Exception
     */
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService);
    }

    /**
     * Authentication entry point.
     *
     * @return AuthenticationEntryPoint
     */
    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, authException) -> {
            log.warn("Authentication for '{} {}' failed with error: {}",
                    request.getMethod(), request.getRequestURL(), authException.getMessage());
            response.sendError(UNAUTHORIZED.value(), authException.getMessage());
        };
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(stringToStateConverter);
        WebMvcConfigurer.super.addFormatters(registry);
    }
}
