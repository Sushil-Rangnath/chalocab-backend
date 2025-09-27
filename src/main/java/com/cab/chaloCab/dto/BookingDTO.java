package com.cab.chaloCab.dto;

import com.cab.chaloCab.enums.BookingStatus;
import com.cab.chaloCab.enums.BookingType;
import com.cab.chaloCab.enums.CabType;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingDTO {

    private Long id;

    private Long customerId;
    private Long driverId;
    private Long assignedDriverId;
    private String cabType;
    private Double distanceKm;

    private BookingType bookingType; // CITY, OUTSTATION, RENTAL

    // -------- City Booking --------
    private String pickupLocation;
    private String dropLocation;

    // -------- Outstation Booking --------
    private String fromCity;
    private String toCity;
    private LocalDate travelDate;
    private String additionalDetails;

    // -------- Rental Booking --------
    private String rentalPackage;

    // -------- Common --------
    private Double fare;
    private Double negotiatedFare;
    private LocalDateTime bookingTime;
    private BookingStatus status;

    private boolean deleted;
}
