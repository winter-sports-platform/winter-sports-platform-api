package com.wintersports.services.statistics;

import com.wintersports.dtos.responses.AthleteProfileResponse;
import com.wintersports.dtos.responses.CompetitionResultResponse;
import com.wintersports.dtos.responses.MedalCountryResponse;
import com.wintersports.entities.AthleteProfile;
import com.wintersports.entities.Medal;
import com.wintersports.entities.result.BiathlonResult;
import com.wintersports.entities.result.CompetitionResult;
import com.wintersports.entities.result.SlalomResult;
import com.wintersports.exceptions.ResourceNotFoundException.ResourceNotFoundException;
import com.wintersports.repositories.competition.ICompetitionRepository;
import com.wintersports.repositories.medal.IMedalRepository;
import com.wintersports.repositories.result.ICompetitionResultRepository;
import com.wintersports.services.medal.IMedalService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatisticsService implements IStatisticsService {

    private final ICompetitionResultRepository competitionResultRepository;
    private final ICompetitionRepository competitionRepository;
    private final IMedalRepository medalRepository;
    private final IMedalService medalService;
    private final ModelMapper modelMapper;

    @Override
    public List<CompetitionResultResponse> getRankings(Long competitionId) {
        if (!competitionRepository.existsById(competitionId)) {
            throw new ResourceNotFoundException("Competition with id " + competitionId + " not found");
        }

        return competitionResultRepository.findByCompetitionId(competitionId)
                .stream()
                .sorted(Comparator.comparing(CompetitionResult::isFinished).reversed()
                        .thenComparing(r -> {
                            if (r instanceof SlalomResult s) return s.getTotalTime() != null ? s.getTotalTime() : new BigDecimal(Integer.MAX_VALUE);
                            if (r instanceof BiathlonResult b) return b.getTotalTime();
                            return new BigDecimal(Integer.MAX_VALUE);
                        }))
                .map(r -> modelMapper.map(r, CompetitionResultResponse.class))
                .toList();
    }

    @Override
    public List<MedalCountryResponse> getMedalsByCountry() {
        return medalService.getByCountry();
    }

    @Override
    public double getAverageAge(Long competitionId) {
        if (!competitionRepository.existsById(competitionId)) {
            throw new ResourceNotFoundException("Competition with id " + competitionId + " not found");
        }

        List<CompetitionResult> results = competitionResultRepository.findByCompetitionId(competitionId);

        if (results.isEmpty()) {
            return 0.0;
        }

        return results.stream()
                .mapToInt(r -> Period.between(
                        r.getAthleteProfile().getDateOfBirth(),
                        LocalDate.now()
                ).getYears())
                .average()
                .orElse(0.0);
    }

    @Override
    public Map<String, AthleteProfileResponse> getYoungestOldestMedalist() {
        List<AthleteProfile> medalists = medalRepository.findAll()
                .stream()
                .map(medal -> medal.getResult().getAthleteProfile())
                .distinct()
                .toList();

        if (medalists.isEmpty()) {
            return Map.of();
        }

        AthleteProfile youngest = medalists.stream()
                .max(Comparator.comparing(AthleteProfile::getDateOfBirth))
                .orElseThrow();

        AthleteProfile oldest = medalists.stream()
                .min(Comparator.comparing(AthleteProfile::getDateOfBirth))
                .orElseThrow();

        return Map.of(
                "youngest", modelMapper.map(youngest, AthleteProfileResponse.class),
                "oldest", modelMapper.map(oldest, AthleteProfileResponse.class)
        );
    }
}