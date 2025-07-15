package com.cab.chaloCab.entity;

import com.cab.chaloCab.enums.CabType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cab {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String model;

    private String registrationNumber;

    private String color;

    @Enumerated(EnumType.STRING)
    private CabType type;

    private boolean available;
}
