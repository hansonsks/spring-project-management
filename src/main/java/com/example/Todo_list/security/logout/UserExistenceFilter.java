package com.example.Todo_list.security.logout;

import com.example.Todo_list.security.local.WebSecurityUserDetails;
import com.example.Todo_list.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filter to check if the user exists in the database.
 */
@Component
@RequiredArgsConstructor
public class UserExistenceFilter extends OncePerRequestFilter {

    private final UserService userService;

    /**
     * Checks if the user exists in the database, if not, invalidates the current session and redirects to the logout page.
     *
     * @param request     the request
     * @param response    the response
     * @param filterChain the filter chain
     * @throws ServletException if a servlet exception occurs
     * @throws IOException      if an I/O exception occurs
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof WebSecurityUserDetails) {
            Long userId = ((WebSecurityUserDetails) auth.getPrincipal()).getId();
            try {
                userService.findUserById(userId);
            } catch (EntityNotFoundException e) {
                // Invalidate session if user does not exist
                HttpSession session = request.getSession(false);
                if (session != null) {
                    session.invalidate();
                }
                SecurityContextHolder.clearContext();
                response.sendRedirect("/logout?userDeleted=true");
                return;

            }
        }
        filterChain.doFilter(request, response);
    }
}
