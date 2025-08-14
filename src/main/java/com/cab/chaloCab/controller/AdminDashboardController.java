package com.cab.chaloCab.controller;

import com.cab.chaloCab.dto.AdminDashboardResponseDTO;
import com.cab.chaloCab.service.AdminDashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")

public class AdminDashboardController {

    @Autowired
    private AdminDashboardService dashboardService;

    // Renamed to avoid conflict with AdminController
    @GetMapping("/dashboard/summary")
    public AdminDashboardResponseDTO getDashboardSummary() {
        return dashboardService.getDashboardSummary();
    }
}
