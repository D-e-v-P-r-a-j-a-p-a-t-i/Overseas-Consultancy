package com.Overseas.overseasproject.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Arrays;

/**
 * Configuration class for security-related configurations.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Configures the password encoder bean.
     *
     * @return BCryptPasswordEncoder object for password encoding.
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(5); // Using BCryptPasswordEncoder with strength of 5
    }

    /**
     * Provides the custom user details service bean.
     *
     * @return CustomUserDetailsService object for handling user details.
     */
    @Bean
    public UserDetailsService getUserDetailsService() {
        return new CustomUserDetailsService(); // Provides custom user details service
    }

    /**
     * Configures the authentication provider bean.
     *
     * @return DaoAuthenticationProvider object for authentication.
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(getUserDetailsService());
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        return daoAuthenticationProvider; // Configures DaoAuthenticationProvider with custom user details service and password encoder
    }

    /**
     * Configures the security filter chain.
     *
     * @param http HttpSecurity object for configuring security.
     * @return SecurityFilterChain object for the security filter chain.
     * @throws Exception Exception thrown if configuration fails.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable()) // Disable CSRF protection
                .authorizeHttpRequests(req -> req
                        .requestMatchers("/", "/add-user").permitAll() // Allow access to specific URLs without authentication
                        .anyRequest().authenticated() // Require authentication for all other requests
                )
                .formLogin(cust -> cust
                        .loginPage("/") // Specifies the login page URL
                        .loginProcessingUrl("/doLogin") // Specifies the URL for processing login
                        .usernameParameter("username") // Specifies the parameter name for username
                        .passwordParameter("password") // Specifies the parameter name for password
                        .permitAll() // Allows all users to access the login page
                        .successHandler((request, response, authentication) -> {
                            response.sendRedirect("/about"); // Redirects to the about page after successful login
                        })
                        .failureHandler((request, response, authentication) -> {
                            String errorMessage = "Please fill out your credentials.";
                            response.sendRedirect("/?error=" + errorMessage); // Redirects to login page with error message on failed login
                        })

                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)  // Configures session creation policy
                );
        return http.build(); // Builds and returns SecurityFilterChain
    }

    /**
     * Additional configuration for caching.
     */
    @Configuration
    @EnableCaching
    public class CacheConfig extends CachingConfigurerSupport {

        /**
         * Configures cache manager bean.
         *
         * @return CacheManager object for managing caching.
         */
        @Bean
        public CacheManager cacheManager() {
            SimpleCacheManager cacheManager = new SimpleCacheManager();
            cacheManager.setCaches(Arrays.asList(
                    new ConcurrentMapCache("users") // Creates a ConcurrentMapCache for caching users
            ));
            return cacheManager; // Returns configured CacheManager
        }
    }
}
