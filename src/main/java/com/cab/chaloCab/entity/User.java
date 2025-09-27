package com.cab.chaloCab.entity;

import com.cab.chaloCab.enums.Role;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // Full name of the user

    @Column(unique = true)
    private String email; // Optional email (unique, can be null for OTP-only users)

    private String password; // Encrypted password (null for OTP-only login)

    @Enumerated(EnumType.STRING)
    private Role role; // ✅ Stored as STRING (ADMIN, CUSTOMER, DRIVER, etc.)

    @Column(name = "phone_number", unique = true, nullable = false)
    private String phoneNumber; // ✅ Mandatory & Unique (primary login identifier for OTP flow)

    @Column(nullable = false)
    private boolean phoneVerified = false; // Marks whether the phone is verified via OTP

    // ✅ OTP-based login fields
    private String otp;  // 🔥 Current OTP for verification (temporary, expires after some time)

    private LocalDateTime otpExpiry; // Expiry timestamp of OTP
}
