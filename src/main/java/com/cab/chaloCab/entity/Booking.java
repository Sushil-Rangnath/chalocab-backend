package com.cab.chaloCab.entity;

import com.cab.chaloCab.enums.BookingStatus;
import com.cab.chaloCab.enums.BookingType;
import com.cab.chaloCab.enums.CabType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Customer making the booking
    @Column(nullable = false)
    private Long customerId;

    // Driver assigned (nullable until assigned)
    private Long driverId;

    private Long assignedDriverId;

    // Booking type: CITY, OUTSTATION, RENTAL
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingType bookingType;

    @Column(name = "cab_type", nullable = false)
    private String cabType;


    // Common fields (City Ride)
    private String pickupLocation;
    private String dropLocation;
    private Double distanceKm;

    // Outstation fields
    private String fromCity;          // text area (free input)
    private String toCity;            // text area (free input)
    private LocalDate travelDate;     // date picker
    private String additionalDetails; // textarea for extra info

    // Rental (future: list cars/hourly rates)
    private String rentalPackage;     // placeholder for rental plan

    // Fare
    private Double fare;
    private Double negotiatedFare;

    // Booking timing
    private LocalDateTime bookingTime;

    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    // Soft delete flag
    private boolean deleted = false;
}
