package com.wintersports.integrations.tournament;

import com.wintersports.dtos.requests.CreateTournamentRequest;
import com.wintersports.dtos.requests.LoginRequest;
import com.wintersports.dtos.responses.AuthResponse;
import com.wintersports.entities.TournamentType;
import com.wintersports.entities.User;
import com.wintersports.enums.Role;
import com.wintersports.enums.UserStatus;
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
class TournamentIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ITournamentTypeRepository tournamentTypeRepository;

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private MockMvc mockMvc;
    private String adminToken;
    private Long typeId;

    @BeforeEach
    void setUp() throws Exception {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();

        TournamentType type = new TournamentType();
        type.setName("Olympics");
        typeId = tournamentTypeRepository.save(type).getId();

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
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        adminToken = objectMapper.readValue(response, AuthResponse.class).getToken();
    }

    private CreateTournamentRequest buildTournamentRequest() {
        CreateTournamentRequest request = new CreateTournamentRequest();
        request.setName("Winter Olympics 2027");
        request.setYear(2027);
        request.setLocation("Milano");
        request.setStartDate(LocalDate.of(2027, 2, 1));
        request.setEndDate(LocalDate.of(2027, 2, 20));
        request.setTypeId(typeId);
        return request;
    }

    @Test
    void getAll_public_returns200() throws Exception {
        mockMvc.perform(get("/api/tournaments"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void getById_notFound_returns404() throws Exception {
        mockMvc.perform(get("/api/tournaments/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_withoutAuth_returns401() throws Exception {
        CreateTournamentRequest request = buildTournamentRequest();

        mockMvc.perform(post("/api/tournaments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void create_withAdminAuth_returns201() throws Exception {
        CreateTournamentRequest request = buildTournamentRequest();

        mockMvc.perform(post("/api/tournaments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Winter Olympics 2027"))
                .andExpect(jsonPath("$.year").value(2027))
                .andExpect(jsonPath("$.location").value("Milano"));
    }

    @Test
    void create_duplicateName_returns409() throws Exception {
        CreateTournamentRequest request = buildTournamentRequest();

        mockMvc.perform(post("/api/tournaments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .header("Authorization", "Bearer " + adminToken));

        mockMvc.perform(post("/api/tournaments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isConflict());
    }

    @Test
    void delete_withAdminAuth_returns204() throws Exception {
        CreateTournamentRequest request = buildTournamentRequest();

        String response = mockMvc.perform(post("/api/tournaments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", "Bearer " + adminToken))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long id = objectMapper.readValue(response, com.wintersports.dtos.responses.TournamentResponse.class).getId();

        mockMvc.perform(delete("/api/tournaments/" + id)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());
    }

    @Test
    void delete_withoutAuth_returns401() throws Exception {
        mockMvc.perform(delete("/api/tournaments/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void delete_notFound_returns404() throws Exception {
        mockMvc.perform(delete("/api/tournaments/999")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound());
    }
}