package com.wintersports.integrations.athlete;

import com.wintersports.dtos.requests.LoginRequest;
import com.wintersports.dtos.requests.RegisterAthleteRequest;
import com.wintersports.dtos.requests.UpdateAthleteProfileRequest;
import com.wintersports.dtos.responses.AuthResponse;
import com.wintersports.entities.AthleteProfile;
import com.wintersports.entities.User;
import com.wintersports.enums.Gender;
import com.wintersports.enums.Role;
import com.wintersports.enums.UserStatus;
import com.wintersports.repositories.athleteprofile.IAthleteProfileRepository;
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
class AthleteIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private IAthleteProfileRepository athleteProfileRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private MockMvc mockMvc;
    private String athleteToken;
    private String adminToken;
    private AthleteProfile athleteProfile;

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
    }

    @Test
    void getAll_public_returns200() throws Exception {
        mockMvc.perform(get("/api/athletes"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name").value("Test Athlete"));
    }

    @Test
    void getById_public_returns200() throws Exception {
        mockMvc.perform(get("/api/athletes/" + athleteProfile.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Athlete"))
                .andExpect(jsonPath("$.country").value("Bulgaria"));
    }

    @Test
    void getById_notFound_returns404() throws Exception {
        mockMvc.perform(get("/api/athletes/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void update_withAthleteAuth_returns200() throws Exception {
        UpdateAthleteProfileRequest request = new UpdateAthleteProfileRequest();
        request.setName("Updated Name");
        request.setCountry("Germany");
        request.setGender(Gender.MALE);
        request.setDateOfBirth(LocalDate.of(1995, 5, 15));

        mockMvc.perform(put("/api/athletes/" + athleteProfile.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", "Bearer " + athleteToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"))
                .andExpect(jsonPath("$.country").value("Germany"));
    }

    @Test
    void update_withoutAuth_returns401() throws Exception {
        UpdateAthleteProfileRequest request = new UpdateAthleteProfileRequest();
        request.setName("Updated Name");
        request.setCountry("Germany");
        request.setGender(Gender.MALE);
        request.setDateOfBirth(LocalDate.of(1995, 5, 15));

        mockMvc.perform(put("/api/athletes/" + athleteProfile.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void delete_withAdminAuth_returns204() throws Exception {
        mockMvc.perform(delete("/api/athletes/" + athleteProfile.getId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());
    }

    @Test
    void delete_withoutAuth_returns401() throws Exception {
        mockMvc.perform(delete("/api/athletes/" + athleteProfile.getId()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void delete_notFound_returns404() throws Exception {
        mockMvc.perform(delete("/api/athletes/999")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound());
    }
}