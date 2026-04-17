package com.wintersports.repositories.tournamenttype;

import com.wintersports.entities.TournamentType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ITournamentTypeRepositoryTest {

    @Autowired
    private ITournamentTypeRepository tournamentTypeRepository;

    @Test
    void existsByName_returnsTrue_whenExists() {
        TournamentType type = new TournamentType();
        type.setName("Olympics");
        tournamentTypeRepository.save(type);

        assertTrue(tournamentTypeRepository.existsByName("Olympics"));
    }

    @Test
    void existsByName_returnsFalse_whenNotExists() {
        assertFalse(tournamentTypeRepository.existsByName("NonExistent"));
    }
}