package com.wintersports.services.result;

import com.wintersports.dtos.responses.CompetitionResultResponse;

import java.util.List;

public interface ICompetitionResultService {
    List<CompetitionResultResponse> getAll();

    CompetitionResultResponse getById(Long id);

    List<CompetitionResultResponse> getAllByCompetition(Long competitionId);

    void delete(Long id);
}
