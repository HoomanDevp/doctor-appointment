package com.stts.doctorappointment.exception;

public class AlreadyTakenAppointmentException extends RuntimeException {
    public AlreadyTakenAppointmentException() {
    }

    public AlreadyTakenAppointmentException(String message) {
        super(message);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
