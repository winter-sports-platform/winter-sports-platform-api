package com.wintersports.services.tournament;

import com.wintersports.configs.ModelMapperConfig;
import com.wintersports.dtos.requests.CreateTournamentRequest;
import com.wintersports.dtos.responses.TournamentResponse;
import com.wintersports.entities.Tournament;
import com.wintersports.entities.TournamentType;
import com.wintersports.exceptions.DuplicateResourceException.DuplicateResourceException;
import com.wintersports.exceptions.ResourceNotFoundException.ResourceNotFoundException;
import com.wintersports.repositories.tournament.ITournamentRepository;
import com.wintersports.repositories.tournamenttype.ITournamentTypeRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TournamentService implements ITournamentService {
    private final ITournamentRepository tournamentRepository;
    private final ITournamentTypeRepository tournamentTypeRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<TournamentResponse> getAll() {
        return ModelMapperConfig.mapList(
                tournamentRepository.findAll(),
                TournamentResponse.class,
                modelMapper
        );
    }

    @Override
    public TournamentResponse getById(Long id) {
        return modelMapper.map(findById(id), TournamentResponse.class);
    }

    @Override
    public TournamentResponse create(CreateTournamentRequest request) {
        if (tournamentRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException("Tournament with name '" + request.getName() + "' already exists");
        }

        TournamentType type = tournamentTypeRepository.findById(request.getTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("TournamentType with id " + request.getTypeId() + " not found"));

        Tournament entity = modelMapper.map(request, Tournament.class);
        entity.setType(type);

        return modelMapper.map(tournamentRepository.save(entity), TournamentResponse.class);
    }

    @Override
    public TournamentResponse update(Long id, CreateTournamentRequest request) {
        Tournament entity = findById(id);

        if (tournamentRepository.existsByNameAndIdNot(request.getName(), id)) {
            throw new DuplicateResourceException("Tournament with name '" + request.getName() + "' already exists");
        }

        TournamentType type = tournamentTypeRepository.findById(request.getTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("TournamentType with id " + request.getTypeId() + " not found"));

        modelMapper.map(request, entity);
        entity.setType(type);

        return modelMapper.map(tournamentRepository.save(entity), TournamentResponse.class);
    }

    @Override
    public void delete(Long id) {
        findById(id);
        tournamentRepository.deleteById(id);
    }

    private Tournament findById(Long id) {
        return tournamentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tournament with id " + id + " not found"));
    }
}