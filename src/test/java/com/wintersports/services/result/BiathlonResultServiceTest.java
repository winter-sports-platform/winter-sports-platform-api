package com.wintersports.services.result;

import com.wintersports.dtos.requests.CreateBiathlonResultRequest;
import com.wintersports.dtos.requests.UpdateBiathlonResultRequest;
import com.wintersports.dtos.responses.BiathlonResultResponse;
import com.wintersports.entities.AthleteProfile;
import com.wintersports.entities.competition.BiathlonCompetition;
import com.wintersports.entities.result.BiathlonResult;
import com.wintersports.exceptions.DuplicateResourceException.DuplicateResourceException;
import com.wintersports.exceptions.InvalidRegistrationException.InvalidRegistrationException;
import com.wintersports.exceptions.ResourceNotFoundException.ResourceNotFoundException;
import com.wintersports.repositories.athleteprofile.IAthleteProfileRepository;
import com.wintersports.repositories.competition.IBiathlonCompetitionRepository;
import com.wintersports.repositories.registration.IRegistrationRepository;
import com.wintersports.repositories.result.IBiathlonResultRepository;
import com.wintersports.repositories.result.ICompetitionResultRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BiathlonResultServiceTest {

    @Mock
    private IBiathlonResultRepository biathlonResultRepository;

    @Mock
    private IBiathlonCompetitionRepository biathlonCompetitionRepository;

    @Mock
    private IAthleteProfileRepository athleteProfileRepository;

    @Mock
    private ICompetitionResultRepository competitionResultRepository;

    @Mock
    private IRegistrationRepository registrationRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private BiathlonResultService biathlonResultService;

    private AthleteProfile athleteProfile;
    private BiathlonCompetition competition;
    private BiathlonResult biathlonResult;
    private CreateBiathlonResultRequest createRequest;

    @BeforeEach
    void setUp() {
        athleteProfile = new AthleteProfile();
        athleteProfile.setId(1L);

        competition = mock(BiathlonCompetition.class);

        biathlonResult = new BiathlonResult();
        biathlonResult.setId(1L);
        biathlonResult.setSkiTime(BigDecimal.valueOf(120.456));
        biathlonResult.setMissedShots(3);
        biathlonResult.setPenaltyTime(BigDecimal.valueOf(180.0));
        biathlonResult.setTotalTime(BigDecimal.valueOf(300.456));

        createRequest = new CreateBiathlonResultRequest();
        createRequest.setAthleteProfileId(1L);
        createRequest.setCompetitionId(1L);
        createRequest.setSkiTime(BigDecimal.valueOf(120.456));
        createRequest.setMissedShots(3);
    }

    @Test
    void create_success() {
        when(competition.getPenaltySeconds()).thenReturn(60);

        when(athleteProfileRepository.findById(1L)).thenReturn(Optional.of(athleteProfile));
        when(biathlonCompetitionRepository.findById(1L)).thenReturn(Optional.of(competition));
        when(registrationRepository.existsByAthleteProfileIdAndCompetitionIdAndStatus(any(), any(), any())).thenReturn(true);
        when(competitionResultRepository.existsByAthleteProfileIdAndCompetitionId(1L, 1L)).thenReturn(false);
        when(biathlonResultRepository.save(any())).thenReturn(biathlonResult);
        when(modelMapper.map(any(), eq(BiathlonResultResponse.class))).thenReturn(new BiathlonResultResponse());

        BiathlonResultResponse result = biathlonResultService.create(createRequest);

        assertNotNull(result);
    }

    @Test
    void create_athleteNotFound_throwsResourceNotFoundException() {
        when(athleteProfileRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> biathlonResultService.create(createRequest));
    }

    @Test
    void create_competitionNotFound_throwsResourceNotFoundException() {
        when(athleteProfileRepository.findById(1L)).thenReturn(Optional.of(athleteProfile));
        when(biathlonCompetitionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> biathlonResultService.create(createRequest));
    }

    @Test
    void create_notRegistered_throwsInvalidRegistrationException() {
        when(athleteProfileRepository.findById(1L)).thenReturn(Optional.of(athleteProfile));
        when(biathlonCompetitionRepository.findById(1L)).thenReturn(Optional.of(competition));
        when(registrationRepository.existsByAthleteProfileIdAndCompetitionIdAndStatus(any(), any(), any())).thenReturn(false);

        assertThrows(InvalidRegistrationException.class, () -> biathlonResultService.create(createRequest));
    }

    @Test
    void create_duplicate_throwsDuplicateResourceException() {
        when(athleteProfileRepository.findById(1L)).thenReturn(Optional.of(athleteProfile));
        when(biathlonCompetitionRepository.findById(1L)).thenReturn(Optional.of(competition));
        when(registrationRepository.existsByAthleteProfileIdAndCompetitionIdAndStatus(any(), any(), any())).thenReturn(true);
        when(competitionResultRepository.existsByAthleteProfileIdAndCompetitionId(1L, 1L)).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> biathlonResultService.create(createRequest));
    }

    @Test
    void update_success() {
        when(competition.getId()).thenReturn(1L);
        when(competition.getPenaltySeconds()).thenReturn(60);

        UpdateBiathlonResultRequest updateRequest = new UpdateBiathlonResultRequest();
        updateRequest.setSkiTime(BigDecimal.valueOf(115.0));
        updateRequest.setMissedShots(2);
        updateRequest.setFinished(true);

        biathlonResult.setCompetition(competition);

        when(biathlonResultRepository.findById(1L)).thenReturn(Optional.of(biathlonResult));
        when(biathlonCompetitionRepository.findById(1L)).thenReturn(Optional.of(competition));
        when(biathlonResultRepository.save(any())).thenReturn(biathlonResult);
        when(modelMapper.map(any(), eq(BiathlonResultResponse.class))).thenReturn(new BiathlonResultResponse());

        BiathlonResultResponse result = biathlonResultService.update(1L, updateRequest);

        assertNotNull(result);
    }

    @Test
    void update_notFound_throwsResourceNotFoundException() {
        UpdateBiathlonResultRequest updateRequest = new UpdateBiathlonResultRequest();
        when(biathlonResultRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> biathlonResultService.update(1L, updateRequest));
    }
}