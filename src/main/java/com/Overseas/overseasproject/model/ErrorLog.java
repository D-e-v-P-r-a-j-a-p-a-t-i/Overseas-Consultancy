package com.Overseas.overseasproject.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * This class represents an error log entry, which is used to store information about errors that occur in the system.
 */
@Entity
public class ErrorLog {

    // Unique identifier for each error log entry
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ID will be generated automatically
    private Long id;

    // Error message describing what went wrong
    private String message;

    // Additional details about the error
    private String details;

    // Timestamp when the error occurred
    private LocalDateTime timestamp;

    /**
     * Default constructor required by JPA.
     */
    public ErrorLog() {
    }

    /**
     * Parameterized constructor to create an ErrorLog instance with specific details.
     *
     * @param message   Error message
     * @param details   Additional details about the error
     * @param timestamp Timestamp when the error occurred
     */
    public ErrorLog(String message, String details, LocalDateTime timestamp) {
        this.message = message;
        this.details = details;
        this.timestamp = timestamp;
    }
}
