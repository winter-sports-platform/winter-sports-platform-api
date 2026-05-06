package com.wintersports.integrations.auth;

import com.wintersports.dtos.requests.LoginRequest;
import com.wintersports.dtos.requests.RegisterAthleteRequest;
import com.wintersports.enums.Gender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class AuthIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    private RegisterAthleteRequest buildRegisterRequest(String username, String email) {
        RegisterAthleteRequest request = new RegisterAthleteRequest();
        request.setUsername(username);
        request.setEmail(email);
        request.setPassword("Test123");
        request.setName("Test Athlete");
        request.setCountry("Bulgaria");
        request.setGender(Gender.MALE);
        request.setDateOfBirth(LocalDate.of(1995, 5, 15));
        return request;
    }

    @Test
    void registerAthlete_success_returns201() throws Exception {
        RegisterAthleteRequest request = buildRegisterRequest("testathlete", "test@test.com");

        mockMvc.perform(post("/api/auth/register/athlete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    void registerAthlete_duplicateUsername_returns409() throws Exception {
        RegisterAthleteRequest request = buildRegisterRequest("testathlete", "test@test.com");

        mockMvc.perform(post("/api/auth/register/athlete")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        mockMvc.perform(post("/api/auth/register/athlete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void login_invalidCredentials_returns401() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("nonexistent");
        loginRequest.setPassword("wrong");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void login_pendingAccount_returns403() throws Exception {
        RegisterAthleteRequest registerRequest = buildRegisterRequest("pendingathlete", "pending@test.com");

        mockMvc.perform(post("/api/auth/register/athlete")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)));

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("pendingathlete");
        loginRequest.setPassword("Test123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").exists());
    }
}