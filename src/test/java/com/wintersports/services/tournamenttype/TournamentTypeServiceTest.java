package com.wintersports.services.tournamenttype;

import com.wintersports.dtos.requests.CreateTournamentTypeRequest;
import com.wintersports.dtos.responses.TournamentTypeResponse;
import com.wintersports.entities.TournamentType;
import com.wintersports.exceptions.DuplicateResourceException.DuplicateResourceException;
import com.wintersports.exceptions.ResourceNotFoundException.ResourceNotFoundException;
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
class TournamentTypeServiceTest {

    @Mock
    private ITournamentTypeRepository tournamentTypeRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private TournamentTypeService tournamentTypeService;

    @Test
    void getAll_shouldReturnAllTournamentTypes() {
        TournamentType type = new TournamentType();
        type.setId(1L);
        type.setName("Olympics");

        TournamentTypeResponse response = new TournamentTypeResponse();
        response.setId(1L);
        response.setName("Olympics");

        when(tournamentTypeRepository.findAll()).thenReturn(List.of(type));
        when(modelMapper.map(type, TournamentTypeResponse.class)).thenReturn(response);

        List<TournamentTypeResponse> result = tournamentTypeService.getAll();

        assertEquals(1, result.size());
        assertEquals("Olympics", result.getFirst().getName());
        verify(tournamentTypeRepository, times(1)).findAll();
    }

    @Test
    void getById_shouldReturnTournamentType_whenExists() {
        TournamentType type = new TournamentType();
        type.setId(1L);
        type.setName("Olympics");

        TournamentTypeResponse response = new TournamentTypeResponse();
        response.setId(1L);
        response.setName("Olympics");

        when(tournamentTypeRepository.findById(1L)).thenReturn(Optional.of(type));
        when(modelMapper.map(type, TournamentTypeResponse.class)).thenReturn(response);

        TournamentTypeResponse result = tournamentTypeService.getById(1L);

        assertEquals("Olympics", result.getName());
    }

    @Test
    void getById_shouldThrowResourceNotFoundException_whenNotExists() {
        when(tournamentTypeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> tournamentTypeService.getById(99L));
    }

    @Test
    void create_shouldCreateTournamentType_whenNameIsUnique() {
        CreateTournamentTypeRequest request = new CreateTournamentTypeRequest();
        request.setName("Olympics");

        TournamentType saved = new TournamentType();
        saved.setId(1L);
        saved.setName("Olympics");

        TournamentTypeResponse response = new TournamentTypeResponse();
        response.setId(1L);
        response.setName("Olympics");

        when(tournamentTypeRepository.existsByName("Olympics")).thenReturn(false);
        when(tournamentTypeRepository.save(any())).thenReturn(saved);
        when(modelMapper.map(saved, TournamentTypeResponse.class)).thenReturn(response);

        TournamentTypeResponse result = tournamentTypeService.create(request);

        assertEquals("Olympics", result.getName());
        verify(tournamentTypeRepository, times(1)).save(any());
    }

    @Test
    void create_shouldThrowDuplicateResourceException_whenNameExists() {
        CreateTournamentTypeRequest request = new CreateTournamentTypeRequest();
        request.setName("Olympics");

        when(tournamentTypeRepository.existsByName("Olympics")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> tournamentTypeService.create(request));
        verify(tournamentTypeRepository, never()).save(any());
    }

    @Test
    void delete_shouldThrowResourceNotFoundException_whenNotExists() {
        when(tournamentTypeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> tournamentTypeService.delete(99L));
        verify(tournamentTypeRepository, never()).deleteById(any());
    }
}