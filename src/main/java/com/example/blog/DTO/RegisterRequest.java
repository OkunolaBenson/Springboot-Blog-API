package com.example.blog.DTO;

import com.example.blog.enums.Role;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
public class RegisterRequest {
    @NotBlank
    private String name;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    @Size(min = 6)
    private String password;
}
