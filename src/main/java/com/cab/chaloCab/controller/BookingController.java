package com.cab.chaloCab.controller;

import com.cab.chaloCab.dto.BookingDTO;
import com.cab.chaloCab.enums.BookingStatus;
import com.cab.chaloCab.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @PostMapping("/create")
    public ResponseEntity<BookingDTO> createBooking(@RequestBody BookingDTO bookingDTO) {
        return ResponseEntity.ok(bookingService.createBooking(bookingDTO));
    }

    @PutMapping("/{bookingId}/status")
    public ResponseEntity<BookingDTO> updateBookingStatus(
            @PathVariable Long bookingId,
            @RequestParam BookingStatus status) {
        return ResponseEntity.ok(bookingService.updateBookingStatus(bookingId, status));
    }

    @GetMapping("/all")
    public ResponseEntity<List<BookingDTO>> getAllBookings() {
        return ResponseEntity.ok(bookingService.getAllBookings());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingDTO> getBookingById(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.getBookingById(id));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<BookingDTO>> getBookingsByCustomer(@PathVariable Long customerId) {
        return ResponseEntity.ok(bookingService.getBookingsByCustomerId(customerId));
    }

    @GetMapping("/driver/{driverId}")
    public ResponseEntity<List<BookingDTO>> getBookingsByDriver(@PathVariable Long driverId) {
        return ResponseEntity.ok(bookingService.getBookingsByDriverId(driverId));
    }

    @GetMapping("/{bookingId}/dropoff")
    public ResponseEntity<String> getDropoffLocation(@PathVariable Long bookingId) {
        return ResponseEntity.ok(bookingService.getDropoffLocation(bookingId));
    }

    @GetMapping("/{bookingId}/assigned-driver")
    public ResponseEntity<Long> getAssignedDriverId(@PathVariable Long bookingId) {
        return ResponseEntity.ok(bookingService.getAssignedDriverId(bookingId));
    }
}
