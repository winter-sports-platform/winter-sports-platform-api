package com.wintersports.dtos.responses;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class BiathlonCompetitionResponse extends CompetitionResponse {
    private int lapsCount;
    private int shootingRounds;
    private int penaltySeconds;
}