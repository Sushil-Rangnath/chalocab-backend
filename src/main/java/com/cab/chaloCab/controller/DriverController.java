package com.cab.chaloCab.controller;

import com.cab.chaloCab.dto.DriverDTO;
import com.cab.chaloCab.dto.DriverRequestDTO;
import com.cab.chaloCab.service.DriverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/drivers")
public class DriverController {

    @Autowired
    private DriverService driverService;

    @PostMapping("/request")
    public ResponseEntity<String> requestRegistration(@RequestBody DriverRequestDTO requestDTO) {
        driverService.submitDriverRequest(requestDTO);
        return ResponseEntity.ok("Driver registration request submitted");
    }

    @GetMapping("/requests")
    public ResponseEntity<List<DriverDTO>> getAllRequests() {
        return ResponseEntity.ok(driverService.getAllDriverRequests());
    }

    @PostMapping("/approve/{requestId}")
    public ResponseEntity<String> approve(@PathVariable Long requestId) {
        driverService.approveDriverRequest(requestId);
        return ResponseEntity.ok("Driver request approved");
    }

    @PostMapping("/reject/{requestId}")
    public ResponseEntity<String> reject(@PathVariable Long requestId) {
        driverService.rejectDriverRequest(requestId);
        return ResponseEntity.ok("Driver request rejected");
    }

    @GetMapping("/approved")
    public ResponseEntity<List<DriverDTO>> getAllApprovedDrivers() {
        return ResponseEntity.ok(driverService.getAllApprovedDrivers());
    }
}
