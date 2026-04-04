package com.wintersports.services.tournamenttype;

import com.wintersports.dtos.requests.CreateTournamentTypeRequest;
import com.wintersports.dtos.responses.TournamentTypeResponse;
import com.wintersports.entities.TournamentType;
import com.wintersports.exceptions.DuplicateResourceException.DuplicateResourceException;
import com.wintersports.exceptions.ResourceNotFoundException.ResourceNotFoundException;
import com.wintersports.repositories.tournamenttype.ITournamentTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TournamentTypeService implements ITournamentTypeService {

    private final ITournamentTypeRepository tournamentTypeRepository;

    @Override
    public List<TournamentTypeResponse> getAll() {
        return tournamentTypeRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public TournamentTypeResponse getById(Long id) {
        return toResponse(findById(id));
    }

    @Override
    public TournamentTypeResponse create(CreateTournamentTypeRequest request) {
        if (tournamentTypeRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException("TournamentType with name '" + request.getName() + "' already exists");
        }

        TournamentType entity = new TournamentType();
        entity.setName(request.getName());
        return toResponse(tournamentTypeRepository.save(entity));
    }

    @Override
    public TournamentTypeResponse update(Long id, CreateTournamentTypeRequest request) {
        TournamentType entity = findById(id);
        entity.setName(request.getName());
        return toResponse(tournamentTypeRepository.save(entity));
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

    private TournamentTypeResponse toResponse(TournamentType entity) {
        TournamentTypeResponse response = new TournamentTypeResponse();
        response.setId(entity.getId());
        response.setName(entity.getName());
        return response;
    }
}