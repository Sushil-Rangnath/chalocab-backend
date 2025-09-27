package com.cab.chaloCab.service;

import com.cab.chaloCab.entity.RefreshToken;

import java.time.Instant;
import java.util.Optional;

public interface RefreshTokenService {

    /**
     * Persist a raw refresh token (store only its SHA-256 hash).
     *
     * @param userId      user identifier (string)
     * @param rawToken    the raw refresh token string (will be hashed before saving)
     * @param expiresAt   expiry instant for token
     * @param deviceInfo  optional device string (can be null)
     * @return the persisted RefreshToken entity
     */
    RefreshToken createRefreshToken(String userId, String rawToken, Instant expiresAt, String deviceInfo);

    /**
     * Lookup a persisted token by the raw token presented by client.
     * Returns Optional.empty() if not found, expired, or revoked.
     */
    Optional<RefreshToken> findByRawToken(String rawToken);

    /**
     * Revoke (mark revoked=true) the token by its id (UUID string).
     */
    void revokeToken(String tokenId);

    /**
     * (Optional) Remove expired tokens from DB. You can call periodically if desired.
     */
    void removeExpiredTokens();

    // -------------------------
    // New methods: revoke by user/device
    // -------------------------

    /**
     * Revoke all refresh tokens for a given user that match the provided deviceInfo JSON.
     *
     * @param userId          user identifier as stored in RefreshToken.userId
     * @param deviceInfoJson  canonical JSON string for device info (must match stored deviceInfo)
     */
    void revokeTokensForUserByDevice(String userId, String deviceInfoJson);

    /**
     * Revoke all refresh tokens for the given user (logout from all devices).
     *
     * @param userId user identifier as stored in RefreshToken.userId
     */
    void revokeAllTokensForUser(String userId);
}
