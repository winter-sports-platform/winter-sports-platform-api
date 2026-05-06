package com.wintersports.entities;

import com.wintersports.entities.competition.Competition;
import com.wintersports.entities.result.CompetitionResult;
import com.wintersports.enums.MedalType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "medals", uniqueConstraints = {
        @UniqueConstraint(name = "uq_medal_type_per_competition", columnNames = {"competition_id", "type"})
})
@Getter
@Setter
public class Medal extends BaseEntity {
    @OneToOne
    @JoinColumn(name = "result_id", nullable = false, unique = true)
    private CompetitionResult result;

    @ManyToOne
    @JoinColumn(name = "competition_id", nullable = false)
    private Competition competition;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MedalType type;
}
