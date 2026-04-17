package com.wintersports.dtos.responses;

import lombok.Data;
import java.util.List;

@Data
public class RegistrationResponse {
    private AthleteProfileResponse athleteProfile;
    private List<RegistrationItemResponse> registrations;
}