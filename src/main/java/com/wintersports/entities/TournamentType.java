package com.wintersports.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "tournament_types")
@Data
@EqualsAndHashCode(callSuper = true)
public class TournamentType extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String name;
}