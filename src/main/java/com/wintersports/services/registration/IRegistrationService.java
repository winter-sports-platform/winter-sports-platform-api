package com.wintersports.services.registration;

import com.wintersports.dtos.requests.CreateRegistrationRequest;
import com.wintersports.dtos.requests.UpdateRegistrationStatusRequest;
import com.wintersports.dtos.responses.RegistrationResponse;

import java.util.List;

public interface IRegistrationService {
    List<RegistrationResponse> getAll();
    RegistrationResponse create(CreateRegistrationRequest request);
    RegistrationResponse updateStatus(Long id, UpdateRegistrationStatusRequest request);
    void delete(Long id);
}