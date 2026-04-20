package com.wintersports.services.result;

import com.wintersports.dtos.requests.CreateSlalomResultRequest;
import com.wintersports.dtos.requests.UpdateSlalomResultRequest;
import com.wintersports.dtos.responses.SlalomResultResponse;
import com.wintersports.entities.AthleteProfile;
import com.wintersports.entities.competition.SlalomCompetition;
import com.wintersports.entities.result.SlalomResult;
import com.wintersports.exceptions.DuplicateResourceException.DuplicateResourceException;
import com.wintersports.exceptions.InvalidRegistrationException.InvalidRegistrationException;
import com.wintersports.exceptions.ResourceNotFoundException.ResourceNotFoundException;
import com.wintersports.repositories.athleteprofile.IAthleteProfileRepository;
import com.wintersports.repositories.competition.ISlalomCompetitionRepository;
import com.wintersports.repositories.registration.IRegistrationRepository;
import com.wintersports.repositories.result.ICompetitionResultRepository;
import com.wintersports.repositories.result.ISlalomResultRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SlalomResultServiceTest {

    @Mock
    private ISlalomResultRepository slalomResultRepository;

    @Mock
    private ISlalomCompetitionRepository slalomCompetitionRepository;

    @Mock
    private IAthleteProfileRepository athleteProfileRepository;

    @Mock
    private ICompetitionResultRepository competitionResultRepository;

    @Mock
    private IRegistrationRepository registrationRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private SlalomResultService slalomResultService;

    private AthleteProfile athleteProfile;
    private SlalomCompetition competition;
    private SlalomResult slalomResult;
    private CreateSlalomResultRequest createRequest;

    @BeforeEach
    void setUp() {
        athleteProfile = new AthleteProfile();
        athleteProfile.setId(1L);

        competition = mock(SlalomCompetition.class);

        slalomResult = new SlalomResult();
        slalomResult.setId(1L);
        slalomResult.setRun1Time(BigDecimal.valueOf(45.234));
        slalomResult.setCompetition(competition);

        createRequest = new CreateSlalomResultRequest();
        createRequest.setAthleteProfileId(1L);
        createRequest.setCompetitionId(1L);
        createRequest.setRun1Time(BigDecimal.valueOf(45.234));
    }

    @Test
    void create_success() {
        when(athleteProfileRepository.findById(1L)).thenReturn(Optional.of(athleteProfile));
        when(slalomCompetitionRepository.findById(1L)).thenReturn(Optional.of(competition));
        when(registrationRepository.existsByAthleteProfileIdAndCompetitionIdAndStatus(any(), any(), any())).thenReturn(true);
        when(competitionResultRepository.existsByAthleteProfileIdAndCompetitionId(1L, 1L)).thenReturn(false);
        when(slalomResultRepository.save(any())).thenReturn(slalomResult);
        when(modelMapper.map(any(), eq(SlalomResultResponse.class))).thenReturn(new SlalomResultResponse());

        SlalomResultResponse result = slalomResultService.create(createRequest);

        assertNotNull(result);
    }

    @Test
    void create_athleteNotFound_throwsResourceNotFoundException() {
        when(athleteProfileRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> slalomResultService.create(createRequest));
    }

    @Test
    void create_competitionNotFound_throwsResourceNotFoundException() {
        when(athleteProfileRepository.findById(1L)).thenReturn(Optional.of(athleteProfile));
        when(slalomCompetitionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> slalomResultService.create(createRequest));
    }

    @Test
    void create_notRegistered_throwsInvalidRegistrationException() {
        when(athleteProfileRepository.findById(1L)).thenReturn(Optional.of(athleteProfile));
        when(slalomCompetitionRepository.findById(1L)).thenReturn(Optional.of(competition));
        when(registrationRepository.existsByAthleteProfileIdAndCompetitionIdAndStatus(any(), any(), any())).thenReturn(false);

        assertThrows(InvalidRegistrationException.class, () -> slalomResultService.create(createRequest));
    }

    @Test
    void create_duplicate_throwsDuplicateResourceException() {
        when(athleteProfileRepository.findById(1L)).thenReturn(Optional.of(athleteProfile));
        when(slalomCompetitionRepository.findById(1L)).thenReturn(Optional.of(competition));
        when(registrationRepository.existsByAthleteProfileIdAndCompetitionIdAndStatus(any(), any(), any())).thenReturn(true);
        when(competitionResultRepository.existsByAthleteProfileIdAndCompetitionId(1L, 1L)).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> slalomResultService.create(createRequest));
    }

    @Test
    void update_success() {
        when(competition.getId()).thenReturn(1L);
        when(competition.getMaxRun2Participants()).thenReturn(30);

        UpdateSlalomResultRequest updateRequest = new UpdateSlalomResultRequest();
        updateRequest.setRun2Time(BigDecimal.valueOf(48.123));

        slalomResult.setCompetition(competition);

        when(slalomResultRepository.findById(1L)).thenReturn(Optional.of(slalomResult));
        when(slalomCompetitionRepository.findById(1L)).thenReturn(Optional.of(competition));
        when(slalomResultRepository.findByCompetition_IdOrderByRun1TimeAsc(1L)).thenReturn(List.of(slalomResult));
        when(slalomResultRepository.save(any())).thenReturn(slalomResult);
        when(modelMapper.map(any(), eq(SlalomResultResponse.class))).thenReturn(new SlalomResultResponse());

        SlalomResultResponse result = slalomResultService.update(1L, updateRequest);

        assertNotNull(result);
    }

    @Test
    void update_notFound_throwsResourceNotFoundException() {
        UpdateSlalomResultRequest updateRequest = new UpdateSlalomResultRequest();
        when(slalomResultRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> slalomResultService.update(1L, updateRequest));
    }

    @Test
    void update_notQualified_throwsInvalidRegistrationException() {
        when(competition.getId()).thenReturn(1L);
        when(competition.getMaxRun2Participants()).thenReturn(1);

        UpdateSlalomResultRequest updateRequest = new UpdateSlalomResultRequest();
        updateRequest.setRun2Time(BigDecimal.valueOf(48.123));

        SlalomResult otherResult = new SlalomResult();
        otherResult.setId(99L);
        otherResult.setRun1Time(BigDecimal.valueOf(40.0));
        otherResult.setCompetition(competition);

        slalomResult.setCompetition(competition);

        when(slalomCompetitionRepository.findById(1L)).thenReturn(Optional.of(competition));
        when(slalomResultRepository.findById(1L)).thenReturn(Optional.of(slalomResult));
        when(slalomResultRepository.findByCompetition_IdOrderByRun1TimeAsc(1L)).thenReturn(List.of(otherResult));
        when(competition.getMaxRun2Participants()).thenReturn(1);

        assertThrows(InvalidRegistrationException.class, () -> slalomResultService.update(1L, updateRequest));
    }
}