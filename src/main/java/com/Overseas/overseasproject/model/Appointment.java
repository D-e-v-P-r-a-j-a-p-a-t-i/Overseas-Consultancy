package com.Overseas.overseasproject.model;

import com.Overseas.overseasproject.AppointmentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

/**
 * This class represents an appointment entity.
 */
@Entity
@Data
@Getter
@Setter
@ToString
@Table(name = "appointment") // Specifies the table name in the database
public class Appointment {

    // Default constructor for JPA
    public Appointment() {
    }

    // Unique identifier for each appointment
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ID will be generated automatically
    private Long id;

    // Note associated with the appointment
    @Column(nullable = false) // This field cannot be null in the database
    private String note;

    // Status of the appointment (e.g., Scheduled, Completed, Cancelled)
    private AppointmentStatus status;

    // Start time of the appointment
    private Date startTime;

    // End time of the appointment
    private Date endTime;

    // ID of the consultant associated with the appointment
    private Long consultantId;

    // ID of the student associated with the appointment
    private Long studentId;

    /**
     * Parameterized constructor to create an Appointment instance with specific details.
     *
     * @param studentId    ID of the student
     * @param consultantId ID of the consultant
     * @param startTime    Start time of the appointment
     * @param endTime      End time of the appointment
     * @param status       Status of the appointment
     * @param note         Note associated with the appointment
     */
    public Appointment(Long studentId, Long consultantId, Date startTime, Date endTime, AppointmentStatus status, String note) {
        this.status = status;
        this.startTime = startTime;
        this.endTime = endTime;
        this.consultantId = consultantId;
        this.studentId = studentId;
        this.note = note;
    }
}
