package com.example.blog.services;

import com.example.blog.DTO.AuthResponse;
import com.example.blog.DTO.LoginRequest;
import com.example.blog.DTO.RegisterRequest;
import com.example.blog.enums.Role;
import com.example.blog.exceptions.DuplicateResourceException;
import com.example.blog.exceptions.ResourceNotFoundException;
import com.example.blog.models.User;
import com.example.blog.repositories.UserRepository;
import com.example.blog.security.JwtService;
import lombok.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Data
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already exists");
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();

        User savedUser = userRepository.save(user);

        String token = jwtService.generateToken(savedUser);

        UserDetails userDetails = org.springframework.security.core.userdetails.User.withUsername(savedUser.getEmail())
                .password(savedUser.getPassword())
                .roles(savedUser.getRole().name())
                .build();

        String accessToken = jwtService.generateToken(userDetails);



        return mapToAuthResponse(savedUser, token);
    }
    
    public AuthResponse registerAdmin(RegisterRequest registerRequest) {
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new DuplicateResourceException("Email already exists");
        }

        User admin = User.builder()
                .name(registerRequest.getName())
                .email(registerRequest.getEmail())
                .password(registerRequest.getPassword())
                .role(Role.ADMIN)
                .build();

        userRepository.save(admin);

        String token = jwtService.generateToken(admin);

        return mapToAuthResponse(admin, token);
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new ResourceNotFoundException("invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        String token = jwtService.generateToken(user);

        return mapToAuthResponse(user, token);
    }

    private AuthResponse mapToAuthResponse(User user, String token) {
        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }
}
