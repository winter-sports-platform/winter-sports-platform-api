package com.wintersports.dtos.requests;

import com.wintersports.enums.MedalType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateMedalRequest {
    @NotNull(message = "Result is required")
    private Long resultId;

    @NotNull(message = "Competition is required")
    private Long competitionId;

    @NotNull(message = "Medal type is required")
    private MedalType type;
}