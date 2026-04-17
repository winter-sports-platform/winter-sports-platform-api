package com.wintersports.dtos.responses;

import com.wintersports.enums.RegistrationStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RegistrationItemResponse {
    private Long id;
    private CompetitionResponse competition;
    private LocalDateTime registeredAt;
    private RegistrationStatus status;
}