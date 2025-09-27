package com.cab.chaloCab.service.impl;

import com.cab.chaloCab.entity.RefreshToken;
import com.cab.chaloCab.repository.RefreshTokenRepository;
import com.cab.chaloCab.service.RefreshTokenService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    @Transactional
    public RefreshToken createRefreshToken(String userId, String rawToken, Instant expiresAt, String deviceInfo) {
        String hash = sha256Hex(rawToken);
        RefreshToken rt = RefreshToken.builder()
                .userId(userId)
                .tokenHash(hash)
                .deviceInfo(deviceInfo)
                .issuedAt(Instant.now())
                .expiresAt(expiresAt)
                .revoked(false)
                .build();
        return refreshTokenRepository.save(rt);
    }

    @Override
    public Optional<RefreshToken> findByRawToken(String rawToken) {
        String hash = sha256Hex(rawToken);
        Optional<RefreshToken> opt = refreshTokenRepository.findByTokenHashAndRevokedFalse(hash);
        if (opt.isEmpty()) return Optional.empty();
        RefreshToken rt = opt.get();
        // check expiry
        if (rt.getExpiresAt() != null && rt.getExpiresAt().isBefore(Instant.now())) {
            return Optional.empty();
        }
        return Optional.of(rt);
    }

    @Override
    @Transactional
    public void revokeToken(String tokenId) {
        refreshTokenRepository.findById(tokenId).ifPresent(rt -> {
            rt.setRevoked(true);
            refreshTokenRepository.save(rt);
        });
    }

    @Override
    @Transactional
    public void removeExpiredTokens() {
        refreshTokenRepository.findAll().stream()
                .filter(rt -> rt.getExpiresAt() != null && rt.getExpiresAt().isBefore(Instant.now()))
                .forEach(rt -> refreshTokenRepository.delete(rt));
    }

    // -------------------------
    // New implementations
    // -------------------------

    @Override
    @Transactional
    public void revokeTokensForUserByDevice(String userId, String deviceInfoJson) {
        if (userId == null) return;
        List<RefreshToken> tokens = refreshTokenRepository.findByUserId(userId);
        if (tokens == null || tokens.isEmpty()) return;
        for (RefreshToken t : tokens) {
            String stored = t.getDeviceInfo();
            if (stored != null && stored.equals(deviceInfoJson) && !t.isRevoked()) {
                t.setRevoked(true);
                refreshTokenRepository.save(t);
            }
        }
    }

    @Override
    @Transactional
    public void revokeAllTokensForUser(String userId) {
        if (userId == null) return;
        List<RefreshToken> tokens = refreshTokenRepository.findByUserId(userId);
        if (tokens == null || tokens.isEmpty()) return;
        for (RefreshToken t : tokens) {
            if (!t.isRevoked()) {
                t.setRevoked(true);
                refreshTokenRepository.save(t);
            }

        }
    }

    // -------- helper hashing (SHA-256 hex) ----------
    private static String sha256Hex(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(2 * digest.length);
            for (byte b : digest) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();
        } catch (Exception ex) {
            throw new RuntimeException("Failed to compute SHA-256 hash", ex);
        }
    }
}
