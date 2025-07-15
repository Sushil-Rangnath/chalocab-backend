package com.cab.chaloCab.service;

import com.cab.chaloCab.dto.AuthRequest;
import com.cab.chaloCab.dto.AuthResponse;
import com.cab.chaloCab.dto.RegisterRequest;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse authenticate(AuthRequest request);
}
