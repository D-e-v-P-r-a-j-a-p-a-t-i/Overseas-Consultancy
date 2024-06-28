package com.Overseas.overseasproject.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import java.io.IOException;

/**
 * Custom Basic Authentication Filter that extends BasicAuthenticationFilter.
 */
public class CustomBasicAuthFilter extends BasicAuthenticationFilter {

    /**
     * Constructor for CustomBasicAuthFilter.
     *
     * @param authenticationManager The authentication manager to use for authentication.
     */
    public CustomBasicAuthFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    /**
     * Redirects to the "/about" page upon successful authentication.
     *
     * @param request    The HTTP servlet request.
     * @param response   The HTTP servlet response.
     * @param authResult The Authentication object representing the authenticated user.
     * @throws IOException If an I/O exception occurs.
     */
    protected void onSuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, Authentication authResult) throws IOException {
        response.sendRedirect("/about"); // Redirect to the "/about" page upon successful authentication
    }
}
