package com.stts.doctorappointment.service;

import com.stts.doctorappointment.exception.*;
import com.stts.doctorappointment.model.Appointment;
import com.stts.doctorappointment.model.Patient;
import com.stts.doctorappointment.repository.AppointmentRepository;
import com.stts.doctorappointment.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.OptimisticLockException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AppointmentService {
    private final static Logger logger = LoggerFactory.getLogger(AppointmentService.class);

    private final PatientService patientService;
    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;

    /**
     * Adds open appointment times within the specified time range.
     *
     * @param startTime the start time of the range
     * @param endTime the end time of the range
     */
    @Transactional
    public void addOpenTimes(LocalDateTime startTime, LocalDateTime endTime) {
        Objects.requireNonNull(startTime, "Start time must not be null");
        Objects.requireNonNull(endTime, "End time must not be null");
        validateTimeRange(startTime, endTime);

        while (startTime.plusMinutes(30).isBefore(endTime) || startTime.plusMinutes(30).isEqual(endTime)) {
            Appointment appointment = new Appointment(startTime, startTime.plusMinutes(30));
            appointmentRepository.save(appointment);
            startTime = startTime.plusMinutes(30);
        }
    }

    /**
     * Retrieves all reserved appointments.
     *
     * @return a list of reserved appointments
     */
    public List<Appointment> viewReservedAppointment() {
        return appointmentRepository.findByPatientNotNull();
    }

    /**
     * Retrieves all open appointments.
     *
     * @return a list of open appointments
     */
    public List<Appointment> viewOpenAppointments() {
        return appointmentRepository.findAllByTakenIs(false);
    }

    /**
     * Retrieves all open appointments for a specific day.
     *
     * @param availableAppointmentDay the day to check for open appointments
     * @return a list of open appointments for the specified day
     */
    public List<Appointment> viewOpenAppointments(LocalDate availableAppointmentDay) {
        LocalDateTime startOfDay = availableAppointmentDay.atStartOfDay();
        LocalDateTime endOfDay = startOfDay.toLocalDate().atTime(LocalTime.MAX);
        return appointmentRepository.findAllByStartTimeBeforeAndEndTimeAfter(startOfDay, endOfDay);
    }

    /**
     * Retrieves all taken appointments.
     *
     * @return a list of taken appointments
     */
    public List<Appointment> viewTakenAppointments() {
        return appointmentRepository.findAllByTakenIs(true);
    }

    /**
     * Deletes an open appointment by its ID.
     *
     * @param appointmentId the ID of the appointment to delete
     */
    @Transactional
    public void deleteOpenAppointment(Long appointmentId) {
        if (appointmentRepository.count() == 0) {
            logger.error("There is no appointment available!");
            throw new NoAvailableAppointmentException("No available appointments.");
        }
        Appointment appointment = findAppointmentById(appointmentId);
        if (appointment.isTaken()) {
            logger.error("Cannot delete a taken appointment.");
            throw new AlreadyTakenAppointmentException("Cannot delete a taken appointment.");
        }
        appointmentRepository.delete(appointment);
    }

    /**
     * Books an appointment for a patient.
     *
     * @param appointmentId the ID of the appointment to book
     * @param patient the patient booking the appointment
     */
    @Transactional
    public void bookAppointment(Long appointmentId, Patient patient) {
        try {
            Objects.requireNonNull(patient, "Patient must not be null");
            Patient patientDb = patientRepository.findByPhoneNumber(patient.getPhoneNumber())
                    .orElseGet(() -> patientService.addPatient(patient));

            Appointment appointment = findAppointmentById(appointmentId);
            Patient finalPatient = findPatientById(patientDb.getId());

            if (appointment.isTaken()) {
                throw new AlreadyTakenAppointmentException("This appointment is already taken.");
            }
            appointment.setTaken(true);
            appointment.setPatient(finalPatient);
            appointmentRepository.save(appointment);
        } catch (OptimisticLockException e) {
            logger.error("Optimistic lock exception occurred while booking appointment [{}]", appointmentId, e);
            throw new ConcurrentModificationException("The appointment was modified concurrently. Please try again.");
        }
    }

    private void validateTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        if (endTime.isBefore(startTime)) {
            logger.debug("End time [{}] cannot be before start time [{}]", endTime, startTime);
            throw new InvalidTimeRangeException("End time cannot be before start time.");
        }
    }

    private Appointment findAppointmentById(Long appointmentId) {
        return appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> {
                    logger.debug("Appointment [{}] not found.", appointmentId);
                    return new AppointmentNotFoundException("Appointment not found.");
                });
    }

    private Patient findPatientById(Long patientId) {
        return patientRepository.findById(patientId)
                .orElseThrow(() -> {
                    logger.debug("Patient [{}] not found.", patientId);
                    return new PatientNotFoundException("Patient not found.");
                });
    }
}