package com.wintersports.dtos.responses;

import lombok.Data;

@Data
public class CompetitionResultResponse {
    private Long id;
    private AthleteProfileResponse athleteProfile;
    private CompetitionResponse competition;
    private boolean finished;
    private String type;
}