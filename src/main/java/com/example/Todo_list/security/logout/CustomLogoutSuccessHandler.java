package com.example.Todo_list.security.logout;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * Custom logout success handler.
 */
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {

    /**
     * Redirects the user to the login page after a successful logout.
     *
     * @param request        the request
     * @param response       the response
     * @param authentication the authentication
     * @throws IOException if an input or output exception occurs
     */
    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        Map<String, String> params = new HashMap<>();

        // Collect all relevant parameters
        Enumeration<String> parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String paramName = parameterNames.nextElement();
            String paramValue = request.getParameter(paramName);
            params.put(paramName, paramValue);
        }

        // Build the redirect URL
        StringBuilder redirectUrl = new StringBuilder("/login-form");
        if (!params.isEmpty()) {
            redirectUrl.append("?");
            params.forEach((key, value) -> {
                if (value != null && !value.isEmpty()) {
                    redirectUrl.append(key).append("=").append(value).append("&");
                }
            });

            // Remove the last '&'
            redirectUrl.setLength(redirectUrl.length() - 1);
        }

        response.sendRedirect(redirectUrl.toString());
    }
}
