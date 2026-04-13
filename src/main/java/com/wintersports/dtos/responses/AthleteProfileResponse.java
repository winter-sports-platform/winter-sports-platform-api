package com.wintersports.dtos.responses;

import com.wintersports.enums.Gender;
import lombok.Data;

import java.time.LocalDate;

@Data
public class AthleteProfileResponse {
    private Long id;
    private String name;
    private String country;
    private Gender gender;
    private LocalDate dateOfBirth;
}