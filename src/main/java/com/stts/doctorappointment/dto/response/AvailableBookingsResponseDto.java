package com.stts.doctorappointment.dto.response;

import java.util.List;

public record AvailableBookingsResponseDto(List<AppointmentResponseDto> appointment) implements ApplicationResponse {
}
