package com.cab.chaloCab.dto;

import com.cab.chaloCab.enums.DriverRequestStatus;
import com.cab.chaloCab.enums.DriverStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriverDTO {
    private Long id;
    private String name;
    private String email;
    private String phone;  // ✅ Add this field
    private String licenseNumber;
    private String vehicleNumber;
    private String status; // Store enum as string for frontend/API
}
