package com.stts.doctorappointment.dto.request;

import java.time.LocalDateTime;

public record AppointmentSpecifyDto(LocalDateTime startTime, LocalDateTime endTime) implements ApplicationRequest {
}
