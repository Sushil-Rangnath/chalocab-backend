package com.cab.chaloCab.dto;

import com.cab.chaloCab.entity.DriverRequest;
import com.cab.chaloCab.entity.Driver;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriverRequestResponseDTO {
    private String status; // "success" or "failed"
    private String message; // human-readable message
    private DriverRequest driverRequest; // optional, include request details
    private Driver driver; // optional, include driver details after approval
}
