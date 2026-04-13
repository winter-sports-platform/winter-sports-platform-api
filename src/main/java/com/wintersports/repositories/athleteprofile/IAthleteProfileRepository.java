package com.wintersports.repositories.athleteprofile;

import com.wintersports.entities.AthleteProfile;
import com.wintersports.repositories.IBaseRepository;

import java.util.Optional;

public interface IAthleteProfileRepository extends IBaseRepository<AthleteProfile, Long> {
    Optional<AthleteProfile> findByUserUsername(String username);
}