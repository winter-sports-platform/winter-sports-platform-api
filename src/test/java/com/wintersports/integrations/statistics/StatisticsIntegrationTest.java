package com.wintersports.integrations.statistics;

import com.wintersports.dtos.requests.LoginRequest;
import com.wintersports.dtos.responses.AuthResponse;
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
import com.wintersports.repositories.athleteprofile.IAthleteProfileRepository;
import com.wintersports.repositories.competition.ISlalomCompetitionRepository;
import com.wintersports.repositories.medal.IMedalRepository;
import com.wintersports.repositories.result.ISlalomResultRepository;
import com.wintersports.repositories.tournament.ITournamentRepository;
import com.wintersports.repositories.tournamenttype.ITournamentTypeRepository;
import com.wintersports.repositories.user.IUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class StatisticsIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private IAthleteProfileRepository athleteProfileRepository;

    @Autowired
    private ITournamentTypeRepository tournamentTypeRepository;

    @Autowired
    private ITournamentRepository tournamentRepository;

    @Autowired
    private ISlalomCompetitionRepository slalomCompetitionRepository;

    @Autowired
    private ISlalomResultRepository slalomResultRepository;

    @Autowired
    private IMedalRepository medalRepository;

    private MockMvc mockMvc;
    private SlalomCompetition competition;

    @BeforeEach
    void setUp() throws Exception {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();

        User athleteUser = new User();
        athleteUser.setUsername("testathlete");
        athleteUser.setEmail("athlete@test.com");
        athleteUser.setPassword(passwordEncoder.encode("Test123"));
        athleteUser.setRole(Role.ATHLETE);
        athleteUser.setStatus(UserStatus.APPROVED);
        userRepository.save(athleteUser);

        AthleteProfile athleteProfile = new AthleteProfile();
        athleteProfile.setUser(athleteUser);
        athleteProfile.setName("Test Athlete");
        athleteProfile.setCountry("Bulgaria");
        athleteProfile.setGender(Gender.MALE);
        athleteProfile.setDateOfBirth(LocalDate.of(1995, 5, 15));
        athleteProfileRepository.save(athleteProfile);

        TournamentType type = new TournamentType();
        type.setName("Olympics");
        tournamentTypeRepository.save(type);

        Tournament tournament = new Tournament();
        tournament.setName("Winter Olympics 2027");
        tournament.setYear(2027);
        tournament.setLocation("Milano");
        tournament.setStartDate(LocalDate.of(2027, 2, 1));
        tournament.setEndDate(LocalDate.of(2027, 2, 20));
        tournament.setType(type);
        tournamentRepository.save(tournament);

        competition = new SlalomCompetition();
        competition.setName("Slalom Men");
        competition.setGender(Gender.MALE);
        competition.setMinAge(18);
        competition.setDate(LocalDate.of(2027, 2, 5));
        competition.setRegistrationDeadlineDays(14);
        competition.setMaxRun2Participants(30);
        competition.setType("SLALOM");
        competition.setTournament(tournament);
        slalomCompetitionRepository.save(competition);

        SlalomResult result = new SlalomResult();
        result.setAthleteProfile(athleteProfile);
        result.setCompetition(competition);
        result.setRun1Time(BigDecimal.valueOf(45.234));
        result.setRun2Time(BigDecimal.valueOf(48.132));
        result.setTotalTime(BigDecimal.valueOf(93.366));
        result.setFinished(true);
        result.setType("SLALOM");
        slalomResultRepository.save(result);

        Medal medal = new Medal();
        medal.setResult(result);
        medal.setCompetition(competition);
        medal.setType(MedalType.GOLD);
        medalRepository.save(medal);
    }

    @Test
    void getRankings_public_returns200() throws Exception {
        mockMvc.perform(get("/api/statistics/rankings/" + competition.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].finished").value(true));
    }

    @Test
    void getRankings_notFound_returns404() throws Exception {
        mockMvc.perform(get("/api/statistics/rankings/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getMedalsByCountry_public_returns200() throws Exception {
        mockMvc.perform(get("/api/statistics/medals-by-country"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].country").value("Bulgaria"))
                .andExpect(jsonPath("$[0].gold").value(1));
    }

    @Test
    void getAverageAge_public_returns200() throws Exception {
        mockMvc.perform(get("/api/statistics/average-age/" + competition.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.averageAge").exists());
    }

    @Test
    void getAverageAge_notFound_returns404() throws Exception {
        mockMvc.perform(get("/api/statistics/average-age/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getYoungestOldestMedalist_public_returns200() throws Exception {
        mockMvc.perform(get("/api/statistics/youngest-oldest-medalist"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.youngest").exists())
                .andExpect(jsonPath("$.oldest").exists());
    }

    @Test
    void getYoungestOldestMedalist_noMedalists_returnsEmptyMap() throws Exception {
        medalRepository.deleteAll();

        mockMvc.perform(get("/api/statistics/youngest-oldest-medalist"))
                .andExpect(status().isOk())
                .andExpect(content().string("{}"));
    }
}