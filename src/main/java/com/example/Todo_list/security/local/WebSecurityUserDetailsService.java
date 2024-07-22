package com.example.Todo_list.security.local;

import com.example.Todo_list.entity.User;
import com.example.Todo_list.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service for loading user details from the database
 */
@Service
@RequiredArgsConstructor
public class WebSecurityUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Load user details from the database by username
     *
     * @param username username
     * @return user details
     * @throws UsernameNotFoundException if user was not found
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByEmail(username);

        if (user.isPresent()) {
            return new WebSecurityUserDetails(user.get());
        } else {
            throw new UsernameNotFoundException("User with username=" + username + " was not found");
        }
    }
}
