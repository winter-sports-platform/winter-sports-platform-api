package com.wintersports.repositories.result;

import com.wintersports.entities.result.SlalomResult;
import com.wintersports.repositories.IBaseRepository;

import java.util.List;

public interface ISlalomResultRepository extends IBaseRepository<SlalomResult, Long> {
    List<SlalomResult> findByCompetition_IdOrderByRun1TimeAsc(Long competitionId);
}
