package com.cab.chaloCab.dto;

import com.cab.chaloCab.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private Role role;
}
