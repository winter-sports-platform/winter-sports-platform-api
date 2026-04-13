package com.wintersports.dtos.responses;

import com.wintersports.enums.Role;
import com.wintersports.enums.UserStatus;
import lombok.Data;

@Data
public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private UserStatus status;
    private Role role;
}