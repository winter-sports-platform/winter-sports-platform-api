package com.wintersports.repositories.medal;

import com.wintersports.entities.AthleteProfile;
import com.wintersports.entities.Medal;
import com.wintersports.entities.Tournament;
import com.wintersports.entities.TournamentType;
import com.wintersports.entities.User;
import com.wintersports.entities.competition.SlalomCompetition;
import com.wintersports.entities.result.SlalomResult;
import com.wintersports.enums.Gender;
import com.wintersports.enums.MedalType;
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
class IMedalRepositoryTest {

    @Autowired
    private IMedalRepository medalRepository;

    @Autowired
    private TestEntityManager entityManager;

    private SlalomCompetition competition;
    private SlalomResult result;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setUsername("ivan123");
        user.setEmail("ivan@test.com");
        user.setPassword("Test123");
        user.setRole(Role.ATHLETE);
        user.setStatus(UserStatus.APPROVED);
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

        result = new SlalomResult();
        result.setAthleteProfile(athleteProfile);
        result.setCompetition(competition);
        result.setRun1Time(BigDecimal.valueOf(45.234));
        result.setRun2Time(BigDecimal.valueOf(48.132));
        result.setTotalTime(BigDecimal.valueOf(93.366));
        result.setFinished(true);
        result.setType("SLALOM");
        entityManager.persist(result);

        Medal medal = new Medal();
        medal.setResult(result);
        medal.setCompetition(competition);
        medal.setType(MedalType.GOLD);
        entityManager.persist(medal);

        entityManager.flush();
    }

    @Test
    void findByCompetitionId_success() {
        List<Medal> medals = medalRepository.findByCompetitionId(competition.getId());
        assertFalse(medals.isEmpty());
        assertEquals(1, medals.size());
        assertEquals(MedalType.GOLD, medals.getFirst().getType());
    }

    @Test
    void findByCompetitionId_notFound() {
        List<Medal> medals = medalRepository.findByCompetitionId(999L);
        assertTrue(medals.isEmpty());
    }

    @Test
    void existsByCompetitionIdAndType_returnsTrue() {
        assertTrue(medalRepository.existsByCompetitionIdAndType(
                competition.getId(), MedalType.GOLD));
    }

    @Test
    void existsByCompetitionIdAndType_returnsFalse_wrongType() {
        assertFalse(medalRepository.existsByCompetitionIdAndType(
                competition.getId(), MedalType.SILVER));
    }

    @Test
    void existsByCompetitionIdAndType_returnsFalse_wrongCompetition() {
        assertFalse(medalRepository.existsByCompetitionIdAndType(
                999L, MedalType.GOLD));
    }
}