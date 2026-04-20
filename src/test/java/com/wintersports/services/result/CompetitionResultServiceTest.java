package com.wintersports.services.result;

import com.wintersports.dtos.responses.BiathlonResultResponse;
import com.wintersports.dtos.responses.CompetitionResultResponse;
import com.wintersports.dtos.responses.SlalomResultResponse;
import com.wintersports.entities.result.BiathlonResult;
import com.wintersports.entities.result.SlalomResult;
import com.wintersports.exceptions.ResourceNotFoundException.ResourceNotFoundException;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompetitionResultServiceTest {

    @Mock
    private ICompetitionResultRepository competitionResultRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private CompetitionResultService competitionResultService;

    private SlalomResult slalomResult;
    private BiathlonResult biathlonResult;

    @BeforeEach
    void setUp() {
        slalomResult = new SlalomResult();
        slalomResult.setId(1L);

        biathlonResult = new BiathlonResult();
        biathlonResult.setId(2L);
    }

    @Test
    void getAll_success() {
        when(competitionResultRepository.findAll()).thenReturn(List.of(slalomResult, biathlonResult));
        when(modelMapper.map(any(SlalomResult.class), eq(SlalomResultResponse.class))).thenReturn(new SlalomResultResponse());
        when(modelMapper.map(any(BiathlonResult.class), eq(BiathlonResultResponse.class))).thenReturn(new BiathlonResultResponse());

        List<CompetitionResultResponse> result = competitionResultService.getAll();

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void getById_slalom_success() {
        when(competitionResultRepository.findById(1L)).thenReturn(Optional.of(slalomResult));
        when(modelMapper.map(any(SlalomResult.class), eq(SlalomResultResponse.class))).thenReturn(new SlalomResultResponse());

        CompetitionResultResponse result = competitionResultService.getById(1L);

        assertNotNull(result);
    }

    @Test
    void getById_biathlon_success() {
        when(competitionResultRepository.findById(2L)).thenReturn(Optional.of(biathlonResult));
        when(modelMapper.map(any(BiathlonResult.class), eq(BiathlonResultResponse.class))).thenReturn(new BiathlonResultResponse());

        CompetitionResultResponse result = competitionResultService.getById(2L);

        assertNotNull(result);
    }

    @Test
    void getById_notFound_throwsResourceNotFoundException() {
        when(competitionResultRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> competitionResultService.getById(1L));
    }

    @Test
    void delete_success() {
        when(competitionResultRepository.findById(1L)).thenReturn(Optional.of(slalomResult));

        assertDoesNotThrow(() -> competitionResultService.delete(1L));
        verify(competitionResultRepository, times(1)).deleteById(1L);
    }

    @Test
    void delete_notFound_throwsResourceNotFoundException() {
        when(competitionResultRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> competitionResultService.delete(1L));
    }
}