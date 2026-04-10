package com.wintersports.entities.competition;

import com.wintersports.entities.BaseEntity;
import com.wintersports.entities.Tournament;
import com.wintersports.enums.Gender;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Entity
@Table(name = "competitions")
@Data
@EqualsAndHashCode(callSuper = true)
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Competition extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "tournament_id", nullable = false)
    private Tournament tournament;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    @Column(nullable = false)
    private int minAge;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private int registrationDeadlineDays;
}
