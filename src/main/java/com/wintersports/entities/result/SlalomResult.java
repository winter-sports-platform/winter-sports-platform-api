package com.wintersports.entities.result;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;


@Entity
@Table(name = "slalom_results")
@Data
@EqualsAndHashCode(callSuper = true)
public class SlalomResult extends CompetitionResult {

    @Column(precision = 10, scale = 3)
    private BigDecimal run1Time;

    @Column(precision = 10, scale = 3)
    private BigDecimal run2Time;

    @Column(precision = 10, scale = 3)
    private BigDecimal totalTime;
}