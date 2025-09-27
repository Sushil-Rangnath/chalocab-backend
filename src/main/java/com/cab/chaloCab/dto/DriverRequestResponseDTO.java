package com.cab.chaloCab.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO returned after submitting/fetching a driver request.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriverRequestResponseDTO {

    private String status;       // "success" or "failed"
    private String message;      // human-readable message
    private Long requestId;      // ID of DriverRequest
    private String name;
    private String phone;
    private String licenseNumber;
    private String vehicleRegistration;
    private String vehicleType;
    private String address;
    private String requestStatus; // PENDING, APPROVED, REJECTED
}
