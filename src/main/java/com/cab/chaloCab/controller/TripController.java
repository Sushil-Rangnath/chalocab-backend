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

    // Complete a trip and return the updated BookingDTO
    @PostMapping("/complete/{bookingId}/driver/{driverId}")
    public ResponseEntity<BookingDTO> completeTrip(
            @PathVariable Long bookingId,
            @PathVariable Long driverId) {
        BookingDTO completedBooking = bookingService.completeTrip(bookingId, driverId);
        return ResponseEntity.ok(completedBooking);
    }

    @GetMapping("/history/customer/{customerId}")
    public ResponseEntity<List<BookingDTO>> getCustomerHistory(@PathVariable Long customerId) {
        List<BookingDTO> history = bookingService.getBookingHistoryByCustomer(customerId);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/history/driver/{driverId}")
    public ResponseEntity<List<BookingDTO>> getDriverHistory(@PathVariable Long driverId) {
        List<BookingDTO> history = bookingService.getBookingHistoryByDriver(driverId);
        return ResponseEntity.ok(history);
    }
}