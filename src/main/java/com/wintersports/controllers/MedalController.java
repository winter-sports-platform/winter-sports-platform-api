package com.wintersports.controllers;

import com.wintersports.dtos.requests.CreateMedalRequest;
import com.wintersports.dtos.responses.MedalCountryResponse;
import com.wintersports.dtos.responses.MedalResponse;
import com.wintersports.services.medal.IMedalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medals")
@RequiredArgsConstructor
public class MedalController {

    private final IMedalService medalService;

    @GetMapping
    public ResponseEntity<List<MedalResponse>> getAll() {
        return ResponseEntity.ok(medalService.getAll());
    }

    @GetMapping("/by-country")
    public ResponseEntity<List<MedalCountryResponse>> getByCountry() {
        return ResponseEntity.ok(medalService.getByCountry());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MedalResponse> create(@Valid @RequestBody CreateMedalRequest request) {
        return ResponseEntity.status(201).body(medalService.create(request));
    }

    @PostMapping("/auto-assign/{competitionId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<MedalResponse>> autoAssign(@PathVariable Long competitionId) {
        return ResponseEntity.status(201).body(medalService.autoAssignAll(competitionId));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        medalService.delete(id);
        return ResponseEntity.noContent().build();
    }
}