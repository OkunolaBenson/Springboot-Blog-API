package com.example.blog.services;

import com.example.blog.DTO.AuthResponse;
import com.example.blog.DTO.LoginRequest;
import com.example.blog.DTO.RegisterRequest;
import com.example.blog.enums.Role;
import com.example.blog.exceptions.DuplicateResourceException;
import com.example.blog.exceptions.ResourceNotFoundException;
import com.example.blog.models.RefreshToken;
import com.example.blog.models.User;
import com.example.blog.repositories.RefreshTokenRepository;
import com.example.blog.repositories.UserRepository;
import com.example.blog.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;

    private final RefreshTokenRepository refreshTokenRepository;

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

        return generateAuthResponse(savedUser);
    }

    public AuthResponse registerSuperAdmin(RegisterRequest request) {

        long superAdminCount = userRepository.countByRole(Role.SUPER_ADMIN);

        if (superAdminCount >= 2) {
            throw new IllegalStateException("Maximum number of super admins reached");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already exists");
        }

        User superAdmin = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.SUPER_ADMIN)
                .build();

        User savedSuperAdmin = userRepository.save(superAdmin);

        return generateAuthResponse(savedSuperAdmin);
    }

    public AuthResponse registerAdmin(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already exists");
        }

        User admin = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.ADMIN)
                .build();

        User savedAdmin = userRepository.save(admin);

        return generateAuthResponse(savedAdmin);
    }

    public AuthResponse login(LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new ResourceNotFoundException("Invalid email or password"));

        return generateAuthResponse(user);
    }

    public AuthResponse refreshToken(String refreshToken) {

        RefreshToken storedToken = refreshTokenRepository.findByToken(refreshToken).orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        if (storedToken.isRevoked()) {
            throw new RuntimeException("Refresh token revoked");
        }

        if (storedToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Refresh token expired");
        }

        User user = storedToken.getUser();

        return generateAuthResponse(user);
    }

    public void logout(String refreshToken) {

        RefreshToken storedToken = refreshTokenRepository.findByToken(refreshToken).orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        storedToken.setRevoked(true);

        refreshTokenRepository.save(storedToken);
    }

    private AuthResponse generateAuthResponse(User user) {

        UserDetails userDetails = org.springframework.security.core.userdetails.User
                        .withUsername(user.getEmail())
                        .password(user.getPassword())
                        .roles(user.getRole().name())
                        .build();

        String accessToken = jwtService.generateToken(userDetails);

        String refreshToken = UUID.randomUUID().toString();

        RefreshToken savedRefreshToken = RefreshToken.builder()
                        .token(refreshToken)
                        .user(user)
                        .expiryDate(LocalDateTime.now().plusDays(7))
                        .revoked(false)
                        .build();

        refreshTokenRepository.save(savedRefreshToken);

        return AuthResponse.builder()
                .token(accessToken)
                .refreshToken(refreshToken)
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }
}
