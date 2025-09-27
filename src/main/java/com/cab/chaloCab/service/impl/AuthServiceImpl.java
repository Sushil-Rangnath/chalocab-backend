package com.cab.chaloCab.service.impl;

import com.cab.chaloCab.dto.AuthRequest;
import com.cab.chaloCab.dto.AuthResponse;
import com.cab.chaloCab.dto.RegisterRequest;
import com.cab.chaloCab.entity.Customer;
import com.cab.chaloCab.entity.Driver;
import com.cab.chaloCab.entity.User;
import com.cab.chaloCab.repository.CustomerRepository;
import com.cab.chaloCab.repository.DriverRepository;
import com.cab.chaloCab.repository.UserRepository;
import com.cab.chaloCab.security.JwtUtil;
import com.cab.chaloCab.service.AuthService;
import com.cab.chaloCab.service.RefreshTokenService;
import io.jsonwebtoken.Claims;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepo;              // Admins
    private final CustomerRepository customerRepository; // Customers
    private final DriverRepository driverRepository;     // Drivers

    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest req) {
        if (userRepo.existsByEmail(req.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already registered");
        }
        if (req.getPhoneNumber() == null || req.getPhoneNumber().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Phone number is required");
        }
        if (userRepo.existsByPhoneNumber(req.getPhoneNumber())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Phone number already registered");
        }

        User user = User.builder()
                .name(req.getName())
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .role(req.getRole())
                .phoneNumber(req.getPhoneNumber())
                .phoneVerified(false)
                .build();

        userRepo.save(user);

        String subject = user.getPhoneNumber();
        String accessToken = jwtUtil.generateToken(subject, user.getRole().name());
        String refreshToken = jwtUtil.generateRefreshToken(subject, user.getRole().name());

        Date refreshExpDate = jwtUtil.extractClaim(refreshToken, Claims::getExpiration);
        Instant refreshExpiresAt = refreshExpDate.toInstant();
        refreshTokenService.createRefreshToken(String.valueOf(user.getId()), refreshToken, refreshExpiresAt, "registration");

        return new AuthResponse(accessToken, refreshToken, user.getRole(), user.isPhoneVerified());
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

        String subject = user.getEmail();
        String accessToken = jwtUtil.generateToken(subject, user.getRole().name());
        String refreshToken = jwtUtil.generateRefreshToken(subject, user.getRole().name());

        Date refreshExpDate = jwtUtil.extractClaim(refreshToken, Claims::getExpiration);
        Instant refreshExpiresAt = refreshExpDate.toInstant();
        refreshTokenService.createRefreshToken(String.valueOf(user.getId()), refreshToken, refreshExpiresAt, "admin-login");

        return new AuthResponse(accessToken, refreshToken, user.getRole(), user.isPhoneVerified());
    }

    @Override
    public AuthResponse refreshToken(String refreshToken) {
        if (refreshToken == null || !jwtUtil.validateToken(refreshToken)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or expired refresh token");
        }

        Optional<com.cab.chaloCab.entity.RefreshToken> persisted = refreshTokenService.findByRawToken(refreshToken);
        if (persisted.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token revoked or not recognized");
        }
        com.cab.chaloCab.entity.RefreshToken current = persisted.get();

        // Subject from token (usually phone/email)
        String subject = jwtUtil.getSubjectFromToken(refreshToken);

        // Try to resolve user across Admin (User), Customer, Driver
        String roleName = null;
        Long userId = null;
        String subjectForToken = null;

        Optional<User> adminOpt = userRepo.findByPhoneNumber(subject);
        if (adminOpt.isEmpty()) {
            adminOpt = userRepo.findByEmail(subject);
        }
        if (adminOpt.isPresent()) {
            User u = adminOpt.get();
            userId = u.getId();
            subjectForToken = u.getEmail(); // admin subject = email
            roleName = u.getRole().name();
        } else {
            Optional<Customer> custOpt = customerRepository.findByPhone(subject);
            if (custOpt.isPresent()) {
                Customer c = custOpt.get();
                userId = c.getId();
                subjectForToken = c.getPhone();
                roleName = c.getRole().name();
            } else {
                Optional<Driver> drvOpt = driverRepository.findByPhoneNumber(subject);
                if (drvOpt.isPresent()) {
                    Driver d = drvOpt.get();
                    userId = d.getId();
                    subjectForToken = d.getPhoneNumber();
                    // FIX: use name() to get String role
                    roleName = d.getRole();
                } else {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
                }
            }
        }

        // rotate: revoke current persisted token
        refreshTokenService.revokeToken(current.getId());

        // create new refresh token (raw) and persist it
        String newRefresh = jwtUtil.generateRefreshToken(subjectForToken, roleName);
        Date exp = jwtUtil.extractClaim(newRefresh, Claims::getExpiration);
        Instant expiresAt = (exp != null) ? exp.toInstant() : Instant.now().plusSeconds(60 * 60 * 24 * 30); // fallback 30d
        refreshTokenService.createRefreshToken(String.valueOf(userId), newRefresh, expiresAt, current.getDeviceInfo());

        // New access token
        String newAccess = jwtUtil.generateToken(subjectForToken, roleName);

        return new AuthResponse(newAccess, newRefresh, com.cab.chaloCab.enums.Role.valueOf(roleName), true);
    }

}
