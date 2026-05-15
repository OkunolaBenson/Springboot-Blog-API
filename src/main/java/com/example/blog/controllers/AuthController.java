package com.example.blog.controllers;

import com.example.blog.DTO.AuthResponse;
import com.example.blog.DTO.LoginRequest;
import com.example.blog.DTO.RegisterRequest;
import com.example.blog.services.AuthService;
import jakarta.validation.Valid;
import lombok.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/admin/register")
    public ResponseEntity<AuthResponse> registerAdmin(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.registerAdmin(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/{refresh}")
    public AuthResponse refreshToken(@RequestParam String refreshToken) {
        return authService.refreshToken(refeshToken);
    }

    @PostMapping("/{logout}")
    public String logout(@RequestParam String refreshToken) {
        authService.logout(refreshToken);
        return "Logged Out Successfully";
    }
}
