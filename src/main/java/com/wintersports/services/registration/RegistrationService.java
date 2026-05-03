package com.wintersports.services.registration;

import com.wintersports.configs.ModelMapperConfig;
import com.wintersports.dtos.requests.CreateRegistrationRequest;
import com.wintersports.dtos.requests.UpdateRegistrationStatusRequest;
import com.wintersports.dtos.responses.AthleteProfileResponse;
import com.wintersports.dtos.responses.RegistrationItemResponse;
import com.wintersports.dtos.responses.RegistrationResponse;
import com.wintersports.entities.AthleteProfile;
import com.wintersports.entities.Registration;
import com.wintersports.entities.competition.Competition;
import com.wintersports.enums.RegistrationStatus;
import com.wintersports.exceptions.DuplicateResourceException.DuplicateResourceException;
import com.wintersports.exceptions.InvalidRegistrationException.InvalidRegistrationException;
import com.wintersports.exceptions.RegistrationClosedException.RegistrationClosedException;
import com.wintersports.exceptions.ResourceNotFoundException.ResourceNotFoundException;
import com.wintersports.repositories.athleteprofile.IAthleteProfileRepository;
import com.wintersports.repositories.competition.ICompetitionRepository;
import com.wintersports.repositories.registration.IRegistrationRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RegistrationService implements IRegistrationService {

    private final IRegistrationRepository registrationRepository;
    private final IAthleteProfileRepository athleteProfileRepository;
    private final ICompetitionRepository competitionRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<RegistrationResponse> getAll() {
        return registrationRepository.findAll()
                .stream()
                .collect(Collectors.groupingBy(Registration::getAthleteProfile))
                .entrySet()
                .stream()
                .map(entry -> {
                    RegistrationResponse response = new RegistrationResponse();
                    response.setAthleteProfile(modelMapper.map(entry.getKey(), AthleteProfileResponse.class));
                    response.setRegistrations(ModelMapperConfig.mapList(
                            entry.getValue(),
                            RegistrationItemResponse.class,
                            modelMapper
                    ));
                    return response;
                })
                .toList();
    }

    @Override
    public RegistrationResponse create(CreateRegistrationRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        AthleteProfile athleteProfile = athleteProfileRepository.findByUserUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("AthleteProfile not found"));

        Competition competition = competitionRepository.findById(request.getCompetitionId())
                .orElseThrow(() -> new ResourceNotFoundException("Competition with id " + request.getCompetitionId() + " not found"));

        LocalDate deadline = competition.getDate().minusDays(competition.getRegistrationDeadlineDays());
        if (LocalDate.now().isAfter(deadline)) {
            throw new RegistrationClosedException("Registration for this competition is closed");
        }

        if (athleteProfile.getGender() != competition.getGender()) {
            throw new InvalidRegistrationException("Your gender does not match the competition gender");
        }

        int age = Period.between(athleteProfile.getDateOfBirth(), competition.getDate()).getYears();
        if (age < competition.getMinAge()) {
            throw new InvalidRegistrationException("You must be at least " + competition.getMinAge() + " years old to register");
        }

        if (registrationRepository.existsByAthleteProfileIdAndCompetitionId(athleteProfile.getId(), competition.getId())) {
            throw new DuplicateResourceException("You are already registered for this competition");
        }

        Registration registration = new Registration();
        registration.setAthleteProfile(athleteProfile);
        registration.setCompetition(competition);
        registration.setStatus(RegistrationStatus.PENDING);

        return modelMapper.map(registrationRepository.save(registration), RegistrationResponse.class);
    }

    @Override
    public RegistrationItemResponse updateStatus(Long id, UpdateRegistrationStatusRequest request) {
        Registration registration = registrationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Registration with id " + id + " not found"));

        registration.setStatus(request.getStatus());

        return modelMapper.map(registrationRepository.save(registration), RegistrationItemResponse.class);
    }

    @Override
    public void delete(Long id) {
        registrationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Registration with id " + id + " not found"));
        registrationRepository.deleteById(id);
    }

    @Override
    public List<RegistrationItemResponse> getByCompetition(Long competitionId) {
        return registrationRepository.findByCompetitionId(competitionId)
                .stream()
                .map(r -> modelMapper.map(r, RegistrationItemResponse.class))
                .toList();
    }

    @Override
    public RegistrationResponse getMyRegistrations() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        AthleteProfile athleteProfile = athleteProfileRepository.findByUserUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("AthleteProfile not found"));

        List<Registration> registrations = registrationRepository.findByAthleteProfileId(athleteProfile.getId());

        RegistrationResponse response = new RegistrationResponse();
        response.setAthleteProfile(modelMapper.map(athleteProfile, AthleteProfileResponse.class));
        response.setRegistrations(ModelMapperConfig.mapList(registrations, RegistrationItemResponse.class, modelMapper));
        return response;
    }
}