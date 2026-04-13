package com.wintersports.services.user;

import com.wintersports.configs.ModelMapperConfig;
import com.wintersports.dtos.requests.UpdateUserStatusRequest;
import com.wintersports.dtos.responses.UserResponse;
import com.wintersports.entities.User;
import com.wintersports.exceptions.ResourceNotFoundException.ResourceNotFoundException;
import com.wintersports.repositories.user.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

    private final IUserRepository userRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<UserResponse> getAll() {
        return ModelMapperConfig.mapList(
                userRepository.findAll(),
                UserResponse.class,
                modelMapper
        );
    }

    @Override
    public UserResponse updateStatus(Long id, UpdateUserStatusRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));

        user.setStatus(request.getStatus());

        return modelMapper.map(userRepository.save(user), UserResponse.class);
    }
}