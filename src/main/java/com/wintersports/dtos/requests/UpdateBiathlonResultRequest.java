package com.wintersports.dtos.requests;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateBiathlonResultRequest {

    @NotNull(message = "Ski time is required")
    @Positive(message = "Ski time must be positive")
    private BigDecimal skiTime;

    @Min(value = 0, message = "Missed shots cannot be negative")
    private int missedShots;

    private boolean finished;
}