package com.wintersports.entities.result;

import com.wintersports.entities.AthleteProfile;
import com.wintersports.entities.BaseEntity;
import com.wintersports.entities.competition.Competition;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "competition_results",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_result",
                columnNames = {"athlete_profile_id", "competition_id"}
        ))
@Data
@EqualsAndHashCode(callSuper = true)
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class CompetitionResult extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "athlete_profile_id", nullable = false)
    private AthleteProfile athleteProfile;

    @ManyToOne
    @JoinColumn(name = "competition_id", nullable = false)
    private Competition competition;

    @Column(nullable = false)
    private boolean finished = false;

    @Column(nullable = false)
    private String type;
}