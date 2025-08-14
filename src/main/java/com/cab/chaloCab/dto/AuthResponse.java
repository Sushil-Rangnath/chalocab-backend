package com.cab.chaloCab.dto;

import com.cab.chaloCab.enums.Role;

public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private Role role;
    private String tokenType = "Bearer";

    public AuthResponse() {}

    public AuthResponse(String accessToken, String refreshToken, Role role) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.role = role;
    }

    // convenience constructor
    public AuthResponse(String accessToken, String refreshToken) {
        this(accessToken, refreshToken, null);
    }

    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }
    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
    public String getTokenType() { return tokenType; }
    public void setTokenType(String tokenType) { this.tokenType = tokenType; }
}
