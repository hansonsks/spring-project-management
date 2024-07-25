package com.example.Todo_list.security;

import com.example.Todo_list.security.local.WebSecurityUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AccessControlService {

    public boolean hasAccess(Authentication authentication, Long userId) {
        if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return true;
        }

        Long currentUserId = ((WebSecurityUserDetails) authentication.getPrincipal()).getId();
        return currentUserId.equals(userId);
    }

    public boolean hasAccess(Authentication authentication, List<Long> userIds) {
        if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return true;
        }

        Long currentUserId = ((WebSecurityUserDetails) authentication.getPrincipal()).getId();
        return userIds.contains(currentUserId);
    }

    public boolean hasAdminAccess(Authentication authentication) {
        return authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
}
