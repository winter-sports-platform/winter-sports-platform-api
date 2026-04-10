package com.wintersports.services.competition;

import com.wintersports.dtos.responses.CompetitionResponse;
import com.wintersports.services.IBaseService;

import java.util.List;

public interface ICompetitionService {
    List<CompetitionResponse> getAll();

    CompetitionResponse getById(Long id);

    void delete(Long id);
}