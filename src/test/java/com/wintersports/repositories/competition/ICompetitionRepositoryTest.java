package com.wintersports.repositories.competition;

import com.wintersports.entities.Tournament;
import com.wintersports.entities.TournamentType;
import com.wintersports.entities.competition.BiathlonCompetition;
import com.wintersports.entities.competition.SlalomCompetition;
import com.wintersports.enums.Gender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ICompetitionRepositoryTest {

    @Autowired
    private ICompetitionRepository competitionRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Tournament tournament;
    private SlalomCompetition slalom;
    private BiathlonCompetition biathlon;

    @BeforeEach
    void setUp() {
        TournamentType type = new TournamentType();
        type.setName("Olympics");
        entityManager.persist(type);

        tournament = new Tournament();
        tournament.setName("Winter Olympics 2026");
        tournament.setYear(2026);
        tournament.setLocation("Milano");
        tournament.setStartDate(LocalDate.of(2026, 2, 1));
        tournament.setEndDate(LocalDate.of(2026, 2, 20));
        tournament.setType(type);
        entityManager.persist(tournament);

        slalom = new SlalomCompetition();
        slalom.setName("Slalom Men");
        slalom.setGender(Gender.MALE);
        slalom.setMinAge(18);
        slalom.setDate(LocalDate.of(2026, 2, 5));
        slalom.setRegistrationDeadlineDays(14);
        slalom.setMaxRun2Participants(30);
        slalom.setType("SLALOM");
        slalom.setTournament(tournament);
        entityManager.persist(slalom);

        biathlon = new BiathlonCompetition();
        biathlon.setName("Biathlon Men");
        biathlon.setGender(Gender.MALE);
        biathlon.setMinAge(18);
        biathlon.setDate(LocalDate.of(2026, 2, 6));
        biathlon.setRegistrationDeadlineDays(14);
        biathlon.setLapsCount(5);
        biathlon.setShootingRounds(4);
        biathlon.setPenaltySeconds(60);
        biathlon.setType("BIATHLON");
        biathlon.setTournament(tournament);
        entityManager.persist(biathlon);

        entityManager.flush();
    }

    @Test
    void existsByNameAndTournamentIdAndIdNot_returnsTrue() {
        assertTrue(competitionRepository.existsByNameAndTournamentIdAndIdNot(
                "Slalom Men", tournament.getId(), 999L));
    }

    @Test
    void existsByNameAndTournamentIdAndIdNot_returnsFalse_sameId() {
        assertFalse(competitionRepository.existsByNameAndTournamentIdAndIdNot(
                "Slalom Men", tournament.getId(), slalom.getId()));
    }

    @Test
    void existsByNameAndTournamentIdAndIdNot_returnsFalse_differentName() {
        assertFalse(competitionRepository.existsByNameAndTournamentIdAndIdNot(
                "NonExistent", tournament.getId(), 999L));
    }

    @Test
    void existsByCompetitionId_returnsTrue() {
        assertTrue(competitionRepository.existsById(slalom.getId()));
        assertTrue(competitionRepository.existsById(biathlon.getId()));
    }

    @Test
    void existsByCompetitionId_returnsFalse() {
        assertFalse(competitionRepository.existsById(999L));
    }

    @Test
    void existsByNameAndTournamentId_returnsTrue() {
        assertTrue(competitionRepository.existsByNameAndTournamentId(
                "Slalom Men", tournament.getId()));
    }

    @Test
    void existsByNameAndTournamentId_returnsFalse_wrongName() {
        assertFalse(competitionRepository.existsByNameAndTournamentId(
                "NonExistent", tournament.getId()));
    }

    @Test
    void existsByNameAndTournamentId_returnsFalse_wrongTournament() {
        assertFalse(competitionRepository.existsByNameAndTournamentId(
                "Slalom Men", 999L));
    }

    @Test
    void findAll_returnsBothTypes() {
        var competitions = competitionRepository.findAll();
        assertEquals(2, competitions.size());
    }

    @Test
    void findById_slalom_success() {
        var found = competitionRepository.findById(slalom.getId());
        assertTrue(found.isPresent());
        assertEquals("Slalom Men", found.get().getName());
    }

    @Test
    void findById_biathlon_success() {
        var found = competitionRepository.findById(biathlon.getId());
        assertTrue(found.isPresent());
        assertEquals("Biathlon Men", found.get().getName());
    }

    @Test
    void findById_notFound() {
        var found = competitionRepository.findById(999L);
        assertTrue(found.isEmpty());
    }

    @Test
    void deleteById_success() {
        competitionRepository.deleteById(slalom.getId());
        assertFalse(competitionRepository.existsById(slalom.getId()));
    }
}