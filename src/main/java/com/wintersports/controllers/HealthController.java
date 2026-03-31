package com.wintersports.controllers.health;

import com.wintersports.entities.Health;
import com.wintersports.services.health.IHealthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/health")
@RequiredArgsConstructor
public class HealthController {

    private final IHealthService healthService;

    @GetMapping
    public ResponseEntity<List<Health>> getAll() {
        return ResponseEntity.ok(healthService.getAll());
    }

    @PostMapping
    public ResponseEntity<Health> create(@RequestBody Health health) {
        return ResponseEntity.status(201).body(healthService.create(health));
    }
}