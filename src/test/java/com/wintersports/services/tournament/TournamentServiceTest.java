package com.wintersports.services.tournament;

import com.wintersports.dtos.requests.CreateTournamentRequest;
import com.wintersports.dtos.responses.TournamentResponse;
import com.wintersports.entities.Tournament;
import com.wintersports.exceptions.DuplicateResourceException.DuplicateResourceException;
import com.wintersports.exceptions.ResourceNotFoundException.ResourceNotFoundException;
import com.wintersports.repositories.tournament.ITournamentRepository;
import com.wintersports.repositories.tournamenttype.ITournamentTypeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TournamentServiceTest {

    @Mock
    private ITournamentRepository tournamentRepository;

    @Mock
    private ITournamentTypeRepository tournamentTypeRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private TournamentService tournamentService;

    @Test
    void getAll_shouldReturnAllTournaments() {
        Tournament tournament = new Tournament();
        tournament.setId(1L);
        tournament.setName("Winter Olympics 2026");

        TournamentResponse response = new TournamentResponse();
        response.setId(1L);
        response.setName("Winter Olympics 2026");

        when(tournamentRepository.findAll()).thenReturn(List.of(tournament));
        when(modelMapper.map(tournament, TournamentResponse.class)).thenReturn(response);

        List<TournamentResponse> result = tournamentService.getAll();

        assertEquals(1, result.size());
        assertEquals("Winter Olympics 2026", result.getFirst().getName());
        verify(tournamentRepository, times(1)).findAll();
    }

    @Test
    void getById_shouldReturnTournament_whenExists() {
        Tournament tournament = new Tournament();
        tournament.setId(1L);
        tournament.setName("Winter Olympics 2026");

        TournamentResponse response = new TournamentResponse();
        response.setId(1L);
        response.setName("Winter Olympics 2026");

        when(tournamentRepository.findById(1L)).thenReturn(Optional.of(tournament));
        when(modelMapper.map(tournament, TournamentResponse.class)).thenReturn(response);

        TournamentResponse result = tournamentService.getById(1L);

        assertEquals("Winter Olympics 2026", result.getName());
    }

    @Test
    void getById_shouldThrowResourceNotFoundException_whenNotExists() {
        when(tournamentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> tournamentService.getById(99L));
    }

    @Test
    void create_shouldThrowDuplicateResourceException_whenNameExists() {
        CreateTournamentRequest request = new CreateTournamentRequest();
        request.setName("Winter Olympics 2026");

        when(tournamentRepository.existsByName("Winter Olympics 2026")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> tournamentService.create(request));
        verify(tournamentRepository, never()).save(any());
    }

    @Test
    void create_shouldThrowResourceNotFoundException_whenTypeNotExists() {
        CreateTournamentRequest request = new CreateTournamentRequest();
        request.setName("Winter Olympics 2026");
        request.setTypeId(99L);

        when(tournamentRepository.existsByName("Winter Olympics 2026")).thenReturn(false);
        when(tournamentTypeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> tournamentService.create(request));
    }

    @Test
    void delete_shouldThrowResourceNotFoundException_whenNotExists() {
        when(tournamentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> tournamentService.delete(99L));
        verify(tournamentRepository, never()).deleteById(any());
    }
}