package com.wintersports.controllers;

import com.wintersports.dtos.requests.CreateRegistrationRequest;
import com.wintersports.dtos.requests.UpdateRegistrationStatusRequest;
import com.wintersports.dtos.responses.RegistrationResponse;
import com.wintersports.services.registration.RegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/registrations")
@RequiredArgsConstructor
public class RegistrationController {

    private final RegistrationService registrationService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<RegistrationResponse>> getAll() {
        return ResponseEntity.ok(registrationService.getAll());
    }

    @PostMapping
    @PreAuthorize("hasRole('ATHLETE')")
    public ResponseEntity<RegistrationResponse> create(
            @Valid @RequestBody CreateRegistrationRequest request) {
        return ResponseEntity.status(201).body(registrationService.create(request));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RegistrationResponse> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateRegistrationStatusRequest request) {
        return ResponseEntity.ok(registrationService.updateStatus(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        registrationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}