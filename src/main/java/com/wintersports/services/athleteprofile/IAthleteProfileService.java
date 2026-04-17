package com.wintersports.services.athleteprofile;

import com.wintersports.dtos.requests.UpdateAthleteProfileRequest;
import com.wintersports.dtos.responses.AthleteProfileResponse;

import java.util.List;

public interface IAthleteProfileService {
    List<AthleteProfileResponse> getAll();
    AthleteProfileResponse getById(Long id);
    AthleteProfileResponse update(Long id, UpdateAthleteProfileRequest request);
    void delete(Long id);
}