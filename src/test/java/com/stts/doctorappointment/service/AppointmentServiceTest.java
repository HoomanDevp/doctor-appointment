package com.stts.doctorappointment.service;

import com.stts.doctorappointment.exception.AlreadyTakenAppointmentException;
import com.stts.doctorappointment.exception.NoAvailableAppointmentException;
import com.stts.doctorappointment.model.Appointment;
import com.stts.doctorappointment.model.Patient;
import com.stts.doctorappointment.repository.AppointmentRepository;
import com.stts.doctorappointment.repository.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.persistence.OptimisticLockException;
import java.time.LocalDateTime;
import java.util.ConcurrentModificationException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AppointmentServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private AppointmentRepository appointmentRepository;

    @InjectMocks
    private AppointmentService appointmentService;

    private Patient patient;
    private Appointment appointment;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        patient = new Patient();
        patient.setId(1L);
        patient.setPhoneNumber("1234567890");

        appointment = new Appointment();
        appointment.setId(1L);
    }

    @Test
    void testAddOpenTimes() {
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = startTime.plusHours(2);

        appointmentService.addOpenTimes(startTime, endTime);

        verify(appointmentRepository, times(4)).save(any(Appointment.class));
    }

    @Test
    void testBookAppointment() {
        appointment.setTaken(false);

        when(patientRepository.findByPhoneNumber(anyString())).thenReturn(Optional.of(patient));
        when(appointmentRepository.findById(appointment.getId())).thenReturn(Optional.of(appointment));
        when(patientRepository.findById(patient.getId())).thenReturn(Optional.of(patient));

        appointmentService.bookAppointment(appointment.getId(), patient);

        assertTrue(appointment.isTaken());
        assertEquals(patient, appointment.getPatient());
        verify(appointmentRepository, times(1)).save(appointment);
    }

    @Test
    void testBookAppointmentAlreadyTaken() {
        appointment.setTaken(true);

        when(appointmentRepository.findById(appointment.getId())).thenReturn(Optional.of(appointment));
        when(patientRepository.findByPhoneNumber(anyString())).thenReturn(Optional.of(patient));
        when(patientRepository.findById(patient.getId())).thenReturn(Optional.of(patient));

        assertThrows(AlreadyTakenAppointmentException.class, () -> {
            appointmentService.bookAppointment(appointment.getId(), patient);
        });
    }

    @Test
    void testBookAppointmentOptimisticLockException() {
        appointment.setTaken(false);

        when(patientRepository.findByPhoneNumber(anyString())).thenReturn(Optional.of(patient));
        when(appointmentRepository.findById(appointment.getId())).thenReturn(Optional.of(appointment));
        when(patientRepository.findById(patient.getId())).thenReturn(Optional.of(patient));
        doThrow(new OptimisticLockException()).when(appointmentRepository).save(any(Appointment.class));

        assertThrows(ConcurrentModificationException.class, () -> {
            appointmentService.bookAppointment(appointment.getId(), patient);
        });
    }

    @Test
    void testDeleteOpenAppointment() {
        appointment.setTaken(false);

        when(appointmentRepository.count()).thenReturn(1L);
        when(appointmentRepository.findById(appointment.getId())).thenReturn(Optional.of(appointment));

        appointmentService.deleteOpenAppointment(appointment.getId());

        verify(appointmentRepository, times(1)).delete(appointment);
    }

    @Test
    void testDeleteOpenAppointmentNoAvailable() {
        when(appointmentRepository.count()).thenReturn(0L);

        assertThrows(NoAvailableAppointmentException.class, () -> {
            appointmentService.deleteOpenAppointment(1L);
        });
    }

    @Test
    void testDeleteOpenAppointmentAlreadyTaken() {
        appointment.setTaken(true);

        when(appointmentRepository.count()).thenReturn(1L);
        when(appointmentRepository.findById(appointment.getId())).thenReturn(Optional.of(appointment));

        assertThrows(AlreadyTakenAppointmentException.class, () -> {
            appointmentService.deleteOpenAppointment(appointment.getId());
        });
    }
}