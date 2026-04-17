package com.wintersports.repositories.result;

import com.wintersports.entities.result.CompetitionResult;
import com.wintersports.repositories.IBaseRepository;

public interface ICompetitionResultRepository extends IBaseRepository<CompetitionResult, Long> {
    boolean existsByAthleteProfileIdAndCompetitionId(Long athleteProfileId, Long competitionId);
}