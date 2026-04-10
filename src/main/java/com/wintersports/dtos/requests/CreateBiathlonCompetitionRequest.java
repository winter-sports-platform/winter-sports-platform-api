package com.wintersports.dtos.requests;

import com.wintersports.enums.Gender;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateBiathlonCompetitionRequest {

    @NotNull(message = "Tournament is required")
    private Long tournamentId;

    @NotBlank(message = "Name is required")
    @Size(min = 3, max = 100, message = "Name must be between 3 and 100 characters")
    private String name;

    @NotNull(message = "Gender is required")
    private Gender gender;

    @NotNull(message = "Min age is required")
    @Min(value = 16, message = "Min age must be at least 16")
    @Max(value = 60, message = "Min age must be at most 60")
    private Integer minAge;

    @NotNull(message = "Date is required")
    @Future(message = "Date must be in the future")
    private LocalDate date;

    @NotNull(message = "Registration deadline days is required")
    @Min(value = 1, message = "Registration deadline must be at least 1 day")
    private Integer registrationDeadlineDays;

    @NotNull(message = "Laps count is required")
    @Min(value = 1, message = "Laps count must be at least 1")
    private Integer lapsCount;

    @NotNull(message = "Shooting rounds is required")
    @Min(value = 1, message = "Shooting rounds must be at least 1")
    private Integer shootingRounds;

    @NotNull(message = "Penalty seconds is required")
    @Min(value = 1, message = "Penalty seconds must be at least 1")
    private Integer penaltySeconds;
}