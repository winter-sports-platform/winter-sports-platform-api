package com.wintersports.repositories.result;

import com.wintersports.entities.AthleteProfile;
import com.wintersports.entities.Tournament;
import com.wintersports.entities.TournamentType;
import com.wintersports.entities.User;
import com.wintersports.entities.competition.BiathlonCompetition;
import com.wintersports.entities.result.BiathlonResult;
import com.wintersports.enums.Gender;
import com.wintersports.enums.Role;
import com.wintersports.enums.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ICompetitionResultRepositoryTest {

    @Autowired
    private ICompetitionResultRepository competitionResultRepository;

    @Autowired
    private TestEntityManager entityManager;

    private AthleteProfile athleteProfile;
    private BiathlonCompetition competition;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setUsername("ivan123");
        user.setEmail("ivan@test.com");
        user.setPassword("Test123");
        user.setRole(Role.ATHLETE);
        user.setStatus(UserStatus.PENDING);
        entityManager.persist(user);

        athleteProfile = new AthleteProfile();
        athleteProfile.setUser(user);
        athleteProfile.setName("Ivan Ivanov");
        athleteProfile.setCountry("Bulgaria");
        athleteProfile.setGender(Gender.MALE);
        athleteProfile.setDateOfBirth(LocalDate.of(1995, 5, 15));
        entityManager.persist(athleteProfile);

        TournamentType tournamentType = new TournamentType();
        tournamentType.setName("Olympics");
        entityManager.persist(tournamentType);

        Tournament tournament = new Tournament();
        tournament.setName("Winter Olympics 2026");
        tournament.setYear(2026);
        tournament.setLocation("Milano");
        tournament.setStartDate(LocalDate.of(2026, 2, 1));
        tournament.setEndDate(LocalDate.of(2026, 2, 20));
        tournament.setType(tournamentType);
        entityManager.persist(tournament);

        competition = new BiathlonCompetition();
        competition.setName("Biathlon Men");
        competition.setGender(Gender.MALE);
        competition.setMinAge(18);
        competition.setDate(LocalDate.of(2026, 5, 15));
        competition.setRegistrationDeadlineDays(14);
        competition.setLapsCount(5);
        competition.setShootingRounds(4);
        competition.setPenaltySeconds(60);
        competition.setType("BIATHLON");
        competition.setTournament(tournament);
        entityManager.persist(competition);

        BiathlonResult result = new BiathlonResult();
        result.setAthleteProfile(athleteProfile);
        result.setCompetition(competition);
        result.setSkiTime(BigDecimal.valueOf(120.456));
        result.setMissedShots(3);
        result.setPenaltyTime(BigDecimal.valueOf(180.0));
        result.setTotalTime(BigDecimal.valueOf(300.456));
        result.setFinished(true);
        result.setType("BIATHLON");
        entityManager.persist(result);

        entityManager.flush();
    }

    @Test
    void existsByAthleteProfileIdAndCompetitionId_returnsTrue() {
        assertTrue(competitionResultRepository.existsByAthleteProfileIdAndCompetitionId(
                athleteProfile.getId(), competition.getId()));
    }

    @Test
    void existsByAthleteProfileIdAndCompetitionId_returnsFalse() {
        assertFalse(competitionResultRepository.existsByAthleteProfileIdAndCompetitionId(
                athleteProfile.getId(), 999L));
    }

    @Test
    void findByCompetitionId_success() {
        var results = competitionResultRepository.findByCompetitionId(competition.getId());
        assertFalse(results.isEmpty());
        assertEquals(1, results.size());
    }

    @Test
    void findByCompetitionId_notFound() {
        var results = competitionResultRepository.findByCompetitionId(999L);
        assertTrue(results.isEmpty());
    }
}