package com.wintersports.controllers;

import com.wintersports.dtos.requests.CreateTournamentRequest;
import com.wintersports.dtos.responses.TournamentResponse;
import com.wintersports.services.tournament.TournamentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tournaments")
@RequiredArgsConstructor
public class TournamentController {
    private final TournamentService tournamentService;

    @GetMapping
    public ResponseEntity<List<TournamentResponse>> getAll() {
        return ResponseEntity.ok(tournamentService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TournamentResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(tournamentService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TournamentResponse> create(
            @Valid @RequestBody CreateTournamentRequest request) {
        return ResponseEntity.status(201).body(tournamentService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TournamentResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody CreateTournamentRequest request) {
        return ResponseEntity.ok(tournamentService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        tournamentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
