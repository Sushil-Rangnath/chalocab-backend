package com.cab.chaloCab.controller;

import com.cab.chaloCab.dto.*;
import com.cab.chaloCab.entity.User;
import com.cab.chaloCab.repository.UserRepository;
import com.cab.chaloCab.security.JwtUtil;
import com.cab.chaloCab.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        return ResponseEntity.ok(authService.authenticate(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestBody RefreshRequest req) {
        String refreshToken = req.getRefreshToken();
        return ResponseEntity.ok(authService.refreshToken(refreshToken));
    }
}
