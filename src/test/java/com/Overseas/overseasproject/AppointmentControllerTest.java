package com.Overseas.overseasproject;

import com.Overseas.overseasproject.config.CustomUser;
import com.Overseas.overseasproject.controller.AppointmentController;
import com.Overseas.overseasproject.model.Appointment;
import com.Overseas.overseasproject.model.User;
import com.Overseas.overseasproject.repository.AppointmentRepository;
import com.Overseas.overseasproject.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;

import java.security.Principal;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class AppointmentControllerTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private HttpServletRequest request;

    @Mock
    private Principal principal;

    @InjectMocks
    private AppointmentController appointmentController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Test for adding a new appointment
    @Test
    void testAddAppointment() throws Exception {
        // Mocking principal and user
        when(principal.getName()).thenReturn("test@example.com");
        User user = new User();
        user.setId(1L);
        when(userRepository.findByEmail("test@example.com")).thenReturn(user);

        // Mocking request parameters
        when(request.getParameter("startTime")).thenReturn("2024-06-01T10:00");
        when(request.getParameter("endTime")).thenReturn("2024-06-01T11:00");
        when(request.getParameter("note")).thenReturn("Test appointment");

        // Mocking no overlapping appointments
        when(appointmentRepository.findFirstByStudentIdAndStartTimeLessThanEqualAndEndTimeGreaterThanEqualOrderByStartTime(1L, new Date(), new Date())).thenReturn(null);

        // Mocking appointment save
        when(appointmentRepository.save(any())).thenReturn(new Appointment());

        // Calling the method
        String result = appointmentController.addAppointment("1", request, principal);

        // Verifying the result
        assertEquals("redirect:/about", result);

        // Verifying interactions
        verify(appointmentRepository, times(1)).save(any());
    }

    // Test for adding a new appointment with overlapping time slots
    @Test
    void testAddAppointment_Overlap() throws Exception {
        // Mocking principal, user, and request parameters
        HttpServletRequest request = mock(HttpServletRequest.class);
        Principal principal = mock(Principal.class);
        User currentUser = mock(User.class);
        Authentication authentication = mock(Authentication.class);
        CustomUser customUser = mock(CustomUser.class);
        SecurityContextHolder.setContext(new SecurityContextImpl());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(authentication.getPrincipal()).thenReturn(customUser);
        when(customUser.getId()).thenReturn(1L);
        when(principal.getName()).thenReturn("test@example.com");
        when(userRepository.findByEmail(anyString())).thenReturn(currentUser);
        when(currentUser.getId()).thenReturn(1L);
        when(request.getParameter("startTime")).thenReturn("2024-06-01T10:00");
        when(request.getParameter("endTime")).thenReturn("2024-06-01T12:00");
        when(request.getParameter("note")).thenReturn("Test note");

        // Mocking existing overlapping appointment
        Appointment existingAppointment = mock(Appointment.class);
        when(appointmentRepository.findFirstByStudentIdAndStartTimeLessThanEqualAndEndTimeGreaterThanEqualOrderByStartTime(anyLong(), any(Date.class), any(Date.class))).thenReturn(existingAppointment);

        // Calling the method
        String result = appointmentController.addAppointment("1", request, principal);

        // Verifying the result
        assertEquals("redirect:/about?error=overlap", result);

        // Verifying interactions
        verify(request, times(1)).getParameter("startTime");
        verify(request, times(1)).getParameter("endTime");
        verify(request, times(1)).getParameter("note");
        verify(appointmentRepository, times(1)).findFirstByStudentIdAndStartTimeLessThanEqualAndEndTimeGreaterThanEqualOrderByStartTime(anyLong(), any(Date.class), any(Date.class));
    }

    // Test for updating appointment status to ACCEPTED
    @Test
    void testUpdateAppointmentAccept() {
        // Mocking appointment ID and status
        String appointmentId = "1";
        Appointment appointment = new Appointment();
        appointment.setId(Long.parseLong(appointmentId));
        appointment.setStatus(AppointmentStatus.PENDING);

        // Mocking findById method of appointmentRepository
        when(appointmentRepository.findById(anyLong())).thenReturn(Optional.of(appointment));

        // Calling the method
        String result = appointmentController.updateAppointmentAccept(appointmentId);

        // Verifying the result
        assertEquals("redirect:/about", result);

        // Verifying interactions
        verify(appointmentRepository, times(1)).findById(Long.parseLong(appointmentId));
        verify(appointmentRepository, times(1)).save(appointment);
        assertEquals(AppointmentStatus.ACCEPTED, appointment.getStatus());
    }

    // Test for updating appointment status to DECLINED
    @Test
    void testUpdateAppointmentDecline() {
        // Mocking appointment ID and status
        String appointmentId = "1";
        Appointment appointment = new Appointment();
        appointment.setId(Long.parseLong(appointmentId));
        appointment.setStatus(AppointmentStatus.PENDING);

        // Mocking findById method of appointmentRepository
        when(appointmentRepository.findById(anyLong())).thenReturn(Optional.of(appointment));

        // Calling the method
        String result = appointmentController.updateAppointmentDecline(appointmentId);

        // Verifying the result
        assertEquals("redirect:/about", result);

        // Verifying interactions
        verify(appointmentRepository, times(1)).findById(Long.parseLong(appointmentId));
        verify(appointmentRepository, times(1)).save(appointment);
        assertEquals(AppointmentStatus.DECLINED, appointment.getStatus());
    }

}
