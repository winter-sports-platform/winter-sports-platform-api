package com.wintersports.services.medal;

import com.wintersports.dtos.requests.CreateMedalRequest;
import com.wintersports.dtos.responses.MedalCountryResponse;
import com.wintersports.dtos.responses.MedalResponse;
import com.wintersports.enums.MedalType;

import java.util.List;

public interface IMedalService {
    List<MedalResponse> getAll();
    List<MedalCountryResponse> getByCountry();
    MedalResponse create(CreateMedalRequest request);
    List<MedalResponse> autoAssignAll(Long competitionId);
    void delete(Long id);
}