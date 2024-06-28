package com.Overseas.overseasproject;

import com.Overseas.overseasproject.controller.MainController;
import com.Overseas.overseasproject.model.User;
import com.Overseas.overseasproject.model.UserNotFoundException;
import com.Overseas.overseasproject.repository.UserRepository;
import com.Overseas.overseasproject.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class MainControllerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private Model model;

    @Mock
    private HttpServletRequest request;

    @Mock
    private UserService userService;

    @InjectMocks
    private MainController mainController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Test for adding a new user
    @Test
    void testAddUser() {
        // Mocking request parameters
        when(request.getParameter("name")).thenReturn("Test User");
        when(request.getParameter("password")).thenReturn("password");
        when(request.getParameter("email")).thenReturn("test@example.com");
        when(request.getParameter("role")).thenReturn("student");

        // Calling the method
        String result = mainController.addUserToDb(request, model);

        // Verifying the result
        assertEquals("redirect:/about", result);

        // Verifying interactions
        verify(userService, times(1)).save(any(User.class));
    }

    // Test for deleting a user
    @Test
    void testDeleteUser() {
        // Mocking user ID
        String userId = "1";

        // Calling the method
        String result = mainController.deleteUserById(userId, model);

        // Verifying the result
        assertEquals("redirect:/about", result);

        // Verifying interactions
        verify(userRepository, times(1)).deleteById(Long.parseLong(userId));
    }

    // Test for editing a user
    @Test
    void testEditUser() throws UserNotFoundException {
        // Mocking user ID and request parameters
        String userId = "1";
        when(userService.get(anyLong())).thenReturn(new User());
        when(request.getParameter("name")).thenReturn("Updated User");
        when(request.getParameter("email")).thenReturn("updated@example.com");
        when(request.getParameter("password")).thenReturn("updatedPassword");
        when(request.getParameter("role")).thenReturn("student");

        // Calling the method
        String result = mainController.postUpdateUserById(userId, request);

        // Verifying the result
        assertEquals("redirect:/about", result);

        // Verifying interactions
        verify(userService, times(1)).update(anyLong(), any(User.class));
    }

    // Test for fetching users
    @Test
    void testFetchUsers() {
        // Mocking list of users
        List<User> users = new ArrayList<>();
        users.add(new User(/* User details */));
        when(userService.listAll()).thenReturn(users);

        // Calling the method
        List<User> result = userService.listAll();

        // Verifying interactions
        verify(userService, times(1)).listAll();

        // Verifying the result
        assertEquals(users, result);
    }
}
