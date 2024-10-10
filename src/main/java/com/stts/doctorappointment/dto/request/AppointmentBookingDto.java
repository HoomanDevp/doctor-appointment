package com.stts.doctorappointment.dto.request;

import javax.validation.constraints.NotNull;

public record AppointmentBookingDto(
        @NotNull(message = "name could not be null!") String name,
        @NotNull(message = "phone could not be null!") String phone,
        @NotNull(message = "appointmentId could not be null!") Long appointmentId
) implements ApplicationRequest {

}