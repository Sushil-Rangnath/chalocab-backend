package com.cab.chaloCab.entity;

import com.cab.chaloCab.enums.DriverRequestStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

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

    @Column(nullable = false, unique = true)
    private String phoneNumber;

    private String licenseNumber;

    // renamed from vehicleNumber -> vehicleRegistration to match DTO/frontend
    private String vehicleNumber; ;

    // new field to indicate type (SUV, Bike, Sedan, etc.)
    private String vehicleType;

    private String address;

    @Enumerated(EnumType.STRING)
    private DriverRequestStatus status;

    // optional timestamps (helpful for audits). Uncomment if you want to persist them:
    // private Long createdAtEpoch;
    // private Long updatedAtEpoch;

    @PrePersist
    protected void onCreate() {
        if (status == null) {
            status = DriverRequestStatus.PENDING;
        }
        // createdAtEpoch = Instant.now().getEpochSecond();
    }

    @PreUpdate
    protected void onUpdate() {
        // updatedAtEpoch = Instant.now().getEpochSecond();
    }
}
