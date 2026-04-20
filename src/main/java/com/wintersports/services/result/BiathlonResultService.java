package com.wintersports.services.result;

import com.wintersports.dtos.requests.CreateBiathlonResultRequest;
import com.wintersports.dtos.requests.UpdateBiathlonResultRequest;
import com.wintersports.dtos.responses.BiathlonResultResponse;
import com.wintersports.entities.AthleteProfile;
import com.wintersports.entities.competition.BiathlonCompetition;
import com.wintersports.entities.result.BiathlonResult;
import com.wintersports.enums.RegistrationStatus;
import com.wintersports.exceptions.DuplicateResourceException.DuplicateResourceException;
import com.wintersports.exceptions.InvalidRegistrationException.InvalidRegistrationException;
import com.wintersports.exceptions.ResourceNotFoundException.ResourceNotFoundException;
import com.wintersports.repositories.athleteprofile.IAthleteProfileRepository;
import com.wintersports.repositories.competition.IBiathlonCompetitionRepository;
import com.wintersports.repositories.registration.IRegistrationRepository;
import com.wintersports.repositories.result.IBiathlonResultRepository;
import com.wintersports.repositories.result.ICompetitionResultRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class BiathlonResultService implements IBiathlonResultService {

    private final IBiathlonResultRepository biathlonResultRepository;
    private final IBiathlonCompetitionRepository biathlonCompetitionRepository;
    private final IAthleteProfileRepository athleteProfileRepository;
    private final ICompetitionResultRepository competitionResultRepository;
    private final IRegistrationRepository registrationRepository;
    private final ModelMapper modelMapper;

    @Override
    public BiathlonResultResponse create(CreateBiathlonResultRequest request) {
        AthleteProfile athleteProfile = athleteProfileRepository.findById(request.getAthleteProfileId())
                .orElseThrow(() -> new ResourceNotFoundException("AthleteProfile with id " + request.getAthleteProfileId() + " not found"));

        BiathlonCompetition competition = biathlonCompetitionRepository.findById(request.getCompetitionId())
                .orElseThrow(() -> new ResourceNotFoundException("BiathlonCompetition with id " + request.getCompetitionId() + " not found"));

        if (!registrationRepository.existsByAthleteProfileIdAndCompetitionIdAndStatus(
                request.getAthleteProfileId(), request.getCompetitionId(), RegistrationStatus.APPROVED)) {
            throw new InvalidRegistrationException("Athlete does not have an approved registration for this competition");
        }

        if (competitionResultRepository.existsByAthleteProfileIdAndCompetitionId(
                request.getAthleteProfileId(), request.getCompetitionId())) {
            throw new DuplicateResourceException("Result already exists for this athlete and competition");
        }

        BigDecimal penaltyTime = calculatePenaltyTime(request.getMissedShots(), competition.getPenaltySeconds());

        BiathlonResult entity = new BiathlonResult();
        entity.setAthleteProfile(athleteProfile);
        entity.setCompetition(competition);
        entity.setSkiTime(request.getSkiTime());
        entity.setMissedShots(request.getMissedShots());
        entity.setPenaltyTime(penaltyTime);
        entity.setTotalTime(request.getSkiTime().add(penaltyTime));
        entity.setFinished(true);
        entity.setType("BIATHLON");

        return modelMapper.map(biathlonResultRepository.save(entity), BiathlonResultResponse.class);
    }

    @Override
    public BiathlonResultResponse update(Long id, UpdateBiathlonResultRequest request) {
        BiathlonResult entity = biathlonResultRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BiathlonResult with id " + id + " not found"));

        BiathlonCompetition competition = biathlonCompetitionRepository.findById(entity.getCompetition().getId())
                .orElseThrow(() -> new ResourceNotFoundException("BiathlonCompetition not found"));

        BigDecimal penaltyTime = BigDecimal.valueOf(request.getMissedShots())
                .multiply(BigDecimal.valueOf(competition.getPenaltySeconds()));

        entity.setSkiTime(request.getSkiTime());
        entity.setMissedShots(request.getMissedShots());
        entity.setPenaltyTime(penaltyTime);
        entity.setTotalTime(request.getSkiTime().add(penaltyTime));
        entity.setFinished(request.isFinished());

        return modelMapper.map(biathlonResultRepository.save(entity), BiathlonResultResponse.class);
    }

    private BigDecimal calculatePenaltyTime(int missedShots, int penaltySeconds) {
        return BigDecimal.valueOf(missedShots)
                .multiply(BigDecimal.valueOf(penaltySeconds));
    }
}
