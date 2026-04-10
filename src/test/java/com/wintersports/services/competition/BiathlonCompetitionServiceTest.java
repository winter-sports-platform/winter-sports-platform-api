package com.wintersports.services.competition;

import com.wintersports.dtos.requests.CreateBiathlonCompetitionRequest;
import com.wintersports.dtos.responses.BiathlonCompetitionResponse;
import com.wintersports.entities.Tournament;
import com.wintersports.entities.competition.BiathlonCompetition;
import com.wintersports.enums.Gender;
import com.wintersports.exceptions.DuplicateResourceException.DuplicateResourceException;
import com.wintersports.exceptions.InvalidCompetitionDateException.InvalidCompetitionDateException;
import com.wintersports.exceptions.ResourceNotFoundException.ResourceNotFoundException;
import com.wintersports.repositories.competition.IBiathlonCompetitionRepository;
import com.wintersports.repositories.competition.ICompetitionRepository;
import com.wintersports.repositories.tournament.ITournamentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BiathlonCompetitionServiceTest {

    @Mock
    private IBiathlonCompetitionRepository biathlonCompetitionRepository;

    @Mock
    private ICompetitionRepository competitionRepository;

    @Mock
    private ITournamentRepository tournamentRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private BiathlonCompetitionService biathlonCompetitionService;

    private Tournament createTournament() {
        Tournament tournament = new Tournament();
        tournament.setId(1L);
        tournament.setName("Winter Olympics 2026");
        tournament.setStartDate(LocalDate.of(2026, 2, 6));
        tournament.setEndDate(LocalDate.of(2026, 2, 22));
        return tournament;
    }

    private CreateBiathlonCompetitionRequest createRequest() {
        CreateBiathlonCompetitionRequest request = new CreateBiathlonCompetitionRequest();
        request.setTournamentId(1L);
        request.setName("Biathlon Men");
        request.setGender(Gender.MALE);
        request.setMinAge(16);
        request.setDate(LocalDate.of(2026, 2, 12));
        request.setRegistrationDeadlineDays(30);
        request.setLapsCount(5);
        request.setShootingRounds(4);
        request.setPenaltySeconds(60);
        return request;
    }

    @Test
    void create_shouldCreateBiathlonCompetition_whenValid() {
        CreateBiathlonCompetitionRequest request = createRequest();
        Tournament tournament = createTournament();

        BiathlonCompetition saved = new BiathlonCompetition();
        saved.setId(1L);
        saved.setName("Biathlon Men");

        BiathlonCompetitionResponse response = new BiathlonCompetitionResponse();
        response.setId(1L);
        response.setName("Biathlon Men");

        when(competitionRepository.existsByNameAndTournamentId("Biathlon Men", 1L)).thenReturn(false);
        when(tournamentRepository.findById(1L)).thenReturn(Optional.of(tournament));
        when(biathlonCompetitionRepository.save(any())).thenReturn(saved);
        when(modelMapper.map(saved, BiathlonCompetitionResponse.class)).thenReturn(response);

        BiathlonCompetitionResponse result = biathlonCompetitionService.create(request);

        assertEquals("Biathlon Men", result.getName());
        verify(biathlonCompetitionRepository, times(1)).save(any());
    }

    @Test
    void create_shouldThrowDuplicateResourceException_whenNameExists() {
        CreateBiathlonCompetitionRequest request = createRequest();

        when(competitionRepository.existsByNameAndTournamentId("Biathlon Men", 1L)).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> biathlonCompetitionService.create(request));
        verify(biathlonCompetitionRepository, never()).save(any());
    }

    @Test
    void create_shouldThrowResourceNotFoundException_whenTournamentNotExists() {
        CreateBiathlonCompetitionRequest request = createRequest();

        when(competitionRepository.existsByNameAndTournamentId("Biathlon Men", 1L)).thenReturn(false);
        when(tournamentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> biathlonCompetitionService.create(request));
        verify(biathlonCompetitionRepository, never()).save(any());
    }

    @Test
    void create_shouldThrowInvalidCompetitionDateException_whenDateBeforeTournamentStart() {
        CreateBiathlonCompetitionRequest request = createRequest();
        request.setDate(LocalDate.of(2026, 1, 1));

        Tournament tournament = createTournament();

        when(competitionRepository.existsByNameAndTournamentId("Biathlon Men", 1L)).thenReturn(false);
        when(tournamentRepository.findById(1L)).thenReturn(Optional.of(tournament));

        assertThrows(InvalidCompetitionDateException.class, () -> biathlonCompetitionService.create(request));
        verify(biathlonCompetitionRepository, never()).save(any());
    }

    @Test
    void create_shouldThrowInvalidCompetitionDateException_whenDateAfterTournamentEnd() {
        CreateBiathlonCompetitionRequest request = createRequest();
        request.setDate(LocalDate.of(2026, 3, 1));

        Tournament tournament = createTournament();

        when(competitionRepository.existsByNameAndTournamentId("Biathlon Men", 1L)).thenReturn(false);
        when(tournamentRepository.findById(1L)).thenReturn(Optional.of(tournament));

        assertThrows(InvalidCompetitionDateException.class, () -> biathlonCompetitionService.create(request));
        verify(biathlonCompetitionRepository, never()).save(any());
    }

    @Test
    void update_shouldThrowResourceNotFoundException_whenNotExists() {
        when(biathlonCompetitionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> biathlonCompetitionService.update(99L, createRequest()));
    }
}