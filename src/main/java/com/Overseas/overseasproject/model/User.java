package com.Overseas.overseasproject.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * This class represents a user entity in the system.
 */
@Entity
@Data
@Getter
@Setter
@NoArgsConstructor
@ToString
@Table(name = "users", indexes = {
        @Index(name = "idx_email", columnList = "email"), // Index for the email column
        @Index  (name = "idx_role", columnList = "role")    // Index for the role column
})
public class User {

    // Unique identifier for each user
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ID will be generated automatically
    private Long id;

    // Name of the user
    private String name;

    // Password for the user's account
    private String password;

    // Email of the user, must be unique and not null
    @Column(nullable = false, unique = true)
    private String email;

    // Role of the user, must not be null
    @Column(nullable = false)
    private String role;

    /**
     * Parameterized constructor to create a User instance with specific details.
     *
     * @param name     Name of the user
     * @param password Password for the user's account
     * @param email    Email of the user
     * @param role     Role of the user
     */
    public User(String name, String password, String email, String role) {
        this.name = name;
        this.password = password;
        this.email = email;
        this.role = role;
    }
}
