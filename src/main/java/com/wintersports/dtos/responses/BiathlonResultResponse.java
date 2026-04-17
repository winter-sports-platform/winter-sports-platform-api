package com.wintersports.dtos.responses;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
public class BiathlonResultResponse extends CompetitionResultResponse {
    private BigDecimal skiTime;
    private int missedShots;
    private BigDecimal penaltyTime;
    private BigDecimal totalTime;
}