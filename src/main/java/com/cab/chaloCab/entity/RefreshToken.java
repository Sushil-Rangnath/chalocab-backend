package com.cab.chaloCab.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.Instant;

@Entity
@Table(name = "refresh_tokens", indexes = {
        @Index(name = "idx_refresh_user", columnList = "user_id"),
        @Index(name = "idx_refresh_tokenhash", columnList = "token_hash")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "id", nullable = false, updatable = false, length = 36, columnDefinition = "VARCHAR(36)")
    private String id; // UUID as string

    @Column(name = "user_id", nullable = false, length = 128)
    private String userId; // store user PK as string (flexible: Long/UUID)

    @Column(name = "token_hash", nullable = false, length = 128)
    private String tokenHash;

    @Column(
            name = "device_info",
            nullable = false,
            columnDefinition = "LONGTEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci"
    )
    private String deviceInfo;

    @Column(name = "issued_at", nullable = false)
    @Builder.Default
    private Instant issuedAt = Instant.now();

    @Column(name = "expires_at")
    private Instant expiresAt;

    @Column(name = "revoked", nullable = false)
    @Builder.Default
    private boolean revoked = false;

    // Convenience constructor for new token creation
    public RefreshToken(String userId, String tokenHash, Instant expiresAt, String deviceInfo) {
        this.userId = userId;
        this.tokenHash = tokenHash;
        this.expiresAt = expiresAt;
        this.deviceInfo = deviceInfo;
        this.issuedAt = Instant.now();
        this.revoked = false;
    }
}
