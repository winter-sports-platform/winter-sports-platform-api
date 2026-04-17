package com.wintersports.services.registration;

import com.wintersports.dtos.requests.CreateRegistrationRequest;
import com.wintersports.dtos.requests.UpdateRegistrationStatusRequest;
import com.wintersports.dtos.responses.AthleteProfileResponse;
import com.wintersports.dtos.responses.RegistrationItemResponse;
import com.wintersports.dtos.responses.RegistrationResponse;
import com.wintersports.entities.AthleteProfile;
import com.wintersports.entities.Registration;
import com.wintersports.entities.User;
import com.wintersports.entities.competition.Competition;
import com.wintersports.enums.Gender;
import com.wintersports.enums.RegistrationStatus;
import com.wintersports.enums.UserStatus;
import com.wintersports.exceptions.DuplicateResourceException.DuplicateResourceException;
import com.wintersports.exceptions.InvalidRegistrationException.InvalidRegistrationException;
import com.wintersports.exceptions.RegistrationClosedException.RegistrationClosedException;
import com.wintersports.exceptions.ResourceNotFoundException.ResourceNotFoundException;
import com.wintersports.repositories.athleteprofile.IAthleteProfileRepository;
import com.wintersports.repositories.competition.ICompetitionRepository;
import com.wintersports.repositories.registration.IRegistrationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegistrationServiceTest {

    @Mock
    private IRegistrationRepository registrationRepository;

    @Mock
    private IAthleteProfileRepository athleteProfileRepository;

    @Mock
    private ICompetitionRepository competitionRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private RegistrationService registrationService;

    private AthleteProfile athleteProfile;
    private Competition competition;
    private CreateRegistrationRequest createRequest;
    private Registration registration;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setUsername("ivan123");
        user.setStatus(UserStatus.APPROVED);

        athleteProfile = new AthleteProfile();
        athleteProfile.setId(1L);
        athleteProfile.setUser(user);
        athleteProfile.setGender(Gender.MALE);
        athleteProfile.setDateOfBirth(LocalDate.of(1995, 5, 15));

        createRequest = new CreateRegistrationRequest();
        createRequest.setCompetitionId(1L);

        registration = new Registration();
        registration.setId(1L);
        registration.setAthleteProfile(athleteProfile);
        registration.setStatus(RegistrationStatus.PENDING);
    }

    @Test
    void getAll_success() {
        when(registrationRepository.findAll()).thenReturn(List.of(registration));
        when(modelMapper.map(any(AthleteProfile.class), eq(AthleteProfileResponse.class)))
                .thenReturn(new AthleteProfileResponse());
        when(modelMapper.map(any(Registration.class), eq(RegistrationItemResponse.class)))
                .thenReturn(new RegistrationItemResponse());

        List<RegistrationResponse> result = registrationService.getAll();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void create_success() {
        competition = mock(Competition.class);
        when(competition.getId()).thenReturn(1L);
        setupCompetition();

        when(athleteProfileRepository.findByUserUsername("ivan123")).thenReturn(Optional.of(athleteProfile));
        when(competitionRepository.findById(1L)).thenReturn(Optional.of(competition));
        when(registrationRepository.existsByAthleteProfileIdAndCompetitionId(1L, 1L)).thenReturn(false);
        when(registrationRepository.save(any())).thenReturn(registration);
        when(modelMapper.map(any(), eq(RegistrationResponse.class))).thenReturn(new RegistrationResponse());

        RegistrationResponse result = registrationService.create(createRequest);

        assertNotNull(result);
    }

    @Test
    void create_athleteNotFound_throwsResourceNotFoundException() {
        when(athleteProfileRepository.findByUserUsername("ivan123")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> registrationService.create(createRequest));
    }

    @Test
    void create_competitionNotFound_throwsResourceNotFoundException() {
        when(athleteProfileRepository.findByUserUsername("ivan123")).thenReturn(Optional.of(athleteProfile));
        when(competitionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> registrationService.create(createRequest));
    }

    @Test
    void create_registrationClosed_throwsRegistrationClosedException() {
        competition = mock(Competition.class);
        when(competition.getDate()).thenReturn(LocalDate.now().plusDays(5));
        when(competition.getRegistrationDeadlineDays()).thenReturn(14);

        setupSecurityContext();

        when(athleteProfileRepository.findByUserUsername("ivan123")).thenReturn(Optional.of(athleteProfile));
        when(competitionRepository.findById(1L)).thenReturn(Optional.of(competition));

        assertThrows(RegistrationClosedException.class, () -> registrationService.create(createRequest));
    }

    @Test
    void create_genderMismatch_throwsInvalidRegistrationException() {
        competition = mock(Competition.class);
        when(competition.getDate()).thenReturn(LocalDate.now().plusDays(60));
        when(competition.getRegistrationDeadlineDays()).thenReturn(14);
        when(competition.getGender()).thenReturn(Gender.FEMALE);

        setupSecurityContext();

        when(competition.getGender()).thenReturn(Gender.FEMALE);
        when(athleteProfileRepository.findByUserUsername("ivan123")).thenReturn(Optional.of(athleteProfile));
        when(competitionRepository.findById(1L)).thenReturn(Optional.of(competition));

        assertThrows(InvalidRegistrationException.class, () -> registrationService.create(createRequest));
    }

    @Test
    void create_tooYoung_throwsInvalidRegistrationException() {
        competition = mock(Competition.class);
        when(competition.getDate()).thenReturn(LocalDate.now().plusDays(60));
        when(competition.getRegistrationDeadlineDays()).thenReturn(14);
        when(competition.getGender()).thenReturn(Gender.MALE);
        when(competition.getMinAge()).thenReturn(18);

        setupSecurityContext();

        athleteProfile.setDateOfBirth(LocalDate.now().minusYears(15));
        when(athleteProfileRepository.findByUserUsername("ivan123")).thenReturn(Optional.of(athleteProfile));
        when(competitionRepository.findById(1L)).thenReturn(Optional.of(competition));

        assertThrows(InvalidRegistrationException.class, () -> registrationService.create(createRequest));
    }

    @Test
    void create_duplicate_throwsDuplicateResourceException() {
        competition = mock(Competition.class);
        when(competition.getId()).thenReturn(1L);
        setupCompetition();

        setupSecurityContext();

        when(athleteProfileRepository.findByUserUsername("ivan123")).thenReturn(Optional.of(athleteProfile));
        when(competitionRepository.findById(1L)).thenReturn(Optional.of(competition));
        when(registrationRepository.existsByAthleteProfileIdAndCompetitionId(1L, 1L)).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> registrationService.create(createRequest));
    }

    @Test
    void updateStatus_success() {
        UpdateRegistrationStatusRequest request = new UpdateRegistrationStatusRequest();
        request.setStatus(RegistrationStatus.APPROVED);

        when(registrationRepository.findById(1L)).thenReturn(Optional.of(registration));
        when(registrationRepository.save(any())).thenReturn(registration);
        when(modelMapper.map(any(), eq(RegistrationResponse.class))).thenReturn(new RegistrationResponse());

        RegistrationResponse result = registrationService.updateStatus(1L, request);

        assertNotNull(result);
        assertEquals(RegistrationStatus.APPROVED, registration.getStatus());
    }

    @Test
    void updateStatus_notFound_throwsResourceNotFoundException() {
        UpdateRegistrationStatusRequest request = new UpdateRegistrationStatusRequest();
        request.setStatus(RegistrationStatus.APPROVED);

        when(registrationRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> registrationService.updateStatus(1L, request));
    }

    @Test
    void delete_success() {
        when(registrationRepository.findById(1L)).thenReturn(Optional.of(registration));

        assertDoesNotThrow(() -> registrationService.delete(1L));
        verify(registrationRepository, times(1)).deleteById(1L);
    }

    @Test
    void delete_notFound_throwsResourceNotFoundException() {
        when(registrationRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> registrationService.delete(1L));
    }

    private void setupCompetition() {
        when(competition.getDate()).thenReturn(LocalDate.now().plusDays(60));
        when(competition.getRegistrationDeadlineDays()).thenReturn(14);
        when(competition.getGender()).thenReturn(Gender.MALE);
        when(competition.getMinAge()).thenReturn(18);
    }

    private void setupSecurityContext() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("ivan123");
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }
}