package com.Overseas.overseasproject.config;

import com.Overseas.overseasproject.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;

/**
 * Custom implementation of UserDetails interface to represent authenticated users.
 */
public class CustomUser implements UserDetails {

    private User user; // User object representing the authenticated user

    /**
     * Constructor to create CustomUser from a User object.
     *
     * @param user The User object representing the authenticated user.
     */
    public CustomUser(User user) {
        this.user = user;
    }

    /**
     * Returns the authorities granted to the user.
     *
     * @return A collection of GrantedAuthority objects representing user roles.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(user.getRole()); // Create a GrantedAuthority based on user's role
        return Arrays.asList(authority); // Return a collection containing the authority
    }

    /**
     * Returns the password used to authenticate the user.
     *
     * @return The user's password.
     */
    @Override
    public String getPassword() {
        return user.getPassword(); // Return the user's password
    }

    /**
     * Returns the username used to authenticate the user.
     *
     * @return The user's email address (username).
     */
    @Override
    public String getUsername() {
        return user.getEmail(); // Return the user's email address (username)
    }

    /**
     * Indicates whether the user's account has not expired.
     *
     * @return true if the user's account is valid (not expired), false otherwise.
     */
    @Override
    public boolean isAccountNonExpired() {
        return true; // User account is always considered non-expired
    }

    /**
     * Indicates whether the user is not locked.
     *
     * @return true if the user is not locked, false otherwise.
     */
    @Override
    public boolean isAccountNonLocked() {
        return true; // User account is always considered non-locked
    }

    /**
     * Indicates whether the user's credentials (password) are not expired.
     *
     * @return true if the user's credentials are valid (not expired), false otherwise.
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true; // User credentials are always considered non-expired
    }

    /**
     * Indicates whether the user is enabled.
     *
     * @return true if the user is enabled, false otherwise.
     */
    @Override
    public boolean isEnabled() {
        return true; // User is always considered enabled
    }

    /**
     * Returns the ID of the user.
     *
     * @return The ID of the user.
     */
    public Long getId() {
        return user.getId(); // Return the ID of the user
    }
}
