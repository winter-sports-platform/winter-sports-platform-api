package com.wintersports.entities.competition;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "biathlon_competitions")
@Data
@EqualsAndHashCode(callSuper = true)
public class BiathlonCompetition extends Competition {
    @Column(nullable = false)
    private int lapsCount;

    @Column(nullable = false)
    private int shootingRounds;

    @Column(nullable = false)
    private int penaltySeconds;
}