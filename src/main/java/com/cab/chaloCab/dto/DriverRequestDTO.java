package com.cab.chaloCab.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for submitting a driver registration request from Flutter.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriverRequestDTO {

    private String name;
    private String email;
    private String phone;
    private String licenseNumber;
    private String vehicleNumber;   // ✅ renamed from vehicleNumber
    private String vehicleType;          // ✅ new field
    private String address;
}
