package com.wintersports.dtos.responses;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SlalomCompetitionResponse extends CompetitionResponse {
    private int maxRun2Participants;
}