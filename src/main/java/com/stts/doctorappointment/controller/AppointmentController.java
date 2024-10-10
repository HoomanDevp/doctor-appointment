package com.stts.doctorappointment.controller;

import com.stts.doctorappointment.dto.request.AppointmentBookingDto;
import com.stts.doctorappointment.dto.request.AppointmentSpecifyDto;
import com.stts.doctorappointment.dto.response.*;
import com.stts.doctorappointment.model.Patient;
import com.stts.doctorappointment.service.AppointmentService;
import com.stts.doctorappointment.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Controller for managing appointments.
 */
@RestController
@RequestMapping("/appointment")
@RequiredArgsConstructor
public class AppointmentController {
    private final Logger logger = LoggerFactory.getLogger(AppointmentController.class);

    private final PatientService patientService;
    private final AppointmentService appointmentService;

    /**
     * Get all available appointments.
     *
     * @return ResponseEntity with available appointments
     */
    @GetMapping("/available-all-bookings")
    public ResponseEntity<ResponseData> availableAppointment() {
        logger.info("Getting all available bookings");
        List<AppointmentResponseDto> appointmentResponseDto = appointmentService.viewOpenAppointments()
                .stream()
                .map(AppointmentResponseDto::convert)
                .toList();

        return ResponseEntity.ok(new ResponseData(HttpStatus.OK.name(), new AvailableBookingsResponseDto(appointmentResponseDto)));
    }

    /**
     * Get available appointments for a specific day.
     *
     * @param date the date to check for available appointments
     * @return ResponseEntity with available appointments for the specified date
     */
    @GetMapping("/available-bookings")
    public ResponseEntity<ResponseData> checkOpenAppointmentForDay(@RequestParam("date") String date) {
        logger.info("Getting available booking for date: [{}]", date);
        List<AppointmentResponseDto> appointmentDto = appointmentService.viewOpenAppointments(LocalDate.parse(date))
                .stream()
                .map(AppointmentResponseDto::convert)
                .toList();

        return ResponseEntity.ok(new ResponseData(HttpStatus.OK.name(), new AvailableBookingsResponseDto(appointmentDto)));
    }

    /**
     * Get appointments for a specific patient.
     *
     * @param phone the phone number of the patient
     * @return ResponseEntity with the patient's appointments
     */
    @GetMapping("/patient-appointments")
    public ResponseEntity<ResponseData> patientAppointments(@RequestParam("phone") String phone) {
        logger.info("Getting appointments for patient with phone: [{}]", phone);

        List<AppointmentResponseDto> appointmentResponseDto = patientService.findByPhone(phone).getAppointments()
                .stream()
                .map(AppointmentResponseDto::convert)
                .toList();

        return ResponseEntity.ok(new ResponseData(HttpStatus.OK.name(), new AvailableBookingsResponseDto(appointmentResponseDto)));
    }

    /**
     * Add open appointment times.
     *
     * @param appointment the appointment details
     * @return ResponseEntity indicating the result of the operation
     */
    @PreAuthorize("hasRole('DOCTOR')")
    @PostMapping("/add-open-appointment")
    public ResponseEntity<ResponseData> registerAppointment(@RequestBody AppointmentSpecifyDto appointment) {
        logger.info("Opening appointment for reservation");
        appointmentService.addOpenTimes(appointment.startTime(), appointment.endTime());
        return ResponseEntity.ok(new ResponseData(HttpStatus.OK.name(), new EmptyDataResponseDto()));
    }

    /**
     * Get all reserved appointments.
     *
     * @return ResponseEntity with reserved appointments
     */
    @PreAuthorize("hasRole('DOCTOR')")
    @GetMapping("/reserved-appointment")
    public ResponseEntity<ResponseData> reservedAppointment() {
        logger.info("Getting reserved appointments");
        List<ReservedAppointment> appointmentDto = appointmentService.viewReservedAppointment().stream()
                .map(ReservedAppointment::convert)
                .toList();
        return ResponseEntity.ok(new ResponseData(HttpStatus.OK.name(), new ReservedAppointmentResponseDto(appointmentDto)));
    }

    /**
     * Book an open appointment.
     *
     * @param bookingDto the booking details
     * @return ResponseEntity indicating the result of the operation
     */
    @PostMapping("/book-appointment")
    public ResponseEntity<ResponseData> bookOpenAppointment(@Validated @RequestBody AppointmentBookingDto bookingDto) {
        logger.info("Booking appointment: [{}] for user: [{}]", bookingDto.appointmentId(), bookingDto.phone());
        Patient patient = new Patient();
        patient.setName(bookingDto.name());
        patient.setPhoneNumber(bookingDto.phone());

        appointmentService.bookAppointment(bookingDto.appointmentId(), patient);
        return ResponseEntity.ok(new ResponseData(HttpStatus.OK.name(), new EmptyDataResponseDto()));
    }

    /**
     * Delete an open appointment.
     *
     * @param appointmentId the ID of the appointment to delete
     * @return ResponseEntity indicating the result of the operation
     */
    @PreAuthorize("hasRole('DOCTOR')")
    @DeleteMapping("/delete-open-appointment/{id}")
    public ResponseEntity<ResponseData> deleteOpenAppointment(@PathVariable("id") Long appointmentId) {
        logger.info("Deleting open appointment: [{}]", appointmentId);
        appointmentService.deleteOpenAppointment(appointmentId);
        return ResponseEntity.ok(new ResponseData(HttpStatus.OK.name(), new EmptyDataResponseDto()));
    }
}