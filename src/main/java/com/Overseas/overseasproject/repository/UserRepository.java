package com.Overseas.overseasproject.repository;

import com.Overseas.overseasproject.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for accessing and managing User entities.
 * Provides methods to perform CRUD operations and custom queries on the User table.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by their role and email.
     *
     * @param role  the role of the user
     * @param email the email of the user
     * @return the user with the specified role and email
     */
    @Query("SELECT u FROM User u WHERE u.role = ?1 AND u.email = ?2")
    User findByRoleAndEmail(String role, String email);

    /**
     * Finds a user by their email.
     *
     * @param email the email of the user
     * @return the user with the specified email
     */
    User findByEmail(String email);

    /**
     * Finds all users except the one with the specified email.
     * Results are paginated.
     *
     * @param emailToExclude the email of the user to exclude from the results
     * @param pageable       pagination information
     * @return a list of users excluding the specified email
     */
    @Query("SELECT u FROM User u WHERE u.email <> ?1")
    List<User> findAllExceptOneByEmail(String emailToExclude, Pageable pageable);

    /**
     * Finds users by their role.
     * Results are paginated.
     *
     * @param role     the role of the users
     * @param pageable pagination information
     * @return a page of users with the specified role
     */
    Page<User> findByRole(String role, Pageable pageable);
}
