package com.wintersports.repositories.athleteprofile;

import com.wintersports.entities.AthleteProfile;
import com.wintersports.entities.User;
import com.wintersports.enums.Gender;
import com.wintersports.enums.Role;
import com.wintersports.enums.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class IAthleteProfileRepositoryTest {

    @Autowired
    private IAthleteProfileRepository athleteProfileRepository;

    @Autowired
    private TestEntityManager entityManager;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setUsername("ivan123");
        user.setEmail("ivan@test.com");
        user.setPassword("Test123");
        user.setRole(Role.ATHLETE);
        user.setStatus(UserStatus.PENDING);
        entityManager.persist(user);

        AthleteProfile athleteProfile = new AthleteProfile();
        athleteProfile.setUser(user);
        athleteProfile.setName("Ivan Ivanov");
        athleteProfile.setCountry("Bulgaria");
        athleteProfile.setGender(Gender.MALE);
        athleteProfile.setDateOfBirth(LocalDate.of(1995, 5, 15));
        entityManager.persist(athleteProfile);
        entityManager.flush();
    }

    @Test
    void findByUserUsername_success() {
        Optional<AthleteProfile> result = athleteProfileRepository.findByUserUsername("ivan123");
        assertTrue(result.isPresent());
        assertEquals("Ivan Ivanov", result.get().getName());
    }

    @Test
    void findByUserUsername_notFound() {
        Optional<AthleteProfile> result = athleteProfileRepository.findByUserUsername("nonexistent");
        assertFalse(result.isPresent());
    }
}