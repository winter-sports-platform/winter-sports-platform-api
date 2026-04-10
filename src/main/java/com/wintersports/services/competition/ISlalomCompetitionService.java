package com.wintersports.services.competition;

import com.wintersports.dtos.requests.CreateSlalomCompetitionRequest;
import com.wintersports.dtos.responses.SlalomCompetitionResponse;

public interface ISlalomCompetitionService {
    SlalomCompetitionResponse create(CreateSlalomCompetitionRequest request);
    SlalomCompetitionResponse update(Long id, CreateSlalomCompetitionRequest request);
}