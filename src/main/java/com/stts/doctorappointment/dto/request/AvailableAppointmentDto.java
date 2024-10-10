package com.stts.doctorappointment.dto.request;

import java.time.LocalDate;

public record AvailableAppointmentDto(LocalDate date) implements ApplicationRequest {
}
