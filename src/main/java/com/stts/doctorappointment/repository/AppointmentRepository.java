package com.stts.doctorappointment.repository;

import com.stts.doctorappointment.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    /**
     * Checks if there exists any appointment within the given time ranges.
     *
     * @param startTime1 the start time of the first range
     * @param endTime1 the end time of the first range
     * @param startTime2 the start time of the second range
     * @param endTime2 the end time of the second range
     * @return true if an appointment exists within the given time ranges, false otherwise
     */
    boolean existsByStartTimeBetweenOrEndTimeBetween(LocalDateTime startTime1, LocalDateTime endTime1, LocalDateTime startTime2, LocalDateTime endTime2);

    /**
     * Finds all appointments that have been reserved by a patient.
     *
     * @return a list of reserved appointments
     */
    List<Appointment> findByPatientNotNull();

    /**
     * Finds all appointments for a specific patient by their ID.
     *
     * @param patientId the ID of the patient
     * @return a list of appointments for the specified patient
     */
    List<Appointment> findAllByPatientId(Long patientId);

    /**
     * Finds all appointments based on their taken status.
     *
     * @param isTaken the taken status of the appointments
     * @return a list of appointments with the specified taken status
     */
    List<Appointment> findAllByTakenIs(boolean isTaken);

    /**
     * Finds all appointments within a specific time range.
     *
     * @param startOfDay the start of the time range
     * @param endOfDay the end of the time range
     * @return a list of appointments within the specified time range
     */
    List<Appointment> findAllByStartTimeBeforeAndEndTimeAfter(LocalDateTime startOfDay, LocalDateTime endOfDay);
}