package com.wintersports.repositories.competition;

import com.wintersports.entities.competition.Competition;
import com.wintersports.repositories.IBaseRepository;

public interface ICompetitionRepository extends IBaseRepository<Competition, Long> {
    boolean existsByNameAndTournamentId(String name, Long tournamentId);
}