package com.Overseas.overseasproject;

import com.Overseas.overseasproject.model.User;
import com.Overseas.overseasproject.repository.UserRepository;
import com.Overseas.overseasproject.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class UserRepositoryTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Test for listing all users
    @Test
    void testListAll() {
        // Mocking the userRepository.findAll() method to return a list of users
        List<User> users = new ArrayList<>();
        users.add(new User(/* User details */));
        // Add more user objects as needed
        when(userRepository.findAll()).thenReturn(users);

        // Calling the method to fetch all users from the service
        List<User> result = userService.listAll();

        // Verifying that userRepository.findAll() is called once
        verify(userRepository, times(1)).findAll();

        // Asserting that the result returned by the service is equal to the list of users
        assertEquals(users, result);
    }

    // Test for finding a user by role and email
    @Test
    void testFindByRoleAndEmail() {
        // Given
        String role = "admin";
        String email = "admin34543@gmail.com";
        User user = new User();
        user.setRole(role);
        user.setEmail(email);
        when(userRepository.findByRoleAndEmail(role, email)).thenReturn(user);

        // When
        User foundUser = userRepository.findByRoleAndEmail(role, email);

        // Then
        assertNotNull(foundUser);
        assertEquals(role, foundUser.getRole());
        assertEquals(email, foundUser.getEmail());
    }

    // Test for finding a user by email
    @Test
    void testFindByEmail() {
        // Given
        String email = "admin34543@gmail.com";
        User user = new User();
        user.setEmail(email);
        when(userRepository.findByEmail(email)).thenReturn(user);

        // When
        User foundUser = userRepository.findByEmail(email);

        // Then
        assertNotNull(foundUser);
        assertEquals(email, foundUser.getEmail());
    }

    // Test for finding all users except one by email
    @Test
    void testFindAllExceptOneByEmail() {
        // Given
        String emailToExclude = "admin34543@gmail.com";
        Pageable pageable = Pageable.unpaged();
        List<User> userList = new ArrayList<>();
        when(userRepository.findAllExceptOneByEmail(emailToExclude, pageable)).thenReturn(userList);

        // When
        List<User> users = userRepository.findAllExceptOneByEmail(emailToExclude, pageable);

        // Then
        assertNotNull(users);
        assertEquals(userList, users);
    }

    // Test for finding users by role
    @Test
    void testFindByRole() {
        // Given
        String role = "student";
        Pageable pageable = Pageable.unpaged();
        Page<User> userPage = mock(Page.class);
        when(userRepository.findByRole(role, pageable)).thenReturn(userPage);

        // When
        Page<User> result = userRepository.findByRole(role, pageable);

        // Then
        assertNotNull(result);
        assertEquals(userPage, result);
    }

}
