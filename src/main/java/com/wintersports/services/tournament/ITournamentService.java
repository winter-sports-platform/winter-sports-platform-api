package com.wintersports.services.tournament;

import com.wintersports.dtos.requests.CreateTournamentRequest;
import com.wintersports.dtos.responses.TournamentResponse;
import com.wintersports.services.IBaseService;

public interface ITournamentService extends IBaseService<
        TournamentResponse,
        CreateTournamentRequest,
        CreateTournamentRequest,
        Long>{
}