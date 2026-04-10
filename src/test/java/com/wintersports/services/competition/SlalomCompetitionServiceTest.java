package com.wintersports.services.competition;

import com.wintersports.dtos.requests.CreateSlalomCompetitionRequest;
import com.wintersports.dtos.responses.SlalomCompetitionResponse;
import com.wintersports.entities.Tournament;
import com.wintersports.entities.competition.SlalomCompetition;
import com.wintersports.enums.Gender;
import com.wintersports.exceptions.DuplicateResourceException.DuplicateResourceException;
import com.wintersports.exceptions.InvalidCompetitionDateException.InvalidCompetitionDateException;
import com.wintersports.exceptions.ResourceNotFoundException.ResourceNotFoundException;
import com.wintersports.repositories.competition.ICompetitionRepository;
import com.wintersports.repositories.competition.ISlalomCompetitionRepository;
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
class SlalomCompetitionServiceTest {

    @Mock
    private ISlalomCompetitionRepository slalomCompetitionRepository;

    @Mock
    private ICompetitionRepository competitionRepository;

    @Mock
    private ITournamentRepository tournamentRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private SlalomCompetitionService slalomCompetitionService;

    private Tournament createTournament() {
        Tournament tournament = new Tournament();
        tournament.setId(1L);
        tournament.setName("Winter Olympics 2026");
        tournament.setStartDate(LocalDate.of(2026, 2, 6));
        tournament.setEndDate(LocalDate.of(2026, 2, 22));
        return tournament;
    }

    private CreateSlalomCompetitionRequest createRequest() {
        CreateSlalomCompetitionRequest request = new CreateSlalomCompetitionRequest();
        request.setTournamentId(1L);
        request.setName("Slalom Men");
        request.setGender(Gender.MALE);
        request.setMinAge(16);
        request.setDate(LocalDate.of(2026, 2, 10));
        request.setRegistrationDeadlineDays(30);
        request.setMaxRun2Participants(30);
        return request;
    }

    @Test
    void create_shouldCreateSlalomCompetition_whenValid() {
        CreateSlalomCompetitionRequest request = createRequest();
        Tournament tournament = createTournament();

        SlalomCompetition saved = new SlalomCompetition();
        saved.setId(1L);
        saved.setName("Slalom Men");

        SlalomCompetitionResponse response = new SlalomCompetitionResponse();
        response.setId(1L);
        response.setName("Slalom Men");

        when(competitionRepository.existsByNameAndTournamentId("Slalom Men", 1L)).thenReturn(false);
        when(tournamentRepository.findById(1L)).thenReturn(Optional.of(tournament));
        when(slalomCompetitionRepository.save(any())).thenReturn(saved);
        when(modelMapper.map(saved, SlalomCompetitionResponse.class)).thenReturn(response);

        SlalomCompetitionResponse result = slalomCompetitionService.create(request);

        assertEquals("Slalom Men", result.getName());
        verify(slalomCompetitionRepository, times(1)).save(any());
    }

    @Test
    void create_shouldThrowDuplicateResourceException_whenNameExists() {
        CreateSlalomCompetitionRequest request = createRequest();

        when(competitionRepository.existsByNameAndTournamentId("Slalom Men", 1L)).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> slalomCompetitionService.create(request));
        verify(slalomCompetitionRepository, never()).save(any());
    }

    @Test
    void create_shouldThrowResourceNotFoundException_whenTournamentNotExists() {
        CreateSlalomCompetitionRequest request = createRequest();

        when(competitionRepository.existsByNameAndTournamentId("Slalom Men", 1L)).thenReturn(false);
        when(tournamentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> slalomCompetitionService.create(request));
        verify(slalomCompetitionRepository, never()).save(any());
    }

    @Test
    void create_shouldThrowInvalidCompetitionDateException_whenDateBeforeTournamentStart() {
        CreateSlalomCompetitionRequest request = createRequest();
        request.setDate(LocalDate.of(2026, 1, 1)); // преди startDate

        Tournament tournament = createTournament();

        when(competitionRepository.existsByNameAndTournamentId("Slalom Men", 1L)).thenReturn(false);
        when(tournamentRepository.findById(1L)).thenReturn(Optional.of(tournament));

        assertThrows(InvalidCompetitionDateException.class, () -> slalomCompetitionService.create(request));
        verify(slalomCompetitionRepository, never()).save(any());
    }

    @Test
    void create_shouldThrowInvalidCompetitionDateException_whenDateAfterTournamentEnd() {
        CreateSlalomCompetitionRequest request = createRequest();
        request.setDate(LocalDate.of(2026, 3, 1)); // след endDate

        Tournament tournament = createTournament();

        when(competitionRepository.existsByNameAndTournamentId("Slalom Men", 1L)).thenReturn(false);
        when(tournamentRepository.findById(1L)).thenReturn(Optional.of(tournament));

        assertThrows(InvalidCompetitionDateException.class, () -> slalomCompetitionService.create(request));
        verify(slalomCompetitionRepository, never()).save(any());
    }

    @Test
    void update_shouldThrowResourceNotFoundException_whenNotExists() {
        when(slalomCompetitionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> slalomCompetitionService.update(99L, createRequest()));
    }
}