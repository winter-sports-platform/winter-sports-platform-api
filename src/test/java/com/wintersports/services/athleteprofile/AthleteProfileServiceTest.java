package com.wintersports.services.athleteprofile;

import com.wintersports.dtos.requests.UpdateAthleteProfileRequest;
import com.wintersports.dtos.responses.AthleteProfileResponse;
import com.wintersports.entities.AthleteProfile;
import com.wintersports.entities.User;
import com.wintersports.enums.Gender;
import com.wintersports.exceptions.ResourceNotFoundException.ResourceNotFoundException;
import com.wintersports.exceptions.UnauthorizedAccessException.UnauthorizedAccessException;
import com.wintersports.repositories.athleteprofile.IAthleteProfileRepository;
import com.wintersports.repositories.user.IUserRepository;
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
class AthleteProfileServiceTest {

    @Mock
    private IAthleteProfileRepository athleteProfileRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private IUserRepository userRepository;

    @InjectMocks
    private AthleteProfileService athleteProfileService;

    private AthleteProfile athleteProfile;
    private UpdateAthleteProfileRequest updateRequest;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setUsername("ivan123");

        athleteProfile = new AthleteProfile();
        athleteProfile.setId(1L);
        athleteProfile.setUser(user);
        athleteProfile.setName("Ivan Ivanov");
        athleteProfile.setCountry("Bulgaria");
        athleteProfile.setGender(Gender.MALE);
        athleteProfile.setDateOfBirth(LocalDate.of(1995, 5, 15));

        updateRequest = new UpdateAthleteProfileRequest();
        updateRequest.setName("Ivan Updated");
        updateRequest.setCountry("Bulgaria");
        updateRequest.setGender(Gender.MALE);
        updateRequest.setDateOfBirth(LocalDate.of(1995, 5, 15));
    }

    @Test
    void getAll_success() {
        when(athleteProfileRepository.findAll()).thenReturn(List.of(athleteProfile));
        when(modelMapper.map(any(), eq(AthleteProfileResponse.class))).thenReturn(new AthleteProfileResponse());

        List<AthleteProfileResponse> result = athleteProfileService.getAll();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getById_success() {
        when(athleteProfileRepository.findById(1L)).thenReturn(Optional.of(athleteProfile));
        when(modelMapper.map(any(), eq(AthleteProfileResponse.class))).thenReturn(new AthleteProfileResponse());

        AthleteProfileResponse result = athleteProfileService.getById(1L);

        assertNotNull(result);
    }

    @Test
    void getById_notFound_throwsResourceNotFoundException() {
        when(athleteProfileRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> athleteProfileService.getById(1L));
    }

    @Test
    void update_success() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("ivan123");
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(athleteProfileRepository.findById(1L)).thenReturn(Optional.of(athleteProfile));
        when(athleteProfileRepository.findByUserUsername("ivan123")).thenReturn(Optional.of(athleteProfile));
        doNothing().when(modelMapper).map(any(), any(AthleteProfile.class));
        when(athleteProfileRepository.save(any())).thenReturn(athleteProfile);
        when(modelMapper.map(any(), eq(AthleteProfileResponse.class))).thenReturn(new AthleteProfileResponse());

        AthleteProfileResponse result = athleteProfileService.update(1L, updateRequest);

        assertNotNull(result);
    }

    @Test
    void update_notFound_throwsResourceNotFoundException() {
        when(athleteProfileRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> athleteProfileService.update(1L, updateRequest));
    }

    @Test
    void update_unauthorizedAccess_throwsUnauthorizedAccessException() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("ivan123");
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        AthleteProfile otherAthlete = new AthleteProfile();
        User otherUser = new User();
        otherUser.setUsername("other123");
        otherAthlete.setUser(otherUser);

        when(athleteProfileRepository.findById(1L)).thenReturn(Optional.of(otherAthlete));
        when(athleteProfileRepository.findByUserUsername("ivan123")).thenReturn(Optional.of(athleteProfile));

        assertThrows(UnauthorizedAccessException.class, () -> athleteProfileService.update(1L, updateRequest));
    }

    @Test
    void delete_success() {
        when(athleteProfileRepository.findById(1L)).thenReturn(Optional.of(athleteProfile));
        doNothing().when(userRepository).deleteById(any());

        assertDoesNotThrow(() -> athleteProfileService.delete(1L));
        verify(athleteProfileRepository, times(1)).deleteById(1L);
    }

    @Test
    void delete_notFound_throwsResourceNotFoundException() {
        when(athleteProfileRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> athleteProfileService.delete(1L));
    }
}