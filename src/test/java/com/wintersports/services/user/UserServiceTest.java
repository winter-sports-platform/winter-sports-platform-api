package com.wintersports.services.user;

import com.wintersports.dtos.requests.UpdateUserStatusRequest;
import com.wintersports.dtos.responses.UserResponse;
import com.wintersports.entities.User;
import com.wintersports.enums.Role;
import com.wintersports.enums.UserStatus;
import com.wintersports.exceptions.ResourceNotFoundException.ResourceNotFoundException;
import com.wintersports.repositories.user.IUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private IUserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private UserService userService;

    private User user;
    private UpdateUserStatusRequest updateStatusRequest;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("ivan123");
        user.setEmail("ivan@test.com");
        user.setRole(Role.ATHLETE);
        user.setStatus(UserStatus.PENDING);

        updateStatusRequest = new UpdateUserStatusRequest();
        updateStatusRequest.setStatus(UserStatus.APPROVED);
    }

    @Test
    void getAll_success() {
        when(userRepository.findAll()).thenReturn(List.of(user));
        when(modelMapper.map(any(), eq(UserResponse.class))).thenReturn(new UserResponse());

        List<UserResponse> result = userService.getAll();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void updateStatus_success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(user);
        when(modelMapper.map(any(), eq(UserResponse.class))).thenReturn(new UserResponse());

        UserResponse result = userService.updateStatus(1L, updateStatusRequest);

        assertNotNull(result);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void updateStatus_notFound_throwsResourceNotFoundException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.updateStatus(1L, updateStatusRequest));
    }

    @Test
    void updateStatus_statusChanged() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(user);
        when(modelMapper.map(any(), eq(UserResponse.class))).thenReturn(new UserResponse());

        userService.updateStatus(1L, updateStatusRequest);

        assertEquals(UserStatus.APPROVED, user.getStatus());
    }
}