package com.wintersports.dtos.requests;

import com.wintersports.enums.RegistrationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateRegistrationStatusRequest {

    @NotNull(message = "Status is required")
    private RegistrationStatus status;
}