package com.stts.doctorappointment.dto.response;

import java.util.List;

public record ReservedAppointmentResponseDto(List<ReservedAppointment> reservedAppointments)
        implements ApplicationResponse {
}