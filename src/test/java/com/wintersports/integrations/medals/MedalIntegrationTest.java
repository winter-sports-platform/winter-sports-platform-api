package com.wintersports.integrations.medal;

import com.wintersports.dtos.requests.CreateMedalRequest;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class MedalIntegrationTest {

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
    private String adminToken;
    private SlalomResult result;
    private SlalomCompetition competition;
    private Medal medal;

    @BeforeEach
    void setUp() throws Exception {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();

        User admin = new User();
        admin.setUsername("testadmin");
        admin.setEmail("admin@test.com");
        admin.setPassword(passwordEncoder.encode("Test123"));
        admin.setRole(Role.ADMIN);
        admin.setStatus(UserStatus.APPROVED);
        userRepository.save(admin);

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("testadmin");
        loginRequest.setPassword("Test123");

        String response = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andReturn().getResponse().getContentAsString();

        adminToken = objectMapper.readValue(response, AuthResponse.class).getToken();

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

        result = new SlalomResult();
        result.setAthleteProfile(athleteProfile);
        result.setCompetition(competition);
        result.setRun1Time(BigDecimal.valueOf(45.234));
        result.setRun2Time(BigDecimal.valueOf(48.132));
        result.setTotalTime(BigDecimal.valueOf(93.366));
        result.setFinished(true);
        result.setType("SLALOM");
        slalomResultRepository.save(result);

        medal = new Medal();
        medal.setResult(result);
        medal.setCompetition(competition);
        medal.setType(MedalType.GOLD);
        medalRepository.save(medal);
    }

    @Test
    void getAll_public_returns200() throws Exception {
        mockMvc.perform(get("/api/medals"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].type").value("GOLD"));
    }

    @Test
    void getByCountry_public_returns200() throws Exception {
        mockMvc.perform(get("/api/medals/by-country"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].country").value("Bulgaria"))
                .andExpect(jsonPath("$[0].gold").value(1));
    }

    @Test
    void create_withAdminAuth_returns201() throws Exception {
        User athleteUser2 = new User();
        athleteUser2.setUsername("testathlete2");
        athleteUser2.setEmail("athlete2@test.com");
        athleteUser2.setPassword(passwordEncoder.encode("Test123"));
        athleteUser2.setRole(Role.ATHLETE);
        athleteUser2.setStatus(UserStatus.APPROVED);
        userRepository.save(athleteUser2);

        AthleteProfile athleteProfile2 = new AthleteProfile();
        athleteProfile2.setUser(athleteUser2);
        athleteProfile2.setName("Test Athlete 2");
        athleteProfile2.setCountry("Germany");
        athleteProfile2.setGender(Gender.MALE);
        athleteProfile2.setDateOfBirth(LocalDate.of(1993, 3, 10));
        athleteProfileRepository.save(athleteProfile2);

        SlalomResult result2 = new SlalomResult();
        result2.setAthleteProfile(athleteProfile2);
        result2.setCompetition(competition);
        result2.setRun1Time(BigDecimal.valueOf(50.000));
        result2.setRun2Time(BigDecimal.valueOf(52.000));
        result2.setTotalTime(BigDecimal.valueOf(102.000));
        result2.setFinished(true);
        result2.setType("SLALOM");
        slalomResultRepository.save(result2);

        CreateMedalRequest request = new CreateMedalRequest();
        request.setResultId(result2.getId());
        request.setCompetitionId(competition.getId());
        request.setType(MedalType.SILVER);

        mockMvc.perform(post("/api/medals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.type").value("SILVER"));
    }

    @Test
    void create_withoutAuth_returns401() throws Exception {
        CreateMedalRequest request = new CreateMedalRequest();
        request.setResultId(result.getId());
        request.setCompetitionId(competition.getId());
        request.setType(MedalType.SILVER);

        mockMvc.perform(post("/api/medals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void create_duplicateMedalType_returns409() throws Exception {
        CreateMedalRequest request = new CreateMedalRequest();
        request.setResultId(result.getId());
        request.setCompetitionId(competition.getId());
        request.setType(MedalType.GOLD);

        mockMvc.perform(post("/api/medals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isConflict());
    }

    @Test
    void delete_withAdminAuth_returns204() throws Exception {
        mockMvc.perform(delete("/api/medals/" + medal.getId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());
    }

    @Test
    void delete_withoutAuth_returns401() throws Exception {
        mockMvc.perform(delete("/api/medals/" + medal.getId()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void delete_notFound_returns404() throws Exception {
        mockMvc.perform(delete("/api/medals/999")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound());
    }
}