package com.wintersports.dtos.responses;

import lombok.Data;

import java.time.LocalDate;

@Data
public class TournamentResponse {
    private Long id;
    private String name;
    private int year;
    private String location;
    private LocalDate startDate;
    private LocalDate endDate;
    private TournamentTypeResponse type;
}