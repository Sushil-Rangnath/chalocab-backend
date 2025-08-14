package com.cab.chaloCab.entity;

import com.cab.chaloCab.enums.RequestStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "outside_station_requests")
public class OutsideStationRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sourceLocation;
    private String destinationLocation;

    private String contactName;
    private String contactPhone;
    private String contactEmail;

    @Column(length = 1000)
    private String notes;  // Optional user message

    private LocalDateTime requestTime;

    @Enumerated(EnumType.STRING)
    private RequestStatus status; // e.g. OPEN, CLOSED

}
