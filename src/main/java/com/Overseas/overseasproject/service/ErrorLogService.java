package com.Overseas.overseasproject.service;

import com.Overseas.overseasproject.model.ErrorLog;
import com.Overseas.overseasproject.repository.ErrorLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

// Service annotation indicates that this class provides some business logic
@Service
public class ErrorLogService {

    // Autowired annotation injects the ErrorLogRepository bean into this class
    @Autowired
    private ErrorLogRepository errorLogRepository;

    /**
     * Logs an error message with details.
     *
     * @param message The error message to log.
     * @param details The details or context of the error.
     * @return void
     */
    public void logError(String message, String details) {
        // Create a new ErrorLog object with the provided message, details, and the current date and time
        ErrorLog errorLog = new ErrorLog(message, details, LocalDateTime.now());

        // Save the error log using the errorLogRepository
        errorLogRepository.save(errorLog);
    }
}
