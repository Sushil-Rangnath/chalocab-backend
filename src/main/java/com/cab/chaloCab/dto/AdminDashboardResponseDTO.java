package com.cab.chaloCab.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminDashboardResponseDTO {
    private long totalCustomers;
    private long totalDrivers;
    private long totalBookings;
    private double totalRevenue;
}
