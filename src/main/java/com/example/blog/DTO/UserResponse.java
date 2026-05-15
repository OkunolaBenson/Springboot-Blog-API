package com.example.blog.DTO;

import com.example.blog.enums.Role;
import lombok.*;

@Data
@Builder
public class UserResponse {
    private Long id;
    private String name;
    private String email;
    private Role role;
}
