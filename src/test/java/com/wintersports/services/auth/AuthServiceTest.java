package com.wintersports.services.auth;

import com.wintersports.dtos.requests.LoginRequest;
import com.wintersports.dtos.requests.RegisterAthleteRequest;
import com.wintersports.dtos.responses.AuthResponse;
import com.wintersports.entities.AthleteProfile;
import com.wintersports.entities.User;
import com.wintersports.enums.Gender;
import com.wintersports.enums.Role;
import com.wintersports.enums.UserStatus;
import com.wintersports.exceptions.AccountNotApprovedException.AccountNotApprovedException;
import com.wintersports.exceptions.DuplicateResourceException.DuplicateResourceException;
import com.wintersports.exceptions.InvalidCredentialsException.InvalidCredentialsException;
import com.wintersports.repositories.athleteprofile.IAthleteProfileRepository;
import com.wintersports.repositories.user.IUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private IUserRepository userRepository;

    @Mock
    private IAthleteProfileRepository athleteProfileRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private AuthService authService;

    private RegisterAthleteRequest registerRequest;
    private LoginRequest loginRequest;
    private User user;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterAthleteRequest();
        registerRequest.setUsername("ivan123");
        registerRequest.setEmail("ivan@test.com");
        registerRequest.setPassword("Test123");
        registerRequest.setName("Ivan Ivanov");
        registerRequest.setCountry("Bulgaria");
        registerRequest.setGender(Gender.MALE);
        registerRequest.setDateOfBirth(LocalDate.of(1995, 5, 15));

        loginRequest = new LoginRequest();
        loginRequest.setUsername("ivan123");
        loginRequest.setPassword("Test123");

        user = new User();
        user.setUsername("ivan123");
        user.setEmail("ivan@test.com");
        user.setPassword("encoded_password");
        user.setRole(Role.ATHLETE);
        user.setStatus(UserStatus.APPROVED);
    }

    @Test
    void registerAthlete_success() {
        when(userRepository.existsByUsername(registerRequest.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("encoded_password");
        when(userRepository.save(any())).thenReturn(user);
        when(athleteProfileRepository.save(any())).thenReturn(new AthleteProfile());
        when(jwtService.generateToken(any(), any(), any())).thenReturn("token");
        when(modelMapper.map(any(), eq(AthleteProfile.class))).thenReturn(new AthleteProfile());

        AuthResponse response = authService.registerAthlete(registerRequest);

        assertNotNull(response);
        assertEquals("token", response.getToken());
    }

    @Test
    void registerAthlete_duplicateUsername_throwsDuplicateResourceException() {
        when(userRepository.existsByUsername(registerRequest.getUsername())).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> authService.registerAthlete(registerRequest));
    }

    @Test
    void registerAthlete_duplicateEmail_throwsDuplicateResourceException() {
        when(userRepository.existsByUsername(registerRequest.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> authService.registerAthlete(registerRequest));
    }

    @Test
    void login_success() {
        when(userRepository.findByUsername(loginRequest.getUsername())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())).thenReturn(true);
        when(jwtService.generateToken(any(), any(), any())).thenReturn("token");

        AuthResponse response = authService.login(loginRequest);

        assertNotNull(response);
        assertEquals("token", response.getToken());
    }

    @Test
    void login_invalidUsername_throwsInvalidCredentialsException() {
        when(userRepository.findByUsername(loginRequest.getUsername())).thenReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class, () -> authService.login(loginRequest));
    }

    @Test
    void login_invalidPassword_throwsInvalidCredentialsException() {
        when(userRepository.findByUsername(loginRequest.getUsername())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())).thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () -> authService.login(loginRequest));
    }

    @Test
    void login_pendingAccount_throwsAccountNotApprovedException() {
        user.setStatus(UserStatus.PENDING);
        when(userRepository.findByUsername(loginRequest.getUsername())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())).thenReturn(true);

        assertThrows(AccountNotApprovedException.class, () -> authService.login(loginRequest));
    }

    @Test
    void login_rejectedAccount_throwsAccountNotApprovedException() {
        user.setStatus(UserStatus.REJECTED);
        when(userRepository.findByUsername(loginRequest.getUsername())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())).thenReturn(true);

        assertThrows(AccountNotApprovedException.class, () -> authService.login(loginRequest));
    }
}