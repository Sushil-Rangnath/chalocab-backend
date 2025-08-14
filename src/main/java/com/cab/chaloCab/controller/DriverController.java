// File: com.cab.chaloCab.controller.DriverController.java
package com.cab.chaloCab.controller;

import com.cab.chaloCab.dto.DriverDTO;
import com.cab.chaloCab.dto.DriverRequestDTO;
import com.cab.chaloCab.entity.Driver;
import com.cab.chaloCab.enums.DriverStatus;
import com.cab.chaloCab.service.DriverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/drivers")
public class DriverController {

    @Autowired
    private DriverService driverService;

    /**
     * Submit a new driver registration request
     */
    @PostMapping("/request")
    public ResponseEntity<String> requestRegistration(@RequestBody DriverRequestDTO requestDTO) {
        driverService.submitDriverRequest(requestDTO);
        return ResponseEntity.ok("Driver registration request submitted");
    }

    /**
     * View all pending driver requests (Admin only)
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/requests")
    public ResponseEntity<List<DriverDTO>> getAllRequests() {
        return ResponseEntity.ok(driverService.getAllDriverRequests());
    }

    /**
     * Approve a driver request (Admin only)
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/approve/{requestId}")
    public ResponseEntity<String> approve(@PathVariable Long requestId) {
        driverService.approveDriverRequest(requestId);
        return ResponseEntity.ok("Driver request approved");
    }

    /**
     * Reject a driver request (Admin only)
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/reject/{requestId}")
    public ResponseEntity<String> reject(@PathVariable Long requestId) {
        driverService.rejectDriverRequest(requestId);
        return ResponseEntity.ok("Driver request rejected");
    }

    /**
     * Get all approved drivers (paginated, optional search)
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/approved")
    public Page<Driver> getApprovedDrivers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search) {
        return driverService.getApprovedDrivers(page, size, search);
    }

    /**
     * Get drivers by status (paginated)
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/status/{status}")
    public Page<Driver> getDriversByStatus(
            @PathVariable DriverStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return driverService.getDriversByStatus(status, PageRequest.of(page, size));
    }

    /**
     * Search drivers by status + keyword
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/status/{status}/search")
    public Page<Driver> searchDriversByStatus(
            @PathVariable DriverStatus status,
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return driverService.searchDriversByStatus(status, keyword, PageRequest.of(page, size));
    }
}
