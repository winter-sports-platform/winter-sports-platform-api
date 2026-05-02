package com.wintersports.repositories.tournament;

import com.wintersports.entities.Tournament;
import com.wintersports.repositories.IBaseRepository;

public interface ITournamentRepository extends IBaseRepository<Tournament, Long> {
    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, Long id);
}