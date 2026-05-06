package com.wintersports.services.medal;

import com.wintersports.dtos.requests.CreateMedalRequest;
import com.wintersports.dtos.responses.MedalCountryResponse;
import com.wintersports.dtos.responses.MedalResponse;
import com.wintersports.entities.Medal;
import com.wintersports.entities.competition.Competition;
import com.wintersports.entities.result.CompetitionResult;
import com.wintersports.exceptions.DuplicateResourceException.DuplicateResourceException;
import com.wintersports.exceptions.ResourceNotFoundException.ResourceNotFoundException;
import com.wintersports.repositories.medal.IMedalRepository;
import com.wintersports.repositories.competition.ICompetitionRepository;
import com.wintersports.repositories.result.ICompetitionResultRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MedalService implements IMedalService {

    private final IMedalRepository medalRepository;
    private final ICompetitionResultRepository competitionResultRepository;
    private final ICompetitionRepository competitionRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<MedalResponse> getAll() {
        return medalRepository.findAll()
                .stream()
                .map(medal -> modelMapper.map(medal, MedalResponse.class))
                .toList();
    }

    @Override
    public List<MedalCountryResponse> getByCountry() {
        return medalRepository.findAll()
                .stream()
                .collect(Collectors.groupingBy(
                        medal -> medal.getResult().getAthleteProfile().getCountry()
                ))
                .entrySet()
                .stream()
                .map(entry -> {
                    String country = entry.getKey();
                    List<Medal> medals = entry.getValue();
                    long gold = medals.stream().filter(m -> m.getType().name().equals("GOLD")).count();
                    long silver = medals.stream().filter(m -> m.getType().name().equals("SILVER")).count();
                    long bronze = medals.stream().filter(m -> m.getType().name().equals("BRONZE")).count();
                    return new MedalCountryResponse(country, gold, silver, bronze, gold + silver + bronze);
                })
                .sorted((a, b) -> Long.compare(b.getGold(), a.getGold()))
                .toList();
    }

    @Override
    public MedalResponse create(CreateMedalRequest request) {
        CompetitionResult result = competitionResultRepository.findById(request.getResultId())
                .orElseThrow(() -> new ResourceNotFoundException("Result with id " + request.getResultId() + " not found"));

        Competition competition = competitionRepository.findById(request.getCompetitionId())
                .orElseThrow(() -> new ResourceNotFoundException("Competition with id " + request.getCompetitionId() + " not found"));

        if (!result.isFinished()) {
            throw new IllegalArgumentException("Cannot award medal to athlete who did not finish");
        }

        if (medalRepository.existsByCompetitionIdAndType(request.getCompetitionId(), request.getType())) {
            throw new DuplicateResourceException("Medal of type " + request.getType() + " already exists for this competition");
        }

        Medal medal = new Medal();
        medal.setResult(result);
        medal.setCompetition(competition);
        medal.setType(request.getType());

        return modelMapper.map(medalRepository.save(medal), MedalResponse.class);
    }

    @Override
    public void delete(Long id) {
        medalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Medal with id " + id + " not found"));
        medalRepository.deleteById(id);
    }
}