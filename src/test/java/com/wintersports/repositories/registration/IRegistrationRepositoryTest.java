package com.wintersports.repositories.registration;

import com.wintersports.entities.AthleteProfile;
import com.wintersports.entities.Registration;
import com.wintersports.entities.Tournament;
import com.wintersports.entities.TournamentType;
import com.wintersports.entities.User;
import com.wintersports.entities.competition.SlalomCompetition;
import com.wintersports.enums.Gender;
import com.wintersports.enums.RegistrationStatus;
import com.wintersports.enums.Role;
import com.wintersports.enums.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class IRegistrationRepositoryTest {

    @Autowired
    private IRegistrationRepository registrationRepository;

    @Autowired
    private TestEntityManager entityManager;

    private AthleteProfile athleteProfile;
    private SlalomCompetition competition;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setUsername("ivan123");
        user.setEmail("ivan@test.com");
        user.setPassword("Test123");
        user.setRole(Role.ATHLETE);
        user.setStatus(UserStatus.APPROVED);
        entityManager.persist(user);

        athleteProfile = new AthleteProfile();
        athleteProfile.setUser(user);
        athleteProfile.setName("Ivan Ivanov");
        athleteProfile.setCountry("Bulgaria");
        athleteProfile.setGender(Gender.MALE);
        athleteProfile.setDateOfBirth(LocalDate.of(1995, 5, 15));
        entityManager.persist(athleteProfile);

        TournamentType type = new TournamentType();
        type.setName("Olympics");
        entityManager.persist(type);

        Tournament tournament = new Tournament();
        tournament.setName("Winter Olympics 2026");
        tournament.setYear(2026);
        tournament.setLocation("Milano");
        tournament.setStartDate(LocalDate.of(2026, 2, 1));
        tournament.setEndDate(LocalDate.of(2026, 2, 20));
        tournament.setType(type);
        entityManager.persist(tournament);

        competition = new SlalomCompetition();
        competition.setName("Slalom Men");
        competition.setGender(Gender.MALE);
        competition.setMinAge(18);
        competition.setDate(LocalDate.of(2026, 5, 15));
        competition.setRegistrationDeadlineDays(14);
        competition.setMaxRun2Participants(30);
        competition.setType("SLALOM");
        competition.setTournament(tournament);
        entityManager.persist(competition);

        Registration registration = new Registration();
        registration.setAthleteProfile(athleteProfile);
        registration.setCompetition(competition);
        registration.setStatus(RegistrationStatus.PENDING);
        entityManager.persist(registration);

        entityManager.flush();
    }

    @Test
    void existsByAthleteProfileIdAndCompetitionId_returnsTrue() {
        assertTrue(registrationRepository.existsByAthleteProfileIdAndCompetitionId(
                athleteProfile.getId(), competition.getId()));
    }

    @Test
    void existsByAthleteProfileIdAndCompetitionId_returnsFalse() {
        assertFalse(registrationRepository.existsByAthleteProfileIdAndCompetitionId(
                athleteProfile.getId(), 999L));
    }

    @Test
    void findByCompetitionId_success() {
        List<Registration> registrations = registrationRepository.findByCompetitionId(competition.getId());
        assertFalse(registrations.isEmpty());
        assertEquals(1, registrations.size());
    }

    @Test
    void findByCompetitionId_notFound() {
        List<Registration> registrations = registrationRepository.findByCompetitionId(999L);
        assertTrue(registrations.isEmpty());
    }

    @Test
    void findByAthleteProfileId_success() {
        List<Registration> registrations = registrationRepository.findByAthleteProfileId(athleteProfile.getId());
        assertFalse(registrations.isEmpty());
        assertEquals(1, registrations.size());
    }

    @Test
    void findByAthleteProfileId_notFound() {
        List<Registration> registrations = registrationRepository.findByAthleteProfileId(999L);
        assertTrue(registrations.isEmpty());
    }
}