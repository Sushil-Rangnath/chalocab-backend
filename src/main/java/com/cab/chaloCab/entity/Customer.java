package com.cab.chaloCab.entity;

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

    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(unique = true, nullable = false)
    private String phone; // Unique for search

    private String address;

    @Builder.Default
    @Column(nullable = false)
    private boolean deleted = false; // For soft delete
}
