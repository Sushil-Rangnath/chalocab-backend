package com.cab.chaloCab.dto;

import com.cab.chaloCab.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * AuthResponse represents the authentication response returned to the client,
 * including access/refresh tokens, role, and phone verification status.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String accessToken;
    private String refreshToken;
    private Role role;
    @Builder.Default
    private String tokenType = "Bearer";
    private boolean phoneVerified; // ✅ new field
    private String message;        // optional field for error messages like "Invalid OTP"

    // --------------------------
    // Legacy constructors for backward compatibility
    // --------------------------

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
}
