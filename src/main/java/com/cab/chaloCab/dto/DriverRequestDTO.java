package com.cab.chaloCab.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriverRequestDTO {

    private String name;
    private String email;
    private String phone;
    private String licenseNumber;
    private String vehicleNumber;
    private String address;
}
