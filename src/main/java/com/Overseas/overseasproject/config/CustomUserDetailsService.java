package com.Overseas.overseasproject.config;

import com.Overseas.overseasproject.model.User;
import com.Overseas.overseasproject.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * Custom implementation of Spring Security's UserDetailsService interface.
 */
@Component
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    UserRepository userRepo; // Autowired UserRepository for accessing user data

    /**
     * Loads user details by username (email).
     *
     * @param email The email of the user to load details for.
     * @return UserDetails object containing user details.
     * @throws UsernameNotFoundException Exception thrown if user with specified email is not found.
     */
    @Override
    @Cacheable(value = "users", key = "#email") // Caches the result of this method based on email
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        System.out.println("auth enter");
        User user = userRepo.findByEmail(email); // Find user by email
        System.out.println("auth exit");
        System.out.println(user);
        if (user == null) {
            System.out.println("in if");
            throw new UsernameNotFoundException("Username not found"); // Throw exception if user not found
        } else {
            System.out.println("in else");
            return new CustomUser(user); // Return CustomUser object containing user details
        }
    }
}
