package com.cab.chaloCab.entity;

import com.cab.chaloCab.enums.CabType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "fares")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Fare {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private CabType cabType;

    @Column(nullable = false)
    private double baseFare;

    @Column(nullable = false)
    private double perKmFare;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private boolean deleted = false;
}
