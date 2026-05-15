package com.example.blog.DTO;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
public class LoginRequest {
    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String password;
}