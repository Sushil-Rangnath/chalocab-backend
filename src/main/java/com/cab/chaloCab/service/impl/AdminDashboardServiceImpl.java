package com.cab.chaloCab.service.impl;

import com.cab.chaloCab.dto.AdminDashboardResponseDTO;
import com.cab.chaloCab.repository.BookingRepository;
import com.cab.chaloCab.repository.CabRepository;
import com.cab.chaloCab.repository.CustomerRepository;
import com.cab.chaloCab.repository.DriverRepository;
import com.cab.chaloCab.service.AdminDashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminDashboardServiceImpl implements AdminDashboardService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private CabRepository cabRepository;  // ✅ Added

    @Override
    public AdminDashboardResponseDTO getDashboardSummary() {
        long totalCustomers = customerRepository.count();
        long totalDrivers = driverRepository.count();
        long totalCabs = cabRepository.count();  // ✅ Added
        long totalBookings = bookingRepository.count();

        // Assuming revenue is sum of all completed booking fares
        Double totalRevenue = bookingRepository.calculateTotalRevenue();
        if (totalRevenue == null) totalRevenue = 0.0;

        // Updated DTO to include totalCabs
        return new AdminDashboardResponseDTO(totalCustomers, totalDrivers, totalCabs, totalBookings, totalRevenue);
    }
}
