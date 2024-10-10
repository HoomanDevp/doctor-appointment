package com.stts.doctorappointment.exception;

public class AppointmentNotFoundException extends RuntimeException {
    public AppointmentNotFoundException() {
    }
    public AppointmentNotFoundException(String message) {
        super(message);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
