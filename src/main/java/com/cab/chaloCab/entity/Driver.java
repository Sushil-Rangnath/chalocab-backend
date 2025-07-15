package com.cab.chaloCab.entity;

import com.cab.chaloCab.enums.DriverStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "drivers")
public class Driver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private String phoneNumber;
    private String licenseNumber;
    private String vehicleNumber;

    @Enumerated(EnumType.STRING)
    private DriverStatus status;
}
