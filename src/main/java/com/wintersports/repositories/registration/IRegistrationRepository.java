package com.wintersports.repositories.registration;

import com.wintersports.entities.Registration;
import com.wintersports.repositories.IBaseRepository;

import java.util.List;

public interface IRegistrationRepository extends IBaseRepository<Registration, Long> {
    boolean existsByAthleteProfileIdAndCompetitionId(Long athleteProfileId, Long competitionId);
    List<Registration> findByCompetitionId(Long competitionId);
}