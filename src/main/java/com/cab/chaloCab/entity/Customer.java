package com.cab.chaloCab.entity;

import com.cab.chaloCab.enums.Role;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = true)
    private String email; // optional for Flutter, unique if provided

    @Column(unique = true, nullable = false)
    private String phone; // mandatory, unique for search/login

    private String address; // optional

    @Builder.Default
    @Column(nullable = false)
    private boolean deleted = false; // soft delete

    // Optionally, for storing JWT refresh token reference
    private String refreshToken;

    private boolean active = true; // account activation/deactivation

    // Optional: Track last login time or OTP info
    private String lastOtp;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.CUSTOMER;
}
