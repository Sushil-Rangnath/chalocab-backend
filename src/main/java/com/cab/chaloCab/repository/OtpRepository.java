package com.cab.chaloCab.repository;

import com.cab.chaloCab.entity.OtpCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OtpRepository extends JpaRepository<OtpCode, Long> {

    Optional<OtpCode> findTopByPhoneAndUsedFalseOrderByExpiresAtDesc(String phone);
}
