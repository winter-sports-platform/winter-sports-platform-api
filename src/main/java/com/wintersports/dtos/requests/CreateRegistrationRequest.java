package com.wintersports.dtos.requests;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateRegistrationRequest {

    @NotNull(message = "Competition id is required")
    private Long competitionId;
}