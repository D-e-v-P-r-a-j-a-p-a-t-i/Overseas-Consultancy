package com.Overseas.overseasproject.repository;

import com.Overseas.overseasproject.AppointmentStatus;
import com.Overseas.overseasproject.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for accessing and managing Appointment entities.
 * Provides methods to perform CRUD operations and custom queries on the Appointment table.
 */
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    /**
     * Finds all appointments for a specific student by their ID.
     *
     * @param studentId the ID of the student
     * @return a list of appointments for the specified student
     */
    ArrayList<Appointment> findAllByStudentId(Long studentId);

    /**
     * Finds all appointments for a specific consultant by their ID.
     *
     * @param consultantId the ID of the consultant
     * @return a list of appointments for the specified consultant
     */
    ArrayList<Appointment> findAllByConsultantId(Long consultantId);

    /**
     * Finds the first appointment for a specific student that overlaps with a given time range.
     * The appointment is ordered by the start time.
     *
     * @param studentId the ID of the student
     * @param startTime the start time of the range
     * @param endTime   the end time of the range
     * @return the first overlapping appointment for the specified student
     */
    Appointment findFirstByStudentIdAndStartTimeLessThanEqualAndEndTimeGreaterThanEqualOrderByStartTime(
            Long studentId, Date startTime, Date endTime);

    /**
     * Finds the first appointment for a specific consultant and student with a given status.
     * The appointment is ordered by the start time in ascending order.
     *
     * @param consultantId the ID of the consultant
     * @param studentId    the ID of the student
     * @param status       the status of the appointment
     * @return an optional containing the first matching appointment, or empty if none found
     */
    Optional<Appointment> findFirstByConsultantIdAndStudentIdAndStatusOrderByStartTimeAsc(
            Long consultantId, Long studentId, AppointmentStatus status);

}
