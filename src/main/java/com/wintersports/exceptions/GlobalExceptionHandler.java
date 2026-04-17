package com.wintersports.exceptions;

import com.wintersports.exceptions.AccountNotApprovedException.AccountNotApprovedException;
import com.wintersports.exceptions.DuplicateResourceException.DuplicateResourceException;
import com.wintersports.exceptions.InvalidCompetitionDateException.InvalidCompetitionDateException;
import com.wintersports.exceptions.InvalidCredentialsException.InvalidCredentialsException;
import com.wintersports.exceptions.InvalidRegistrationException.InvalidRegistrationException;
import com.wintersports.exceptions.RegistrationClosedException.RegistrationClosedException;
import com.wintersports.exceptions.ResourceNotFoundException.ResourceNotFoundException;

import com.wintersports.exceptions.UnauthorizedAccessException.UnauthorizedAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

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

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Map<String, String>> handleNoResourceFound(NoResourceFoundException ex) {
        return ResponseEntity.status(404).body(Map.of("message", "Endpoint not found"));
    }

    // 409 - Resource already exists (duplicate)
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<Map<String, String>> handleDuplicate(
            DuplicateResourceException ex) {
        return ResponseEntity.status(409).body(Map.of("message", ex.getMessage()));
    }

    // 400 - Invalid path variable type (e.g. /api/tournaments/test instead of /api/tournaments/1)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, String>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        return ResponseEntity.status(400).body(Map.of("message", "Invalid id format"));
    }

    // 400 - Competition date is outside tournament date range
    @ExceptionHandler(InvalidCompetitionDateException.class)
    public ResponseEntity<Map<String, String>> handleInvalidCompetitionDate(InvalidCompetitionDateException ex) {
        return ResponseEntity.status(400).body(Map.of("message", ex.getMessage()));
    }

    // 401 - Invalid credentials
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<Map<String, String>> handleInvalidCredentials(
            InvalidCredentialsException ex) {
        return ResponseEntity.status(401).body(Map.of("message", ex.getMessage()));
    }

    // 403 - Account is not yet approved by admin
    @ExceptionHandler(AccountNotApprovedException.class)
    public ResponseEntity<Map<String, String>> handleAccountNotApproved(
            AccountNotApprovedException ex) {
        return ResponseEntity.status(403).body(Map.of("message", ex.getMessage()));
    }

    // 403 - Access denied
    @ExceptionHandler(UnauthorizedAccessException.class)
    public ResponseEntity<Map<String, String>> handleAccessDenied(UnauthorizedAccessException ex) {
        return ResponseEntity.status(403).body(Map.of("message", ex.getMessage()));
    }

    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ResponseEntity<Map<String, String>> handleSpringAccessDenied(
            org.springframework.security.access.AccessDeniedException ex) {
        return ResponseEntity.status(403).body(Map.of("message", "Access denied"));
    }

    // 500 - Unexpected server error
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneral(Exception ex) {
        return ResponseEntity.status(500).body(Map.of("message", "Internal server error"));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handleNotReadable(HttpMessageNotReadableException ex) {
        return ResponseEntity.status(400).body(Map.of("message", "Invalid value for enum field"));
    }
}