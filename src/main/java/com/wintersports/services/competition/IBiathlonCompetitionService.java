package com.wintersports.services.competition;

import com.wintersports.dtos.requests.CreateBiathlonCompetitionRequest;
import com.wintersports.dtos.responses.BiathlonCompetitionResponse;

public interface IBiathlonCompetitionService {
    BiathlonCompetitionResponse create(CreateBiathlonCompetitionRequest request);
    BiathlonCompetitionResponse update(Long id, CreateBiathlonCompetitionRequest request);
}
