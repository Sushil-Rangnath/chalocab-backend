package com.cab.chaloCab.controller;

import com.cab.chaloCab.service.RideAssignmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ride")
public class RideAssignmentController {

    @Autowired
    private RideAssignmentService rideService;

    // Only ADMIN should assign
    @PostMapping("/assign/{bookingId}")
    public ResponseEntity<String> assignDriver(@PathVariable Long bookingId) {
        return ResponseEntity.ok(rideService.assignDriverToBooking(bookingId));
    }

    // Driver accepts booking
    @PostMapping("/accept/{bookingId}/driver/{driverId}")
    public ResponseEntity<String> acceptBooking(
            @PathVariable Long bookingId,
            @PathVariable Long driverId) {
        return ResponseEntity.ok(rideService.driverAcceptBooking(bookingId, driverId));
    }
}
