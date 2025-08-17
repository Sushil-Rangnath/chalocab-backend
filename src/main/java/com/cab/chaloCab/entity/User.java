package com.cab.chaloCab.entity;

import com.cab.chaloCab.enums.Role;
import jakarta.persistence.*;
import lombok.*;

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

    private String name;

    @Column(unique = true)
    private String email;

    private String password;

    @Enumerated(EnumType.STRING)
    private Role role; // ✅ Stored as string

    @Column(name = "phone_number", unique = true, nullable = false)
    private String phoneNumber; // ✅ Mandatory & Unique
    @Column(nullable = false)
    private boolean phoneVerified = false;
}
