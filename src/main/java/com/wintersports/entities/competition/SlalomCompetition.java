package com.wintersports.entities.competition;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "slalom_competitions")
@Data
@EqualsAndHashCode(callSuper = true)
public class SlalomCompetition extends Competition {
    @Column(nullable = false)
    private int maxRun2Participants;
}