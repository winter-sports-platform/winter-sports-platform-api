package com.wintersports.services.competition;

import com.wintersports.dtos.requests.CreateBiathlonCompetitionRequest;
import com.wintersports.dtos.responses.BiathlonCompetitionResponse;
import com.wintersports.entities.Tournament;
import com.wintersports.entities.competition.BiathlonCompetition;
import com.wintersports.exceptions.DuplicateResourceException.DuplicateResourceException;
import com.wintersports.exceptions.InvalidCompetitionDateException.InvalidCompetitionDateException;
import com.wintersports.exceptions.ResourceNotFoundException.ResourceNotFoundException;
import com.wintersports.repositories.competition.ICompetitionRepository;
import com.wintersports.repositories.competition.IBiathlonCompetitionRepository;
import com.wintersports.repositories.tournament.ITournamentRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BiathlonCompetitionService implements IBiathlonCompetitionService {

    private final IBiathlonCompetitionRepository biathlonCompetitionRepository;
    private final ICompetitionRepository competitionRepository;
    private final ITournamentRepository tournamentRepository;
    private final ModelMapper modelMapper;

    @Override
    public BiathlonCompetitionResponse create(CreateBiathlonCompetitionRequest request) {
        if (competitionRepository.existsByNameAndTournamentId(request.getName(), request.getTournamentId())) {
            throw new DuplicateResourceException("Competition with name '" + request.getName() + "' already exists in this tournament");
        }

        Tournament tournament = tournamentRepository.findById(request.getTournamentId())
                .orElseThrow(() -> new ResourceNotFoundException("Tournament with id " + request.getTournamentId() + " not found"));

        if (request.getDate().isBefore(tournament.getStartDate()) ||
                request.getDate().isAfter(tournament.getEndDate())) {
            throw new InvalidCompetitionDateException(
                    "Competition date must be between tournament start and end date"
            );
        }

        BiathlonCompetition entity = toEntity(request, tournament);
        return modelMapper.map(biathlonCompetitionRepository.save(entity), BiathlonCompetitionResponse.class);
    }

    @Override
    public BiathlonCompetitionResponse update(Long id, CreateBiathlonCompetitionRequest request) {
        BiathlonCompetition entity = biathlonCompetitionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BiathlonCompetition with id " + id + " not found"));

        if (competitionRepository.existsByNameAndTournamentIdAndIdNot(request.getName(), request.getTournamentId(), id)) {
            throw new DuplicateResourceException("Competition with name '" + request.getName() + "' already exists in this tournament");
        }

        Tournament tournament = tournamentRepository.findById(request.getTournamentId())
                .orElseThrow(() -> new ResourceNotFoundException("Tournament with id " + request.getTournamentId() + " not found"));

        if (request.getDate().isBefore(tournament.getStartDate()) ||
                request.getDate().isAfter(tournament.getEndDate())) {
            throw new InvalidCompetitionDateException(
                    "BiathlonCompetition date must be between tournament start and end date"
            );
        }

        updateEntity(entity, request, tournament);
        return modelMapper.map(biathlonCompetitionRepository.save(entity), BiathlonCompetitionResponse.class);
    }

    private BiathlonCompetition toEntity(CreateBiathlonCompetitionRequest request, Tournament tournament) {
        BiathlonCompetition entity = new BiathlonCompetition();
        updateEntity(entity, request, tournament);
        entity.setType("BIATHLON");
        return entity;
    }

    private void updateEntity(BiathlonCompetition entity, CreateBiathlonCompetitionRequest request, Tournament tournament) {
        entity.setTournament(tournament);
        entity.setName(request.getName());
        entity.setGender(request.getGender());
        entity.setMinAge(request.getMinAge());
        entity.setDate(request.getDate());
        entity.setRegistrationDeadlineDays(request.getRegistrationDeadlineDays());
        entity.setLapsCount(request.getLapsCount());
        entity.setShootingRounds(request.getShootingRounds());
        entity.setPenaltySeconds(request.getPenaltySeconds());
        entity.setType("BIATHLON");
    }
}