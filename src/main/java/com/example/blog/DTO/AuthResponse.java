package com.example.blog.DTO;

import com.example.blog.enums.Role;
import lombok.*;

@Data
@Builder
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private String email;
    private Role role;
}
