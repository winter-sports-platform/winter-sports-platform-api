package com.wintersports.repositories.result;

import com.wintersports.entities.result.CompetitionResult;
import com.wintersports.repositories.IBaseRepository;

import java.util.List;

public interface ICompetitionResultRepository extends IBaseRepository<CompetitionResult, Long> {
    boolean existsByAthleteProfileIdAndCompetitionId(Long athleteProfileId, Long competitionId);
    List<CompetitionResult> findByCompetitionId(Long competitionId);
    boolean existsByCompetitionId(Long competitionId);
}