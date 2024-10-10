package com.stts.doctorappointment.exception;

public class NoAvailableAppointmentException extends RuntimeException {
    public NoAvailableAppointmentException() {
    }

    public NoAvailableAppointmentException(String message) {
        super(message);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
