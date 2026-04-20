package com.wintersports.dtos.requests;

import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateSlalomResultRequest {

    @Positive(message = "Run 2 time must be positive")
    private BigDecimal run2Time;
}