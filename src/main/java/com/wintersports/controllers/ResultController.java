package com.wintersports.controllers;

import com.wintersports.dtos.requests.CreateBiathlonResultRequest;
import com.wintersports.dtos.requests.CreateSlalomResultRequest;
import com.wintersports.dtos.requests.UpdateBiathlonResultRequest;
import com.wintersports.dtos.requests.UpdateSlalomResultRequest;
import com.wintersports.dtos.responses.BiathlonResultResponse;
import com.wintersports.dtos.responses.CompetitionResultResponse;
import com.wintersports.dtos.responses.SlalomResultResponse;
import com.wintersports.services.result.BiathlonResultService;
import com.wintersports.services.result.CompetitionResultService;
import com.wintersports.services.result.SlalomResultService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/results")
@RequiredArgsConstructor
public class ResultController {

    private final CompetitionResultService competitionResultService;
    private final SlalomResultService slalomResultService;
    private final BiathlonResultService biathlonResultService;

    @GetMapping
    public ResponseEntity<List<CompetitionResultResponse>> getAll() {
        return ResponseEntity.ok(competitionResultService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CompetitionResultResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(competitionResultService.getById(id));
    }

    @GetMapping("/competition/{competitionId}")
    public ResponseEntity<List<CompetitionResultResponse>> getAllByCompetition(
            @PathVariable Long competitionId) {
        return ResponseEntity.ok(competitionResultService.getAllByCompetition(competitionId));
    }

    @PostMapping("/slalom")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SlalomResultResponse> createSlalom(
            @Valid @RequestBody CreateSlalomResultRequest request) {
        return ResponseEntity.status(201).body(slalomResultService.create(request));
    }

    @PutMapping("/slalom/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SlalomResultResponse> updateSlalom(
            @PathVariable Long id,
            @Valid @RequestBody UpdateSlalomResultRequest request) {
        return ResponseEntity.ok(slalomResultService.update(id, request));
    }

    @GetMapping("/slalom/{competitionId}/run2-qualifiers")
    public ResponseEntity<List<SlalomResultResponse>> getRun2Qualifiers(
            @PathVariable Long competitionId) {
        return ResponseEntity.ok(slalomResultService.getRun2Qualifiers(competitionId));
    }

    @PostMapping("/biathlon")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BiathlonResultResponse> createBiathlon(
            @Valid @RequestBody CreateBiathlonResultRequest request) {
        return ResponseEntity.status(201).body(biathlonResultService.create(request));
    }

    @PutMapping("/biathlon/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BiathlonResultResponse> updateBiathlon(
            @PathVariable Long id,
            @Valid @RequestBody UpdateBiathlonResultRequest request) {
        return ResponseEntity.ok(biathlonResultService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        competitionResultService.delete(id);
        return ResponseEntity.noContent().build();
    }
}