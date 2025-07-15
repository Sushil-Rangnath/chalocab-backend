package com.cab.chaloCab.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DriverRequest {
    private String name;
    private String licenseNumber;
    private String phoneNumber;
    private boolean available;
}
