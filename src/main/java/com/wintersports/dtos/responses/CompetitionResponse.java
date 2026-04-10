package com.wintersports.dtos.responses;

import com.wintersports.enums.Gender;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CompetitionResponse {
    private Long id;
    private TournamentResponse tournament;
    private String name;
    private String type;
    private Gender gender;
    private int minAge;
    private LocalDate date;
    private int registrationDeadlineDays;
}