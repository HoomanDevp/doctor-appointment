package com.stts.doctorappointment.config;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.annotation.Priority;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@Priority(value = 1)
public class ValidationErrorHandler extends ResponseEntityExceptionHandler {
    private final Logger logger = LoggerFactory.getLogger(ValidationErrorHandler.class);

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        String requestURI = ((ServletWebRequest) request).getRequest().getRequestURI();

        Map<String, String> errors = new HashMap<>();
        logger.error("response of url: [{}] has code: [{}] - because of: ", requestURI, HttpStatus.BAD_REQUEST);
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            Object rejectedValue = ((FieldError) error).getRejectedValue();
            logger.error("Invalid input: [{}] for field: [{}] - message: [{}]", rejectedValue, fieldName, message);
            errors.put(fieldName, message);
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
}