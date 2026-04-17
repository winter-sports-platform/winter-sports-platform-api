package com.wintersports.entities;

import com.wintersports.entities.competition.Competition;
import com.wintersports.enums.RegistrationStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Entity
@Table(name = "registrations",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_registration",
                columnNames = {"athlete_profile_id", "competition_id"}
        ))
@Data
@EqualsAndHashCode(callSuper = true)
public class Registration extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "athlete_profile_id", nullable = false)
    private AthleteProfile athleteProfile;

    @ManyToOne
    @JoinColumn(name = "competition_id", nullable = false)
    private Competition competition;

    @Column(nullable = false)
    private LocalDateTime registeredAt = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RegistrationStatus status = RegistrationStatus.PENDING;
}