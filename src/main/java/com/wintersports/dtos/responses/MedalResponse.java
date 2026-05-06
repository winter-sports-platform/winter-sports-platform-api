package com.wintersports.dtos.responses;

import lombok.Data;

@Data
public class MedalResponse {
    private Long id;
    private CompetitionResultResponse result;
    private CompetitionResponse competition;
    private String type;
}