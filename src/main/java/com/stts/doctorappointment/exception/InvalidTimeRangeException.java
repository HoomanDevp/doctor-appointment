package com.stts.doctorappointment.exception;

public class InvalidTimeRangeException extends RuntimeException {
    public InvalidTimeRangeException() {
    }

    public InvalidTimeRangeException(String message) {
        super(message);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
