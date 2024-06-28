package com.Overseas.overseasproject.service;

import com.Overseas.overseasproject.model.User;
import com.Overseas.overseasproject.model.UserNotFoundException;
import com.Overseas.overseasproject.repository.UserRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

// Service class for managing users
@Service
public class UserService {

    private final UserRepository userRepo;
    private final ErrorLogService errorLogService;

    // Constructor to inject UserRepository and ErrorLogService
    public UserService(UserRepository userRepo, ErrorLogService errorLogService) {
        this.userRepo = userRepo;
        this.errorLogService = errorLogService;
    }

    /**
     * Lists all users.
     *
     * @return List of all users.
     */
    public List<User> listAll() {
        try {
            return userRepo.findAll();
        } catch (Exception e) {
            // Log error if fetching users list fails
            errorLogService.logError("Error while fetching users list", e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * Lists all consultants.
     *
     * @return List of all consultants.
     */
    public List<User> listAllConsultants() {
        try {
            return (List<User>) userRepo.findByRole("consultant", null);
        } catch (Exception e) {
            // Log error if fetching consultants list fails
            errorLogService.logError("Error while fetching consultants list", e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * Saves a user.
     *
     * @param user The user to save.
     */
    public void save(User user) {
        try {
            // Save user to the repository
            userRepo.save(user);
        } catch (Exception e) {
            // Log error if saving user fails
            errorLogService.logError("Error while saving user to Database", e.getMessage());
        }
    }

    /**
     * Retrieves a user by ID.
     *
     * @param id The ID of the user to retrieve.
     * @return The user with the specified ID.
     * @throws UserNotFoundException if the user with the specified ID is not found.
     */
    public User get(Long id) throws UserNotFoundException {
        try {
            Optional<User> result = userRepo.findById(id);
            if (result.isPresent()) {
                return result.get();
            }
            throw new UserNotFoundException("Could not find any users with Id " + id);
        } catch (Exception e) {
            // Log error if fetching user fails
            errorLogService.logError("Error while fetching user with id: " + id, e.getMessage());
            throw new UserNotFoundException("Could not find any users with Id " + id);
        }
    }

    /**
     * Retrieves a user by E-mail.
     *
     * @param email The E-mail of the user to retrieve.
     * @return The user with the specified E-mail.
     * @throws UserNotFoundException if the user with the specified email is not found.
     */
    public User getUserByRoleAndEmail(String role, String email) throws UserNotFoundException {
        try {
            Optional<User> result;

            // Check if role is specified, if not, fetch by email only
            if (Objects.isNull(role) || role.isEmpty()) {
                result = Optional.ofNullable(userRepo.findByEmail(email));
            } else {
                result = Optional.ofNullable(userRepo.findByRoleAndEmail(role, email));
            }

            if (result.isPresent()) {
                return result.get();
            } else {
                return null;
            }
        } catch (Exception e) {
            // Log error if fetching user by email fails
            errorLogService.logError("Error while fetching user with email: " + email, e.getMessage());
            throw new UserNotFoundException("Could not find any users with email " + email);
        }
    }

    /**
     * Updates a user's password.
     *
     * @param id      The ID of the user to update.
     * @param newUser The user object containing the new password.
     * @throws UserNotFoundException if the user with the specified ID is not found.
     */
    @CacheEvict(value = "users", key = "#newUser.email")
    public void update(Long id, User newUser) throws UserNotFoundException {
        try {
            Optional<User> existingUser = userRepo.findById(id);
            if (existingUser.isPresent()) {
                // Update user's password
                userRepo.save(newUser);
            } else {
                throw new UserNotFoundException("Could not find any users with Id " + id);
            }
        } catch (Exception e) {
            // Log error if updating user fails
            errorLogService.logError("Error while updating user", e.getMessage());
            throw new UserNotFoundException("Could not find any users with Id " + id);
        }
    }

    /**
     * Retrieves a paginated list of users.
     *
     * @param role     The role of the users to retrieve.
     * @param pageable The pageable object containing pagination information.
     * @return A paginated list of users.
     */
    public Page<User> getPaginatedUsers(String role, Pageable pageable) {
        try {
            // If role is 'admin', fetch all users, otherwise fetch only consultants
            if ("admin".equals(role)) {
                return userRepo.findAll(pageable);
            } else {
                return userRepo.findByRole("consultant", pageable);
            }
        } catch (Exception e) {
            // Log error if implementing pagination fails
            errorLogService.logError("Error while implementing pagination", e.getMessage());
        }
        return null;
    }
}
