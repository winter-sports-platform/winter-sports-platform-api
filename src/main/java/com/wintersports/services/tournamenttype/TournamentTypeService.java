package com.wintersports.services.tournamenttype;

import com.wintersports.configs.ModelMapperConfig;
import com.wintersports.dtos.requests.CreateTournamentTypeRequest;
import com.wintersports.dtos.responses.TournamentTypeResponse;
import com.wintersports.entities.TournamentType;
import com.wintersports.exceptions.DuplicateResourceException.DuplicateResourceException;
import com.wintersports.exceptions.ResourceNotFoundException.ResourceNotFoundException;
import com.wintersports.repositories.tournamenttype.ITournamentTypeRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TournamentTypeService implements ITournamentTypeService {

    private final ITournamentTypeRepository tournamentTypeRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<TournamentTypeResponse> getAll() {
        return ModelMapperConfig.mapList(
                tournamentTypeRepository.findAll(),
                TournamentTypeResponse.class,
                modelMapper
        );
    }

    @Override
    public TournamentTypeResponse getById(Long id) {
        return modelMapper.map(findById(id), TournamentTypeResponse.class);
    }

    @Override
    public TournamentTypeResponse create(CreateTournamentTypeRequest request) {
        if (tournamentTypeRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException("TournamentType with name '" + request.getName() + "' already exists");
        }
        TournamentType entity = modelMapper.map(request, TournamentType.class);
        return modelMapper.map(tournamentTypeRepository.save(entity), TournamentTypeResponse.class);
    }

    @Override
    public TournamentTypeResponse update(Long id, CreateTournamentTypeRequest request) {
        TournamentType entity = findById(id);
        modelMapper.map(request, entity);
        return modelMapper.map(tournamentTypeRepository.save(entity), TournamentTypeResponse.class);
    }

    @Override
    public void delete(Long id) {
        findById(id);
        tournamentTypeRepository.deleteById(id);
    }

    private TournamentType findById(Long id) {
        return tournamentTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TournamentType with id " + id + " not found"));
    }
}