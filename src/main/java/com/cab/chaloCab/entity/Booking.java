package com.cab.chaloCab.entity;

import com.cab.chaloCab.enums.BookingStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "driver_id")
    private Driver driver;

    @Column(name = "pickup_location")
    private String pickupLocation;

    @Column(name = "drop_location")
    private String dropoffLocation;

    @Column(name = "fare")
    private Double fare;

    @Column(name = "booking_time")
    private LocalDateTime bookingTime;

    @Column(name = "assigned_driver_id")
    private Long assignedDriverId;


    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private BookingStatus status;

    @Column(nullable = true)
    private String sourceLocation;

    @Column(nullable = true)
    private String destinationLocation;

    @Column(nullable = true)
    private Boolean outsideStation;

    @Column(nullable = true)
    private Double negotiatedFare;
    @Column(nullable = false)
    private boolean deleted = false;

}
