package com.example.Todo_list.service.guest;

import com.example.Todo_list.entity.User;
import com.example.Todo_list.repository.RoleRepository;
import com.example.Todo_list.repository.UserRepository;
import com.example.Todo_list.security.local.WebSecurityUserDetails;
import com.example.Todo_list.utils.PasswordService;
import com.example.Todo_list.utils.SampleTodoInitializer;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Service class for guest users.
 */
@Service
@Transactional
@RequiredArgsConstructor
public class GuestService {

    private static final Logger logger = LoggerFactory.getLogger(GuestService.class);
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordService passwordService;
    private final SampleTodoInitializer todoInitializer;

    /**
     * Creates a temporary guest user and authenticates them.
     *
     * @param request the request
     */
    public void createTemporaryUser(HttpServletRequest request) {
        UUID uuid = UUID.randomUUID();

        logger.info("Creating a guest user...");
        User guest = new User();
        guest.setFirstName("Guest");
        guest.setLastName(uuid.toString().substring(0, 8));
        guest.setRole(roleRepository.findByName("USER").get());
        guest.setEmail("GUEST_" + uuid.toString().substring(0, 8) + "@example.com");
        guest.setPassword(passwordService.generateEncodedPassword(64));
        guest.setIsGuest(true);
        userRepository.save(guest);
        logger.info("GuestService.createTemporaryUser: Saved " + guest);

        logger.info("GuestService.createTemporaryUser: Initializing user's todo list...");
        todoInitializer.initUserToDo(guest);

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                new WebSecurityUserDetails(guest),
                null,
                List.of(new SimpleGrantedAuthority(guest.getRole().getName()))
        );
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        HttpSession session = request.getSession(true);
        session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

        logger.info("Guest user signed in: " + guest);
    }

    /**
     * Deletes expired guest users after 30 minutes
     */
    @Scheduled(fixedRate = 60000)
    public void deleteExpiredUsers() {
        logger.info("GuestService.deleteExpiredUsers(): Deleting expired users...");
        LocalDateTime now = LocalDateTime.now();

        for (User user : userRepository.findAll()) {
            if (user.getIsGuest()  && now.minusMinutes(30).isAfter(user.getCreatedAt().toLocalDateTime())) {
                logger.info("GuestService.deleteExpiredUsers(): Deleted expired user: {}", user);
                userRepository.delete(user);
            }
        }
    }
}