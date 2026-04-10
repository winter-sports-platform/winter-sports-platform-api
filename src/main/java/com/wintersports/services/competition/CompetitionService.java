package com.wintersports.services.competition;

import com.wintersports.configs.ModelMapperConfig;
import com.wintersports.dtos.responses.CompetitionResponse;
import com.wintersports.dtos.responses.TournamentResponse;
import com.wintersports.entities.competition.Competition;
import com.wintersports.exceptions.ResourceNotFoundException.ResourceNotFoundException;
import com.wintersports.repositories.competition.ICompetitionRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CompetitionService implements ICompetitionService {
    private final ICompetitionRepository competitionRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<CompetitionResponse> getAll() {
        return ModelMapperConfig.mapList(
                competitionRepository.findAll(),
                CompetitionResponse.class,
                modelMapper
        );
    }

    @Override
    public CompetitionResponse getById(Long id) {
        return modelMapper.map(findById(id), CompetitionResponse.class);
    }

    @Override
    public void delete(Long id) {
        findById(id);
        competitionRepository.deleteById(id);
    }

    private Competition findById(Long id) {
        return competitionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Competition with id " + id + " not found"));
    }
}
