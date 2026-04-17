package com.wintersports.dtos.responses;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
public class SlalomResultResponse extends CompetitionResultResponse {
    private BigDecimal run1Time;
    private BigDecimal run2Time;
    private BigDecimal totalTime;
}