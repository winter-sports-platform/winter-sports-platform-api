package com.wintersports.dtos.requests;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateSlalomResultRequest {

    @NotNull(message = "Athlete profile id is required")
    private Long athleteProfileId;

    @NotNull(message = "Competition id is required")
    private Long competitionId;

    @Positive(message = "Run 1 time must be positive")
    private BigDecimal run1Time;
}