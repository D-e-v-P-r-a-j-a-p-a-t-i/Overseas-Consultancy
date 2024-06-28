package com.Overseas.overseasproject.model;

/**
 * This exception is thrown when a requested user is not found in the system.
 */
public class UserNotFoundException extends Throwable {

    /**
     * Constructor to create a new UserNotFoundException with a specific message.
     *
     * @param message Detailed message explaining the reason for the exception
     */
    public UserNotFoundException(String message) {
        super(message);
    }
}
