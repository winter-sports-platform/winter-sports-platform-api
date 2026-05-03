package com.wintersports.services.auth;

import com.wintersports.dtos.requests.LoginRequest;
import com.wintersports.dtos.requests.RegisterAdminRequest;
import com.wintersports.dtos.requests.RegisterAthleteRequest;
import com.wintersports.dtos.responses.AuthResponse;
import com.wintersports.entities.AthleteProfile;
import com.wintersports.entities.User;
import com.wintersports.enums.Role;
import com.wintersports.enums.UserStatus;
import com.wintersports.exceptions.AccountNotApprovedException.AccountNotApprovedException;
import com.wintersports.exceptions.DuplicateResourceException.DuplicateResourceException;
import com.wintersports.exceptions.InvalidCredentialsException.InvalidCredentialsException;
import com.wintersports.repositories.athleteprofile.IAthleteProfileRepository;
import com.wintersports.repositories.user.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService implements IAuthService {

    private final IUserRepository userRepository;
    private final IAthleteProfileRepository athleteProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final ModelMapper modelMapper;

    @Override
    public AuthResponse registerAthlete(RegisterAthleteRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("Username '" + request.getUsername() + "' is already taken");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email '" + request.getEmail() + "' is already taken");
        }

        User user = toUserEntity(request.getUsername(), request.getEmail(), request.getPassword(), Role.ATHLETE);
        userRepository.save(user);

        AthleteProfile athleteProfile = toAthleteProfileEntity(request, user);
        athleteProfileRepository.save(athleteProfile);

        return new AuthResponse(jwtService.generateToken(
                user.getUsername(), user.getRole().name(), athleteProfile.getId()));
    }

    @Override
    public AuthResponse registerAdmin(RegisterAdminRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("Username '" + request.getUsername() + "' is already taken");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email '" + request.getEmail() + "' is already taken");
        }

        User user = toUserEntity(request.getUsername(), request.getEmail(), request.getPassword(), Role.ADMIN);
        userRepository.save(user);

        return new AuthResponse(jwtService.generateToken(
                user.getUsername(), user.getRole().name(), null));
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid username or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid username or password");
        }

        if (user.getStatus() == UserStatus.PENDING) {
            throw new AccountNotApprovedException("Your account is pending admin approval");
        }

        if (user.getStatus() == UserStatus.REJECTED) {
            throw new AccountNotApprovedException("Your account has been rejected");
        }

        Long athleteProfileId = null;
        if (user.getRole() == Role.ATHLETE) {
            athleteProfileId = athleteProfileRepository
                    .findByUserUsername(user.getUsername())
                    .map(AthleteProfile::getId)
                    .orElse(null);
        }

        return new AuthResponse(jwtService.generateToken(
                user.getUsername(), user.getRole().name(), athleteProfileId));
    }

    private User toUserEntity(String username, String email, String password, Role role) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);
        user.setStatus(UserStatus.PENDING);
        return user;
    }

    private AthleteProfile toAthleteProfileEntity(RegisterAthleteRequest request, User user) {
        AthleteProfile athleteProfile = modelMapper.map(request, AthleteProfile.class);
        athleteProfile.setUser(user);
        return athleteProfile;
    }
}