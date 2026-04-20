package com.wintersports.services.result;

import com.wintersports.dtos.requests.CreateSlalomResultRequest;
import com.wintersports.dtos.requests.UpdateSlalomResultRequest;
import com.wintersports.dtos.responses.SlalomResultResponse;
import com.wintersports.entities.AthleteProfile;
import com.wintersports.entities.competition.SlalomCompetition;
import com.wintersports.entities.result.SlalomResult;
import com.wintersports.enums.RegistrationStatus;
import com.wintersports.exceptions.DuplicateResourceException.DuplicateResourceException;
import com.wintersports.exceptions.InvalidRegistrationException.InvalidRegistrationException;
import com.wintersports.exceptions.ResourceNotFoundException.ResourceNotFoundException;
import com.wintersports.repositories.athleteprofile.IAthleteProfileRepository;
import com.wintersports.repositories.competition.ISlalomCompetitionRepository;
import com.wintersports.repositories.registration.IRegistrationRepository;
import com.wintersports.repositories.result.ICompetitionResultRepository;
import com.wintersports.repositories.result.ISlalomResultRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SlalomResultService implements ISlalomResultService {

    private final ISlalomResultRepository slalomResultRepository;
    private final ISlalomCompetitionRepository slalomCompetitionRepository;
    private final IAthleteProfileRepository athleteProfileRepository;
    private final ICompetitionResultRepository competitionResultRepository;
    private final IRegistrationRepository registrationRepository;
    private final ModelMapper modelMapper;

    @Override
    public SlalomResultResponse create(CreateSlalomResultRequest request) {
        AthleteProfile athleteProfile = athleteProfileRepository.findById(request.getAthleteProfileId())
                .orElseThrow(() -> new ResourceNotFoundException("AthleteProfile with id " + request.getAthleteProfileId() + " not found"));

        SlalomCompetition competition = slalomCompetitionRepository.findById(request.getCompetitionId())
                .orElseThrow(() -> new ResourceNotFoundException("SlalomCompetition with id " + request.getCompetitionId() + " not found"));

        if (!registrationRepository.existsByAthleteProfileIdAndCompetitionIdAndStatus(
                request.getAthleteProfileId(), request.getCompetitionId(), RegistrationStatus.APPROVED)) {
            throw new InvalidRegistrationException("Athlete does not have an approved registration for this competition");
        }

        if (competitionResultRepository.existsByAthleteProfileIdAndCompetitionId(
                request.getAthleteProfileId(), request.getCompetitionId())) {
            throw new DuplicateResourceException("Result already exists for this athlete and competition");
        }

        SlalomResult entity = new SlalomResult();
        entity.setAthleteProfile(athleteProfile);
        entity.setCompetition(competition);
        entity.setRun1Time(request.getRun1Time());
        entity.setFinished(false);
        entity.setType("SLALOM");

        return modelMapper.map(slalomResultRepository.save(entity), SlalomResultResponse.class);
    }

    @Override
    public SlalomResultResponse update(Long id, UpdateSlalomResultRequest request) {
        SlalomResult entity = slalomResultRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SlalomResult with id " + id + " not found"));

        SlalomCompetition competition = slalomCompetitionRepository.findById(entity.getCompetition().getId())
                .orElseThrow(() -> new ResourceNotFoundException("SlalomCompetition not found"));

        List<SlalomResult> qualifiers = slalomResultRepository
                .findByCompetition_IdOrderByRun1TimeAsc(entity.getCompetition().getId())
                .stream()
                .filter(r -> r.getRun1Time() != null)
                .limit(competition.getMaxRun2Participants())
                .toList();

        boolean isQualified = qualifiers.stream().anyMatch(r -> r.getId().equals(id));
        if (!isQualified) {
            throw new InvalidRegistrationException("Athlete did not qualify for run 2");
        }

        entity.setRun2Time(request.getRun2Time());

        if (request.getRun2Time() != null && entity.getRun1Time() != null) {
            entity.setTotalTime(entity.getRun1Time().add(request.getRun2Time()));
            entity.setFinished(true);
        }

        return modelMapper.map(slalomResultRepository.save(entity), SlalomResultResponse.class);
    }

    @Override
    public List<SlalomResultResponse> getRun2Qualifiers(Long competitionId) {
        SlalomCompetition competition = slalomCompetitionRepository.findById(competitionId)
                .orElseThrow(() -> new ResourceNotFoundException("SlalomCompetition with id " + competitionId + " not found"));

        return slalomResultRepository.findByCompetition_IdOrderByRun1TimeAsc(competitionId)
                .stream()
                .filter(r -> r.getRun1Time() != null)
                .limit(competition.getMaxRun2Participants())
                .map(r -> modelMapper.map(r, SlalomResultResponse.class))
                .toList();
    }
}
