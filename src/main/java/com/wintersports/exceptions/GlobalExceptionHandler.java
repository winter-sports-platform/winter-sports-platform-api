package com.wintersports.exceptions;

import com.wintersports.exceptions.DuplicateResourceException.DuplicateResourceException;
import com.wintersports.exceptions.InvalidRegistrationException.InvalidRegistrationException;
import com.wintersports.exceptions.RegistrationClosedException.RegistrationClosedException;
import com.wintersports.exceptions.ResourceNotFoundException.ResourceNotFoundException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 400 - Invalid request body data (@Valid validation failed)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
        return ResponseEntity.badRequest().body(errors);
    }

    // 400 - Registration is closed (deadline has passed)
    @ExceptionHandler(RegistrationClosedException.class)
    public ResponseEntity<Map<String, String>> handleRegistrationClosed(
            RegistrationClosedException ex) {
        return ResponseEntity.status(400).body(Map.of("message", ex.getMessage()));
    }

    // 400 - Athlete does not meet competition requirements (age, gender)
    @ExceptionHandler(InvalidRegistrationException.class)
    public ResponseEntity<Map<String, String>> handleInvalidRegistration(
            InvalidRegistrationException ex) {
        return ResponseEntity.status(400).body(Map.of("message", ex.getMessage()));
    }

    // 404 - Requested resource not found
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(
            ResourceNotFoundException ex) {
        return ResponseEntity.status(404).body(Map.of("message", ex.getMessage()));
    }

    // 409 - Resource already exists (duplicate)
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<Map<String, String>> handleDuplicate(
            DuplicateResourceException ex) {
        return ResponseEntity.status(409).body(Map.of("message", ex.getMessage()));
    }

    // 500 - Unexpected server error
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneral(Exception ex) {
        return ResponseEntity.status(500).body(Map.of("message", "Internal server error"));
    }
}