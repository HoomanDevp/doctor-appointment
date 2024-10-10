package com.stts.doctorappointment.dto.response;

import com.stts.doctorappointment.model.Appointment;

import java.time.LocalDateTime;

public record AppointmentResponseDto(
        Long id,
        LocalDateTime startTime,
        LocalDateTime endTime
) implements ApplicationResponse {
    public static AppointmentResponseDto convert(Appointment appointment) {
        return new AppointmentResponseDto(appointment.getId(), appointment.getStartTime(), appointment.getEndTime());
    }
}
