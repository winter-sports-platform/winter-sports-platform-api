package com.wintersports.controllers;

import com.wintersports.dtos.requests.CreateBiathlonCompetitionRequest;
import com.wintersports.dtos.requests.CreateSlalomCompetitionRequest;
import com.wintersports.dtos.responses.BiathlonCompetitionResponse;
import com.wintersports.dtos.responses.CompetitionResponse;
import com.wintersports.dtos.responses.SlalomCompetitionResponse;
import com.wintersports.services.competition.IBiathlonCompetitionService;
import com.wintersports.services.competition.ICompetitionService;
import com.wintersports.services.competition.ISlalomCompetitionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/competitions")
@RequiredArgsConstructor
public class CompetitionController {

    private final ICompetitionService competitionService;
    private final ISlalomCompetitionService slalomCompetitionService;
    private final IBiathlonCompetitionService biathlonCompetitionService;

    @GetMapping
    public ResponseEntity<List<CompetitionResponse>> getAll() {
        return ResponseEntity.ok(competitionService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CompetitionResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(competitionService.getById(id));
    }

    @PostMapping("/slalom")
    public ResponseEntity<SlalomCompetitionResponse> createSlalom(
            @Valid @RequestBody CreateSlalomCompetitionRequest request) {
        return ResponseEntity.status(201).body(slalomCompetitionService.create(request));
    }

    @PostMapping("/biathlon")
    public ResponseEntity<BiathlonCompetitionResponse> createBiathlon(
            @Valid @RequestBody CreateBiathlonCompetitionRequest request) {
        return ResponseEntity.status(201).body(biathlonCompetitionService.create(request));
    }

    @PutMapping("/slalom/{id}")
    public ResponseEntity<SlalomCompetitionResponse> updateSlalom(
            @PathVariable Long id,
            @Valid @RequestBody CreateSlalomCompetitionRequest request) {
        return ResponseEntity.ok(slalomCompetitionService.update(id, request));
    }

    @PutMapping("/biathlon/{id}")
    public ResponseEntity<BiathlonCompetitionResponse> updateBiathlon(
            @PathVariable Long id,
            @Valid @RequestBody CreateBiathlonCompetitionRequest request) {
        return ResponseEntity.ok(biathlonCompetitionService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        competitionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}