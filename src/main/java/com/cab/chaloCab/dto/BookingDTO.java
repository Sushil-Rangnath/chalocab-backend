package com.cab.chaloCab.dto;

import com.cab.chaloCab.enums.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingDTO {
    private Long id;
    private Long customerId;
    private Long driverId;
    private Long assignedDriverId;
    private String pickupLocation;
    private String dropLocation;
    private String sourceLocation;
    private String destinationLocation;
    private boolean outsideStation;
    private Double fare;
    private Double negotiatedFare;
    private BookingStatus status;
    private LocalDateTime bookingTime;

    // New fields for frontend display
    private String customerName;
    private String customerPhone;
    private String driverName;
    private String driverPhone;
}
