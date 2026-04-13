package com.wintersports.services.auth;

import com.wintersports.dtos.requests.LoginRequest;
import com.wintersports.dtos.requests.RegisterAdminRequest;
import com.wintersports.dtos.requests.RegisterAthleteRequest;
import com.wintersports.dtos.responses.AuthResponse;

public interface IAuthService {
    AuthResponse registerAthlete(RegisterAthleteRequest request);
    AuthResponse registerAdmin(RegisterAdminRequest request);
    AuthResponse login(LoginRequest request);
}