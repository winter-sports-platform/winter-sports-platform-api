package com.wintersports.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Entity
@Table(name = "biathlon_results")
@Data
@EqualsAndHashCode(callSuper = true)
public class BiathlonResult extends CompetitionResult {

    @Column(nullable = false, precision = 10, scale = 3)
    private BigDecimal skiTime;

    @Column(nullable = false)
    private int missedShots = 0;

    @Column(nullable = false, precision = 10, scale = 3)
    private BigDecimal penaltyTime = BigDecimal.ZERO;

    @Column(nullable = false, precision = 10, scale = 3)
    private BigDecimal totalTime;
}