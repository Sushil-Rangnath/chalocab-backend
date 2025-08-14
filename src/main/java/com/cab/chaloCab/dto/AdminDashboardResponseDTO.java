package com.cab.chaloCab.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminDashboardResponseDTO {
    private long totalCustomers;
    private long totalDrivers;
    private long totalCabs;      // ✅ Added
    private long totalBookings;
    private double totalRevenue;
}
