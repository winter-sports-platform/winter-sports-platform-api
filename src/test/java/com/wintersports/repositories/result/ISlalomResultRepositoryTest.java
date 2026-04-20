package com.wintersports.repositories.result;

import com.wintersports.entities.AthleteProfile;
import com.wintersports.entities.Tournament;
import com.wintersports.entities.TournamentType;
import com.wintersports.entities.User;
import com.wintersports.entities.competition.Competition;
import com.wintersports.entities.competition.SlalomCompetition;
import com.wintersports.entities.result.SlalomResult;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ISlalomResultRepositoryTest {

    @Autowired
    private ISlalomResultRepository slalomResultRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Competition competition;

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

        SlalomCompetition competition = new SlalomCompetition();
        competition.setName("Slalom Men");
        competition.setGender(Gender.MALE);
        competition.setMinAge(18);
        competition.setDate(LocalDate.of(2026, 5, 15));
        competition.setRegistrationDeadlineDays(14);
        competition.setMaxRun2Participants(30);
        competition.setType("SLALOM");
        competition.setTournament(tournament);
        entityManager.persist(competition);

        SlalomResult result1 = new SlalomResult();
        result1.setAthleteProfile(athleteProfile);
        result1.setCompetition(competition);
        result1.setRun1Time(BigDecimal.valueOf(45.234));
        result1.setFinished(false);
        result1.setType("SLALOM");
        entityManager.persist(result1);

        this.competition = competition;

        entityManager.flush();
    }

    @Test
    void findByCompetition_IdOrderByRun1TimeAsc_success() {
        List<SlalomResult> results = slalomResultRepository.findByCompetition_IdOrderByRun1TimeAsc(competition.getId());
        assertFalse(results.isEmpty());
    }

    @Test
    void findByCompetition_IdOrderByRun1TimeAsc_notFound() {
        List<SlalomResult> results = slalomResultRepository.findByCompetition_IdOrderByRun1TimeAsc(999L);
        assertTrue(results.isEmpty());
    }
}