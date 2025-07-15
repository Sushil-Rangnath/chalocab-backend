package com.cab.chaloCab.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DriverResponse {
    private Long id;
    private String name;
    private String licenseNumber;
    private String phoneNumber;
    private boolean available;
}
