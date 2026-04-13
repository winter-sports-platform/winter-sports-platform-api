package com.wintersports.controllers;

import com.wintersports.dtos.requests.LoginRequest;
import com.wintersports.dtos.requests.RegisterAdminRequest;
import com.wintersports.dtos.requests.RegisterAthleteRequest;
import com.wintersports.dtos.responses.AuthResponse;
import com.wintersports.services.auth.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register/athlete")
    public ResponseEntity<AuthResponse> registerAthlete(@Valid @RequestBody RegisterAthleteRequest request) {
        return ResponseEntity.status(201).body(authService.registerAthlete(request));
    }

    @PostMapping("/register/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AuthResponse> registerAdmin(@Valid @RequestBody RegisterAdminRequest request) {
        return ResponseEntity.status(201).body(authService.registerAdmin(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}