package com.wintersports.services.tournamenttype;

import com.wintersports.dtos.requests.CreateTournamentTypeRequest;
import com.wintersports.dtos.responses.TournamentTypeResponse;
import com.wintersports.services.IBaseService;

public interface ITournamentTypeService extends IBaseService<
        TournamentTypeResponse,
        CreateTournamentTypeRequest,
        CreateTournamentTypeRequest,
        Long> {
}