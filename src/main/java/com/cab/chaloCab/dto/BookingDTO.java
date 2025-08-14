package com.cab.chaloCab.dto;

import com.cab.chaloCab.enums.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingDTO {
    private Long id;
    private Long customerId;
    private Long driverId;
    private String pickupLocation;
    private String dropoffLocation;
    private Double fare;
    private BookingStatus status;
    private Long assignedDriverId;  // Optional, useful for trip assignment


    private String sourceLocation;
    private String destinationLocation;
    private Boolean outsideStation;
    private Double negotiatedFare;
}
