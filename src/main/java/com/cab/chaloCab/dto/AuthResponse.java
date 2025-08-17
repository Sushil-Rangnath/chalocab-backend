package com.cab.chaloCab.dto;

import com.cab.chaloCab.enums.Role;

public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private Role role;
    private String tokenType = "Bearer";
    private boolean phoneVerified; // ✅ new field

    public AuthResponse() {}

    // Main constructor with phone verification
    public AuthResponse(String accessToken, String refreshToken, Role role, boolean phoneVerified) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.role = role;
        this.phoneVerified = phoneVerified;
    }

    // Convenience constructor (role only, phoneVerified defaults to false)
    public AuthResponse(String accessToken, String refreshToken, Role role) {
        this(accessToken, refreshToken, role, false);
    }

    // Convenience constructor (no role, no phoneVerified)
    public AuthResponse(String accessToken, String refreshToken) {
        this(accessToken, refreshToken, null, false);
    }

    // getters & setters
    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }

    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public String getTokenType() { return tokenType; }
    public void setTokenType(String tokenType) { this.tokenType = tokenType; }

    public boolean isPhoneVerified() { return phoneVerified; }
    public void setPhoneVerified(boolean phoneVerified) { this.phoneVerified = phoneVerified; }
}
