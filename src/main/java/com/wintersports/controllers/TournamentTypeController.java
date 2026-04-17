package com.wintersports.controllers;

import com.wintersports.dtos.requests.CreateTournamentTypeRequest;
import com.wintersports.dtos.responses.TournamentTypeResponse;
import com.wintersports.services.tournamenttype.ITournamentTypeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tournament-types")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class TournamentTypeController {

    private final ITournamentTypeService tournamentTypeService;

    @GetMapping
    public ResponseEntity<List<TournamentTypeResponse>> getAll() {
        return ResponseEntity.ok(tournamentTypeService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TournamentTypeResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(tournamentTypeService.getById(id));
    }

    @PostMapping
    public ResponseEntity<TournamentTypeResponse> create(
            @Valid @RequestBody CreateTournamentTypeRequest request) {
        return ResponseEntity.status(201).body(tournamentTypeService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TournamentTypeResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody CreateTournamentTypeRequest request) {
        return ResponseEntity.ok(tournamentTypeService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        tournamentTypeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}