package com.wintersports.services.athleteprofile;

import com.wintersports.configs.ModelMapperConfig;
import com.wintersports.dtos.requests.UpdateAthleteProfileRequest;
import com.wintersports.dtos.responses.AthleteProfileResponse;
import com.wintersports.entities.AthleteProfile;
import com.wintersports.exceptions.ResourceNotFoundException.ResourceNotFoundException;
import com.wintersports.exceptions.UnauthorizedAccessException.UnauthorizedAccessException;
import com.wintersports.repositories.athleteprofile.IAthleteProfileRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AthleteProfileService implements IAthleteProfileService {

    private final IAthleteProfileRepository athleteProfileRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<AthleteProfileResponse> getAll() {
        return ModelMapperConfig.mapList(
                athleteProfileRepository.findAll(),
                AthleteProfileResponse.class,
                modelMapper
        );
    }

    @Override
    public AthleteProfileResponse getById(Long id) {
        return modelMapper.map(findById(id), AthleteProfileResponse.class);
    }

    @Override
    public AthleteProfileResponse update(Long id, UpdateAthleteProfileRequest request) {
        AthleteProfile entity = findById(id);

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        AthleteProfile loggedInAthlete = athleteProfileRepository.findByUserUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("AthleteProfile not found"));

        if (!loggedInAthlete.getId().equals(entity.getId())) {
            throw new UnauthorizedAccessException("You can only update your own profile");
        }

        modelMapper.map(request, entity);

        return modelMapper.map(athleteProfileRepository.save(entity), AthleteProfileResponse.class);
    }

    @Override
    public void delete(Long id) {
        //Check if athlete exist
        findById(id);

        athleteProfileRepository.deleteById(id);
    }

    private AthleteProfile findById(Long id) {
        return athleteProfileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AthleteProfile with id " + id + " not found"));
    }
}
