package com.cab.chaloCab.repository;

import com.cab.chaloCab.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {
    Optional<RefreshToken> findByTokenHashAndRevokedFalse(String tokenHash);
    Optional<RefreshToken> findByTokenHash(String tokenHash);
    List<RefreshToken> findByUserIdAndRevokedFalse(String userId);
    List<RefreshToken> findByUserId(String userId);
}
