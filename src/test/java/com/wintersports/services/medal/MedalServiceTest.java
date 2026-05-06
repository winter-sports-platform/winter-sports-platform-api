package com.wintersports.services.medal;

import com.wintersports.dtos.requests.CreateMedalRequest;
import com.wintersports.dtos.responses.MedalCountryResponse;
import com.wintersports.dtos.responses.MedalResponse;
import com.wintersports.entities.AthleteProfile;
import com.wintersports.entities.Medal;
import com.wintersports.entities.competition.BiathlonCompetition;
import com.wintersports.entities.competition.Competition;
import com.wintersports.entities.competition.SlalomCompetition;
import com.wintersports.entities.result.BiathlonResult;
import com.wintersports.entities.result.CompetitionResult;
import com.wintersports.entities.result.SlalomResult;
import com.wintersports.enums.MedalType;
import com.wintersports.exceptions.DuplicateResourceException.DuplicateResourceException;
import com.wintersports.exceptions.ResourceNotFoundException.ResourceNotFoundException;
import com.wintersports.repositories.competition.ICompetitionRepository;
import com.wintersports.repositories.medal.IMedalRepository;
import com.wintersports.repositories.result.ICompetitionResultRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MedalServiceTest {

    @Mock
    private IMedalRepository medalRepository;

    @Mock
    private ICompetitionResultRepository competitionResultRepository;

    @Mock
    private ICompetitionRepository competitionRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private MedalService medalService;

    private CreateMedalRequest request;
    private CompetitionResult result;
    private Competition competition;
    private Medal medal;
    private AthleteProfile athleteProfile;

    @BeforeEach
    void setUp() {
        athleteProfile = new AthleteProfile();
        athleteProfile.setCountry("Bulgaria");

        result = new SlalomResult();
        result.setId(1L);
        result.setFinished(true);
        result.setAthleteProfile(athleteProfile);

        competition = new SlalomCompetition();
        competition.setId(1L);

        medal = new Medal();
        medal.setId(1L);
        medal.setResult(result);
        medal.setCompetition(competition);
        medal.setType(MedalType.GOLD);

        request = new CreateMedalRequest();
        request.setResultId(1L);
        request.setCompetitionId(1L);
        request.setType(MedalType.GOLD);
    }

    @Test
    void getAll_returnsAllMedals() {
        when(medalRepository.findAll()).thenReturn(List.of(medal));
        when(modelMapper.map(medal, MedalResponse.class)).thenReturn(new MedalResponse());

        List<MedalResponse> result = medalService.getAll();

        assertEquals(1, result.size());
    }

    @Test
    void getByCountry_groupsCorrectly() {
        when(medalRepository.findAll()).thenReturn(List.of(medal));

        List<MedalCountryResponse> response = medalService.getByCountry();

        assertEquals(1, response.size());
        assertEquals("Bulgaria", response.getFirst().getCountry());
        assertEquals(1, response.getFirst().getGold());
        assertEquals(0, response.getFirst().getSilver());
        assertEquals(0, response.getFirst().getBronze());
        assertEquals(1, response.getFirst().getTotal());
    }

    @Test
    void create_success() {
        when(competitionResultRepository.findById(1L)).thenReturn(Optional.of(result));
        when(competitionRepository.findById(1L)).thenReturn(Optional.of(competition));
        when(medalRepository.existsByCompetitionIdAndType(1L, MedalType.GOLD)).thenReturn(false);
        when(medalRepository.save(any())).thenReturn(medal);
        when(modelMapper.map(medal, MedalResponse.class)).thenReturn(new MedalResponse());

        MedalResponse response = medalService.create(request);

        assertNotNull(response);
        verify(medalRepository).save(any());
    }

    @Test
    void create_withBiathlonResult_success() {
        BiathlonResult biathlonResult = new BiathlonResult();
        biathlonResult.setId(2L);
        biathlonResult.setFinished(true);
        biathlonResult.setAthleteProfile(athleteProfile);

        BiathlonCompetition biathlonCompetition = new BiathlonCompetition();
        biathlonCompetition.setId(2L);

        CreateMedalRequest biathlonRequest = new CreateMedalRequest();
        biathlonRequest.setResultId(2L);
        biathlonRequest.setCompetitionId(2L);
        biathlonRequest.setType(MedalType.SILVER);

        when(competitionResultRepository.findById(2L)).thenReturn(Optional.of(biathlonResult));
        when(competitionRepository.findById(2L)).thenReturn(Optional.of(biathlonCompetition));
        when(medalRepository.existsByCompetitionIdAndType(2L, MedalType.SILVER)).thenReturn(false);
        when(medalRepository.save(any())).thenReturn(medal);
        when(modelMapper.map(medal, MedalResponse.class)).thenReturn(new MedalResponse());

        MedalResponse response = medalService.create(biathlonRequest);

        assertNotNull(response);
        verify(medalRepository).save(any());
    }

    @Test
    void create_resultNotFound_throwsResourceNotFoundException() {
        when(competitionResultRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> medalService.create(request));
    }

    @Test
    void create_competitionNotFound_throwsResourceNotFoundException() {
        when(competitionResultRepository.findById(1L)).thenReturn(Optional.of(result));
        when(competitionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> medalService.create(request));
    }

    @Test
    void create_athleteNotFinished_throwsIllegalArgumentException() {
        result.setFinished(false);
        when(competitionResultRepository.findById(1L)).thenReturn(Optional.of(result));
        when(competitionRepository.findById(1L)).thenReturn(Optional.of(competition));

        assertThrows(IllegalArgumentException.class, () -> medalService.create(request));
    }

    @Test
    void create_duplicateMedalType_throwsDuplicateResourceException() {
        when(competitionResultRepository.findById(1L)).thenReturn(Optional.of(result));
        when(competitionRepository.findById(1L)).thenReturn(Optional.of(competition));
        when(medalRepository.existsByCompetitionIdAndType(1L, MedalType.GOLD)).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> medalService.create(request));
    }

    @Test
    void delete_success() {
        when(medalRepository.findById(1L)).thenReturn(Optional.of(medal));

        medalService.delete(1L);

        verify(medalRepository).deleteById(1L);
    }

    @Test
    void delete_notFound_throwsResourceNotFoundException() {
        when(medalRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> medalService.delete(1L));
    }
}