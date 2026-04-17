package com.wintersports.controllers;

import com.wintersports.dtos.requests.UpdateAthleteProfileRequest;
import com.wintersports.dtos.responses.AthleteProfileResponse;
import com.wintersports.services.athleteprofile.AthleteProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/athletes")
@RequiredArgsConstructor
public class AthleteProfileController {

    private final AthleteProfileService athleteProfileService;

    @GetMapping
    public ResponseEntity<List<AthleteProfileResponse>> getAll() {
        return ResponseEntity.ok(athleteProfileService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AthleteProfileResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(athleteProfileService.getById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ATHLETE')")
    public ResponseEntity<AthleteProfileResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateAthleteProfileRequest request) {
        return ResponseEntity.ok(athleteProfileService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        athleteProfileService.delete(id);
        return ResponseEntity.noContent().build();
    }
}