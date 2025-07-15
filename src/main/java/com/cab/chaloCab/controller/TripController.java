package com.cab.chaloCab.controller;

import com.cab.chaloCab.dto.BookingDTO;
import com.cab.chaloCab.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trip")
public class TripController {

    @Autowired
    private BookingService bookingService;

    @PostMapping("/complete/{bookingId}/driver/{driverId}")
    public ResponseEntity<String> completeTrip(
            @PathVariable Long bookingId,
            @PathVariable Long driverId) {
        return ResponseEntity.ok(bookingService.completeTrip(bookingId, driverId));
    }

    @GetMapping("/history/customer/{customerId}")
    public ResponseEntity<List<BookingDTO>> getCustomerHistory(@PathVariable Long customerId) {
        return ResponseEntity.ok(bookingService.getBookingHistoryByCustomer(customerId));
    }

    @GetMapping("/history/driver/{driverId}")
    public ResponseEntity<List<BookingDTO>> getDriverHistory(@PathVariable Long driverId) {
        return ResponseEntity.ok(bookingService.getBookingHistoryByDriver(driverId));
    }
}
