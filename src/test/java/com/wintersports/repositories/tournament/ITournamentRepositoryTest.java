package com.wintersports.repositories.tournament;

import com.wintersports.entities.Tournament;
import com.wintersports.entities.TournamentType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ITournamentRepositoryTest {

    @Autowired
    private ITournamentRepository tournamentRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void existsByName_returnsTrue_whenExists() {
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
        tournamentRepository.save(tournament);

        assertTrue(tournamentRepository.existsByName("Winter Olympics 2026"));
    }

    @Test
    void existsByName_returnsFalse_whenNotExists() {
        assertFalse(tournamentRepository.existsByName("NonExistent"));
    }
}