package com.wintersports.services.competition;

import com.wintersports.dtos.requests.CreateSlalomCompetitionRequest;
import com.wintersports.dtos.responses.SlalomCompetitionResponse;
import com.wintersports.entities.Tournament;
import com.wintersports.entities.competition.SlalomCompetition;
import com.wintersports.exceptions.DuplicateResourceException.DuplicateResourceException;
import com.wintersports.exceptions.InvalidCompetitionDateException.InvalidCompetitionDateException;
import com.wintersports.exceptions.ResourceNotFoundException.ResourceNotFoundException;
import com.wintersports.repositories.competition.ICompetitionRepository;
import com.wintersports.repositories.competition.ISlalomCompetitionRepository;
import com.wintersports.repositories.tournament.ITournamentRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SlalomCompetitionService implements ISlalomCompetitionService {

    private final ISlalomCompetitionRepository slalomCompetitionRepository;
    private final ICompetitionRepository competitionRepository;
    private final ITournamentRepository tournamentRepository;
    private final ModelMapper modelMapper;

    @Override
    public SlalomCompetitionResponse create(CreateSlalomCompetitionRequest request) {
        if (competitionRepository.existsByNameAndTournamentId(request.getName(), request.getTournamentId())) {
            throw new DuplicateResourceException("Competition with name '" + request.getName() + "' already exists in this tournament");
        }

        Tournament tournament = tournamentRepository.findById(request.getTournamentId())
                .orElseThrow(() -> new ResourceNotFoundException("Tournament with id " + request.getTournamentId() + " not found"));

        if (request.getDate().isBefore(tournament.getStartDate()) ||
                request.getDate().isAfter(tournament.getEndDate())) {
            throw new InvalidCompetitionDateException(
                    "SlalomCompetition date must be between tournament start and end date"
            );
        }

        SlalomCompetition entity = toEntity(request, tournament);
        return modelMapper.map(slalomCompetitionRepository.save(entity), SlalomCompetitionResponse.class);
    }

    @Override
    public SlalomCompetitionResponse update(Long id, CreateSlalomCompetitionRequest request) {
        SlalomCompetition entity = slalomCompetitionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SlalomCompetition with id " + id + " not found"));

        if (competitionRepository.existsByNameAndTournamentIdAndIdNot(request.getName(), request.getTournamentId(), id)) {
            throw new DuplicateResourceException("Competition with name '" + request.getName() + "' already exists in this tournament");
        }

        Tournament tournament = tournamentRepository.findById(request.getTournamentId())
                .orElseThrow(() -> new ResourceNotFoundException("Tournament with id " + request.getTournamentId() + " not found"));

        if (request.getDate().isBefore(tournament.getStartDate()) ||
                request.getDate().isAfter(tournament.getEndDate())) {
            throw new InvalidCompetitionDateException(
                    "SlalomCompetition date must be between tournament start and end date"
            );
        }

        updateEntity(entity, request, tournament);
        return modelMapper.map(slalomCompetitionRepository.save(entity), SlalomCompetitionResponse.class);
    }

    private SlalomCompetition toEntity(CreateSlalomCompetitionRequest request, Tournament tournament) {
        SlalomCompetition entity = new SlalomCompetition();
        updateEntity(entity, request, tournament);
        entity.setType("SLALOM");
        return entity;
    }

    private void updateEntity(SlalomCompetition entity, CreateSlalomCompetitionRequest request, Tournament tournament) {
        entity.setTournament(tournament);
        entity.setName(request.getName());
        entity.setGender(request.getGender());
        entity.setMinAge(request.getMinAge());
        entity.setDate(request.getDate());
        entity.setRegistrationDeadlineDays(request.getRegistrationDeadlineDays());
        entity.setMaxRun2Participants(request.getMaxRun2Participants());
    }
}