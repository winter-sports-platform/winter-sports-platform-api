package com.wintersports.integrations.registration;

import com.wintersports.dtos.requests.CreateRegistrationRequest;
import com.wintersports.dtos.requests.LoginRequest;
import com.wintersports.dtos.requests.UpdateRegistrationStatusRequest;
import com.wintersports.dtos.responses.AuthResponse;
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
import com.wintersports.repositories.athleteprofile.IAthleteProfileRepository;
import com.wintersports.repositories.competition.ISlalomCompetitionRepository;
import com.wintersports.repositories.registration.IRegistrationRepository;
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

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class RegistrationIntegrationTest {

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
    private IRegistrationRepository registrationRepository;

    private MockMvc mockMvc;
    private String athleteToken;
    private String adminToken;
    private SlalomCompetition competition;
    private AthleteProfile athleteProfile;
    private Registration registration;

    @BeforeEach
    void setUp() throws Exception {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();

        User adminUser = new User();
        adminUser.setUsername("testadmin");
        adminUser.setEmail("admin@test.com");
        adminUser.setPassword(passwordEncoder.encode("Test123"));
        adminUser.setRole(Role.ADMIN);
        adminUser.setStatus(UserStatus.APPROVED);
        userRepository.save(adminUser);

        LoginRequest adminLogin = new LoginRequest();
        adminLogin.setUsername("testadmin");
        adminLogin.setPassword("Test123");

        String adminResponse = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(adminLogin)))
                .andReturn().getResponse().getContentAsString();
        adminToken = objectMapper.readValue(adminResponse, AuthResponse.class).getToken();

        User athleteUser = new User();
        athleteUser.setUsername("testathlete");
        athleteUser.setEmail("athlete@test.com");
        athleteUser.setPassword(passwordEncoder.encode("Test123"));
        athleteUser.setRole(Role.ATHLETE);
        athleteUser.setStatus(UserStatus.APPROVED);
        userRepository.save(athleteUser);

        athleteProfile = new AthleteProfile();
        athleteProfile.setUser(athleteUser);
        athleteProfile.setName("Test Athlete");
        athleteProfile.setCountry("Bulgaria");
        athleteProfile.setGender(Gender.MALE);
        athleteProfile.setDateOfBirth(LocalDate.of(1995, 5, 15));
        athleteProfileRepository.save(athleteProfile);

        LoginRequest athleteLogin = new LoginRequest();
        athleteLogin.setUsername("testathlete");
        athleteLogin.setPassword("Test123");

        String athleteResponse = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(athleteLogin)))
                .andReturn().getResponse().getContentAsString();
        athleteToken = objectMapper.readValue(athleteResponse, AuthResponse.class).getToken();

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

        registration = new Registration();
        registration.setAthleteProfile(athleteProfile);
        registration.setCompetition(competition);
        registration.setStatus(RegistrationStatus.PENDING);
        registrationRepository.save(registration);
    }

    @Test
    void getAll_withAdminAuth_returns200() throws Exception {
        mockMvc.perform(get("/api/registrations")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void getAll_withoutAuth_returns401() throws Exception {
        mockMvc.perform(get("/api/registrations"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getByCompetition_public_returns200() throws Exception {
        mockMvc.perform(get("/api/registrations/competition/" + competition.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].status").value("PENDING"));
    }

    @Test
    void getMyRegistrations_withAthleteAuth_returns200() throws Exception {
        mockMvc.perform(get("/api/registrations/my")
                        .header("Authorization", "Bearer " + athleteToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.athleteProfile.name").value("Test Athlete"));
    }

    @Test
    void getMyRegistrations_withoutAuth_returns401() throws Exception {
        mockMvc.perform(get("/api/registrations/my"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void updateStatus_withAdminAuth_returns200() throws Exception {
        UpdateRegistrationStatusRequest request = new UpdateRegistrationStatusRequest();
        request.setStatus(RegistrationStatus.APPROVED);

        mockMvc.perform(put("/api/registrations/" + registration.getId() + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    void updateStatus_withoutAuth_returns401() throws Exception {
        UpdateRegistrationStatusRequest request = new UpdateRegistrationStatusRequest();
        request.setStatus(RegistrationStatus.APPROVED);

        mockMvc.perform(put("/api/registrations/" + registration.getId() + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void delete_withAdminAuth_returns204() throws Exception {
        mockMvc.perform(delete("/api/registrations/" + registration.getId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());
    }

    @Test
    void delete_withoutAuth_returns401() throws Exception {
        mockMvc.perform(delete("/api/registrations/" + registration.getId()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void delete_notFound_returns404() throws Exception {
        mockMvc.perform(delete("/api/registrations/999")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound());
    }
}