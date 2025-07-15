package com.cab.chaloCab.entity;

import com.cab.chaloCab.enums.DriverRequestStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DriverRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String email;

    private String phoneNumber;

    private String licenseNumber;

    private String vehicleNumber;

    private String address;
    private String phone;

    @Enumerated(EnumType.STRING)
    private DriverRequestStatus status;
}
