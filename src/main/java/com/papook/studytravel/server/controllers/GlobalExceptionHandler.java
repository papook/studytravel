package com.papook.studytravel.server.controllers;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.papook.studytravel.server.errors.ErrorMessage;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorMessage> handleException(Exception ex) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        ErrorMessage errorMessage = new ErrorMessage(
                status.value(),
                "Could not read the request body. Please provide a valid JSON object.");

        return ResponseEntity.status(status)
                .body(errorMessage);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorMessage> handleException(ConstraintViolationException ex) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        ErrorMessage errorMessage = new ErrorMessage(
                status.value(),
                "The request body contains invalid data. Make sure all fields are provided and have the correct format.");

        return ResponseEntity.status(status)
                .body(errorMessage);
    }
}