package com.wintersports.services.result;

import com.wintersports.configs.ModelMapperConfig;
import com.wintersports.dtos.responses.BiathlonResultResponse;
import com.wintersports.dtos.responses.CompetitionResultResponse;
import com.wintersports.dtos.responses.SlalomResultResponse;
import com.wintersports.entities.result.BiathlonResult;
import com.wintersports.entities.result.CompetitionResult;
import com.wintersports.entities.result.SlalomResult;
import com.wintersports.exceptions.ResourceNotFoundException.ResourceNotFoundException;
import com.wintersports.repositories.competition.ICompetitionRepository;
import com.wintersports.repositories.result.ICompetitionResultRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CompetitionResultService implements ICompetitionResultService {

    private final ICompetitionResultRepository competitionResultRepository;
    private final ICompetitionRepository competitionRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<CompetitionResultResponse> getAll() {
        return competitionResultRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public CompetitionResultResponse getById(Long id) {
        return mapToResponse(findById(id));
    }

    @Override
    public List<CompetitionResultResponse> getAllByCompetition(Long competitionId) {
        if (!competitionRepository.existsById(competitionId)) {
            throw new ResourceNotFoundException("Competition with id " + competitionId + " not found");
        }

        return competitionResultRepository.findByCompetitionId(competitionId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public void delete(Long id) {
        findById(id);
        competitionResultRepository.deleteById(id);
    }

    private CompetitionResult findById(Long id) {
        return competitionResultRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Result with id " + id + " not found"));
    }

    private CompetitionResultResponse mapToResponse(CompetitionResult result) {
        if (result instanceof BiathlonResult biathlon) {
            return modelMapper.map(biathlon, BiathlonResultResponse.class);
        } else if (result instanceof SlalomResult slalom) {
            return modelMapper.map(slalom, SlalomResultResponse.class);
        }
        return modelMapper.map(result, CompetitionResultResponse.class);
    }
}