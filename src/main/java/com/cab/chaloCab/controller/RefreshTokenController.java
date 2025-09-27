package com.cab.chaloCab.controller;

import com.cab.chaloCab.dto.AuthResponse;
import com.cab.chaloCab.entity.User;
import com.cab.chaloCab.repository.UserRepository;
import com.cab.chaloCab.security.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class RefreshTokenController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/refresh-token")
    public ResponseEntity<AuthResponse> refreshToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().build();
        }

        String refreshToken = authHeader.substring(7);

        if (!jwtUtil.validateToken(refreshToken)) {
            return ResponseEntity.status(401).build(); // Invalid refresh token
        }

        String subject = jwtUtil.getSubjectFromToken(refreshToken);
        String role = jwtUtil.getRoleFromToken(refreshToken);

        // Find user (match phone or email depending on subject)
        User user = userRepository.findByPhoneNumber(subject)
                .orElseGet(() -> userRepository.findByEmail(subject).orElse(null));

        if (user == null) {
            return ResponseEntity.status(404).build(); // User not found
        }

        String newAccessToken = jwtUtil.generateToken(subject, role);

        return ResponseEntity.ok(
                new AuthResponse(newAccessToken, refreshToken, user.getRole(), user.isPhoneVerified())
        );
    }
}
