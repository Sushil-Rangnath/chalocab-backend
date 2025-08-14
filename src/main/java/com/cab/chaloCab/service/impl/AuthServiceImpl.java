package com.cab.chaloCab.service.impl;

import com.cab.chaloCab.dto.AuthRequest;
import com.cab.chaloCab.dto.AuthResponse;
import com.cab.chaloCab.dto.RegisterRequest;
import com.cab.chaloCab.entity.User;
import com.cab.chaloCab.repository.UserRepository;
import com.cab.chaloCab.security.JwtUtil;
import com.cab.chaloCab.service.AuthService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest req) {
        if (userRepo.existsByEmail(req.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already registered");
        }

        User user = User.builder()
                .name(req.getName())
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .role(req.getRole())
                .build();

        userRepo.save(user);

        String accessToken = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
        String refreshToken = jwtUtil.generateRefreshToken(user.getEmail(), user.getRole().name());

        return new AuthResponse(accessToken, refreshToken, user.getRole());
    }

    @Override
    public AuthResponse authenticate(AuthRequest req) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword())
            );
        } catch (AuthenticationException ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        User user = userRepo.findByEmail(req.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        String accessToken = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
        String refreshToken = jwtUtil.generateRefreshToken(user.getEmail(), user.getRole().name());

        return new AuthResponse(accessToken, refreshToken, user.getRole());
    }

    @Override
    public AuthResponse refreshToken(String refreshToken) {
        if (refreshToken == null || !jwtUtil.validateToken(refreshToken) || jwtUtil.isTokenExpired(refreshToken)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or expired refresh token");
        }

        String email = jwtUtil.extractUsername(refreshToken);
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // load UserDetails for extra validation
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        if (!jwtUtil.validateToken(refreshToken, userDetails)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token");
        }

        String newAccess = jwtUtil.generateToken(email, user.getRole().name());
        String newRefresh = jwtUtil.generateRefreshToken(email, user.getRole().name());

        return new AuthResponse(newAccess, newRefresh, user.getRole());
    }
}
