package com.cab.chaloCab.controller;

import com.cab.chaloCab.dto.DriverRequestDTO;
import com.cab.chaloCab.dto.DriverRequestResponseDTO;
import com.cab.chaloCab.entity.DriverRequest;
import com.cab.chaloCab.service.DriverRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/driver-requests")
public class DriverRequestController {

    @Autowired
    private DriverRequestService driverRequestService;

    @PostMapping("/submit")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    public DriverRequestResponseDTO submitRequest(@RequestBody DriverRequestDTO dto) {
        return driverRequestService.submitRequest(dto);
    }

    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public List<DriverRequest> getPendingRequests() {
        return driverRequestService.getAllPendingRequests();
    }

    @PostMapping("/approve/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public DriverRequestResponseDTO approve(@PathVariable Long id) {
        return driverRequestService.approveRequest(id);
    }

    @PostMapping("/reject/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public DriverRequestResponseDTO reject(@PathVariable Long id) {
        return driverRequestService.rejectRequest(id);
    }

    @PostMapping("/status/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public DriverRequestResponseDTO updateStatus(@PathVariable Long id, @RequestParam String status) {
        return driverRequestService.updateStatus(id, Enum.valueOf(com.cab.chaloCab.enums.DriverRequestStatus.class, status.toUpperCase()));
    }
}
