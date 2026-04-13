package com.wintersports.dtos.responses;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RegistrationResponse {
    private Long id;
    private AthleteProfileResponse athleteProfile;
    private CompetitionResponse competition;
    private LocalDateTime registeredAt;
    private String status;
}