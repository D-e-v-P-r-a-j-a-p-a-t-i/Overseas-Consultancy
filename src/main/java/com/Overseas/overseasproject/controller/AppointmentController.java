package com.Overseas.overseasproject.controller;

import com.Overseas.overseasproject.AppointmentStatus;
import com.Overseas.overseasproject.config.CustomUser;
import com.Overseas.overseasproject.model.Appointment;
import com.Overseas.overseasproject.model.User;
import com.Overseas.overseasproject.repository.AppointmentRepository;
import com.Overseas.overseasproject.repository.UserRepository;
import com.Overseas.overseasproject.service.ErrorLogService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Optional;

@Controller
public class AppointmentController {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private ErrorLogService errorLogService;

    @Autowired
    private UserRepository userRepository;

    /**
     * Displays the book appointment page for a specific consultant.
     *
     * @param consultantId the consultant ID.
     * @param model        the model to pass attributes to the view.
     * @return the name of the book appointment page view or redirects to the about page if there are pending appointments.
     */
    @GetMapping("/book-appointment")
    public String bookAppointmentByConsultantId(@RequestParam("id") String consultantId, Model model) {
        try {
            // Retrieving currently authenticated user
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Long studentId = ((CustomUser) authentication.getPrincipal()).getId();

            // Checking for pending appointments
            Optional<Appointment> firstPendingAppointment = appointmentRepository.findFirstByConsultantIdAndStudentIdAndStatusOrderByStartTimeAsc(Long.parseLong(consultantId), studentId, AppointmentStatus.PENDING);
            if (firstPendingAppointment.isPresent()) {
                // Redirecting with error message if pending appointment found
                errorLogService.logError("Error in bookAppointmentById", "because already there is pending appointment");
                return "redirect:/about";
            }

            // Adding attributes to model for book appointment page
            model.addAttribute("consultantId", consultantId);
            model.addAttribute("studentId", studentId);
            model.addAttribute("appointment", new Appointment());
            return "book-appointment";
        } catch (Exception e) {
            // Logging and redirecting in case of error
            errorLogService.logError("Error trying to book appointment", e.getMessage());
            return "redirect:/about";
        }
    }


    /**
     * Adds a new appointment.
     *
     * @param consultantId The ID of the consultant for whom the appointment is being added.
     * @param req          The HTTP servlet request containing appointment details.
     * @param principal    The currently authenticated user.
     * @return A redirect to the about page.
     * @throws ParseException If parsing of date/time fails.
     */
    @PostMapping("/add-appointment/{id}")
    public String addAppointment(@PathVariable("id") String consultantId, HttpServletRequest req, Principal principal) throws ParseException {
        try {
            // Parsing consultant and student IDs
            Long consultId = Long.parseLong(consultantId);
            User current = userRepository.findByEmail(principal.getName());
            Long studentId = Long.parseLong(String.valueOf(current.getId()));

            // Parsing date/time parameters
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
            LocalDateTime parsedStartTime = LocalDateTime.parse(req.getParameter("startTime"), formatter);
            LocalDateTime parsedEndTime = LocalDateTime.parse(req.getParameter("endTime"), formatter);

            // Converting to Timestamps
            Timestamp startTimestamp = Timestamp.valueOf(parsedStartTime);
            Timestamp endTimestamp = Timestamp.valueOf(parsedEndTime);

            // Extracting note
            String note = req.getParameter("note").trim();

            // Checking for overlapping appointments
            Appointment existingAppointment = appointmentRepository.findFirstByStudentIdAndStartTimeLessThanEqualAndEndTimeGreaterThanEqualOrderByStartTime(current.getId(), new Date(startTimestamp.getTime()), new Date(endTimestamp.getTime()));

            if (existingAppointment != null) {
                // Redirecting with error message if overlap found
                return "redirect:/about?error=overlap";
            }

            // Saving new appointment
            Appointment appointment = new Appointment(studentId, consultId, startTimestamp, endTimestamp, AppointmentStatus.PENDING, note);
            appointmentRepository.save(appointment);

            return "redirect:/about";
        } catch (Exception e) {
            // Logging and redirecting in case of error
            errorLogService.logError("Error while booking appointment post submit", e.getMessage());
            return "redirect:/about";
        }
    }

    /**
     * Updates the status of an appointment to ACCEPTED.
     *
     * @param appointmentId The ID of the appointment to be updated.
     * @return A redirect to the about page.
     */
    @GetMapping("/update-appointment-accept/{id}")
    public String updateAppointmentAccept(@PathVariable("id") String appointmentId) {
        try {
            // Finding appointment by ID
            Optional<Appointment> appointment = appointmentRepository.findById((long) Integer.parseInt(appointmentId));
            if (!appointment.isPresent()) {
                // Redirecting with error if appointment not found
                throw new UsernameNotFoundException("Appointment not found");
            } else {
                // Updating appointment status to ACCEPTED
                appointment.get().setStatus(AppointmentStatus.ACCEPTED);
                appointmentRepository.save(appointment.get());
                return "redirect:/about";
            }
        } catch (Exception e) {
            // Logging and redirecting in case of error
            errorLogService.logError("Error while accepting appointment", e.getMessage());
            return "redirect:/about";
        }
    }

    /**
     * Updates the status of an appointment to DECLINED.
     *
     * @param appointmentId The ID of the appointment to be updated.
     * @return A redirect to the about page.
     */
    @GetMapping("/update-appointment-decline/{id}")
    public String updateAppointmentDecline(@PathVariable("id") String appointmentId) {
        try {
            // Finding appointment by ID
            Optional<Appointment> appointment = appointmentRepository.findById((long) Integer.parseInt(appointmentId));
            if (!appointment.isPresent()) {
                // Redirecting with error if appointment not found
                throw new UsernameNotFoundException("Appointment not found");
            } else {
                // Updating appointment status to DECLINED
                appointment.get().setStatus(AppointmentStatus.DECLINED);
                appointmentRepository.save(appointment.get());
                return "redirect:/about";
            }
        } catch (Exception e) {
            // Logging and redirecting in case of error
            errorLogService.logError("Error while declining appointment", e.getMessage());
            return "redirect:/about";
        }
    }
}
