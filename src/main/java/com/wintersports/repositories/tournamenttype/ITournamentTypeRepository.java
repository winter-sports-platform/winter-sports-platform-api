package com.wintersports.repositories.tournamenttype;

import com.wintersports.entities.TournamentType;
import com.wintersports.repositories.IBaseRepository;

public interface ITournamentTypeRepository extends IBaseRepository<TournamentType, Long> {
    boolean existsByName(String name);
}