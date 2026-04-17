package com.wintersports.services.user;

import com.wintersports.dtos.requests.UpdateUserStatusRequest;
import com.wintersports.dtos.responses.UserResponse;

import java.util.List;

public interface IUserService {
    List<UserResponse> getAll();
    UserResponse updateStatus(Long id, UpdateUserStatusRequest request);
}