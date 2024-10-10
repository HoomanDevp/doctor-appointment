package com.stts.doctorappointment.dto.response;

import com.stts.doctorappointment.model.Appointment;

public record ReservedAppointment(String name, String phone, AppointmentResponseDto appointment)
        implements ApplicationResponse {
    public static ReservedAppointment convert(Appointment appointment) {
        return new ReservedAppointment(
                appointment.getPatient().getName(),
                appointment.getPatient().getPhoneNumber(),
                AppointmentResponseDto.convert(appointment)
        );
    }
}
