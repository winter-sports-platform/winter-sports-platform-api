package com.wintersports.services.statistics;

import com.wintersports.dtos.responses.AthleteProfileResponse;
import com.wintersports.dtos.responses.CompetitionResultResponse;
import com.wintersports.dtos.responses.MedalCountryResponse;

import java.util.List;
import java.util.Map;

public interface IStatisticsService {
    List<CompetitionResultResponse> getRankings(Long competitionId);
    List<MedalCountryResponse> getMedalsByCountry();
    double getAverageAge(Long competitionId);
    Map<String, AthleteProfileResponse> getYoungestOldestMedalist();
}