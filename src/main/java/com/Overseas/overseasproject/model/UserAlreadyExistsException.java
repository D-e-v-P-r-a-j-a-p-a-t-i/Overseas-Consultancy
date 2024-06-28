package com.Overseas.overseasproject.model;

/**
 * This exception is thrown when a user tries to register with an email that already exists in the system.
 */
public class UserAlreadyExistsException extends Throwable {

    /**
     * Constructor to create a new UserAlreadyExistsException with a specific message.
     *
     * @param message Detailed message explaining the reason for the exception
     */
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
