package com.wintersports.controllers;

import com.wintersports.dtos.responses.AthleteProfileResponse;
import com.wintersports.dtos.responses.CompetitionResultResponse;
import com.wintersports.dtos.responses.MedalCountryResponse;
import com.wintersports.services.statistics.IStatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final IStatisticsService statisticsService;

    @GetMapping("/rankings/{competitionId}")
    public ResponseEntity<List<CompetitionResultResponse>> getRankings(
            @PathVariable Long competitionId) {
        return ResponseEntity.ok(statisticsService.getRankings(competitionId));
    }

    @GetMapping("/medals-by-country")
    public ResponseEntity<List<MedalCountryResponse>> getMedalsByCountry() {
        return ResponseEntity.ok(statisticsService.getMedalsByCountry());
    }

    @GetMapping("/average-age/{competitionId}")
    public ResponseEntity<Map<String, Double>> getAverageAge(
            @PathVariable Long competitionId) {
        return ResponseEntity.ok(Map.of("averageAge", statisticsService.getAverageAge(competitionId)));
    }

    @GetMapping("/youngest-oldest-medalist")
    public ResponseEntity<Map<String, AthleteProfileResponse>> getYoungestOldestMedalist() {
        return ResponseEntity.ok(statisticsService.getYoungestOldestMedalist());
    }
}