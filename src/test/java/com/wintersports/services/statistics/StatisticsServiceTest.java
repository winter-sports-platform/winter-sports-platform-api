package com.wintersports.services.statistics;

import com.wintersports.dtos.responses.AthleteProfileResponse;
import com.wintersports.dtos.responses.CompetitionResultResponse;
import com.wintersports.dtos.responses.MedalCountryResponse;
import com.wintersports.entities.AthleteProfile;
import com.wintersports.entities.Medal;
import com.wintersports.entities.result.SlalomResult;
import com.wintersports.enums.Gender;
import com.wintersports.enums.MedalType;
import com.wintersports.exceptions.ResourceNotFoundException.ResourceNotFoundException;
import com.wintersports.repositories.competition.ICompetitionRepository;
import com.wintersports.repositories.medal.IMedalRepository;
import com.wintersports.repositories.result.ICompetitionResultRepository;
import com.wintersports.services.medal.IMedalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StatisticsServiceTest {

    @Mock
    private ICompetitionResultRepository competitionResultRepository;

    @Mock
    private ICompetitionRepository competitionRepository;

    @Mock
    private IMedalRepository medalRepository;

    @Mock
    private IMedalService medalService;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private StatisticsService statisticsService;

    private AthleteProfile athleteProfile;
    private SlalomResult slalomResult;
    private Medal medal;

    @BeforeEach
    void setUp() {
        athleteProfile = new AthleteProfile();
        athleteProfile.setName("Ivan Ivanov");
        athleteProfile.setCountry("Bulgaria");
        athleteProfile.setGender(Gender.MALE);
        athleteProfile.setDateOfBirth(LocalDate.of(1995, 5, 15));

        slalomResult = new SlalomResult();
        slalomResult.setId(1L);
        slalomResult.setAthleteProfile(athleteProfile);
        slalomResult.setRun1Time(BigDecimal.valueOf(45.234));
        slalomResult.setRun2Time(BigDecimal.valueOf(48.132));
        slalomResult.setTotalTime(BigDecimal.valueOf(93.366));
        slalomResult.setFinished(true);

        medal = new Medal();
        medal.setId(1L);
        medal.setResult(slalomResult);
        medal.setType(MedalType.GOLD);
    }

    @Test
    void getRankings_success() {
        when(competitionRepository.existsById(1L)).thenReturn(true);
        when(competitionResultRepository.findByCompetitionId(1L)).thenReturn(List.of(slalomResult));
        when(modelMapper.map(any(), eq(CompetitionResultResponse.class))).thenReturn(new CompetitionResultResponse());

        List<CompetitionResultResponse> result = statisticsService.getRankings(1L);

        assertEquals(1, result.size());
    }

    @Test
    void getRankings_competitionNotFound_throwsResourceNotFoundException() {
        when(competitionRepository.existsById(999L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> statisticsService.getRankings(999L));
    }

    @Test
    void getRankings_emptyResults() {
        when(competitionRepository.existsById(1L)).thenReturn(true);
        when(competitionResultRepository.findByCompetitionId(1L)).thenReturn(List.of());

        List<CompetitionResultResponse> result = statisticsService.getRankings(1L);

        assertTrue(result.isEmpty());
    }

    @Test
    void getMedalsByCountry_delegatesToMedalService() {
        when(medalService.getByCountry()).thenReturn(List.of(
                new MedalCountryResponse("Bulgaria", 1, 0, 0, 1)
        ));

        List<MedalCountryResponse> result = statisticsService.getMedalsByCountry();

        assertEquals(1, result.size());
        assertEquals("Bulgaria", result.get(0).getCountry());
        verify(medalService).getByCountry();
    }

    @Test
    void getAverageAge_success() {
        when(competitionRepository.existsById(1L)).thenReturn(true);
        when(competitionResultRepository.findByCompetitionId(1L)).thenReturn(List.of(slalomResult));

        double result = statisticsService.getAverageAge(1L);

        assertTrue(result > 0);
    }

    @Test
    void getAverageAge_noResults_returnsZero() {
        when(competitionRepository.existsById(1L)).thenReturn(true);
        when(competitionResultRepository.findByCompetitionId(1L)).thenReturn(List.of());

        double result = statisticsService.getAverageAge(1L);

        assertEquals(0.0, result);
    }

    @Test
    void getAverageAge_competitionNotFound_throwsResourceNotFoundException() {
        when(competitionRepository.existsById(999L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> statisticsService.getAverageAge(999L));
    }

    @Test
    void getYoungestOldestMedalist_success() {
        AthleteProfile older = new AthleteProfile();
        older.setDateOfBirth(LocalDate.of(1990, 1, 1));
        older.setName("Older Athlete");
        older.setCountry("Bulgaria");
        older.setGender(Gender.MALE);

        Medal medal2 = new Medal();
        SlalomResult result2 = new SlalomResult();
        result2.setAthleteProfile(older);
        medal2.setResult(result2);
        medal2.setType(MedalType.SILVER);

        when(medalRepository.findAll()).thenReturn(List.of(medal, medal2));
        when(modelMapper.map(any(AthleteProfile.class), eq(AthleteProfileResponse.class)))
                .thenReturn(new AthleteProfileResponse());

        Map<String, AthleteProfileResponse> result = statisticsService.getYoungestOldestMedalist();

        assertFalse(result.isEmpty());
        assertTrue(result.containsKey("youngest"));
        assertTrue(result.containsKey("oldest"));
    }

    @Test
    void getYoungestOldestMedalist_noMedalists_returnsEmptyMap() {
        when(medalRepository.findAll()).thenReturn(List.of());

        Map<String, AthleteProfileResponse> result = statisticsService.getYoungestOldestMedalist();

        assertTrue(result.isEmpty());
    }
}