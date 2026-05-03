package com.wintersports.repositories.registration;

import com.wintersports.entities.Registration;
import com.wintersports.enums.RegistrationStatus;
import com.wintersports.repositories.IBaseRepository;

import java.util.List;

public interface IRegistrationRepository extends IBaseRepository<Registration, Long> {
    boolean existsByAthleteProfileIdAndCompetitionId(Long athleteProfileId, Long competitionId);
    boolean existsByAthleteProfileIdAndCompetitionIdAndStatus(Long athleteProfileId, Long competitionId, RegistrationStatus status);
    List<Registration> findByCompetitionId(Long competitionId);
    List<Registration> findByAthleteProfileId(Long athleteProfileId);
}