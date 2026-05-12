package com.wintersports.repositories.medal;

import com.wintersports.entities.Medal;
import com.wintersports.repositories.IBaseRepository;

import java.util.List;

public interface IMedalRepository extends IBaseRepository<Medal, Long> {
    List<Medal> findByCompetitionId(Long competitionId);
    boolean existsByCompetitionIdAndType(Long competitionId, com.wintersports.enums.MedalType type);
    boolean existsByCompetitionId(Long competitionId);
}