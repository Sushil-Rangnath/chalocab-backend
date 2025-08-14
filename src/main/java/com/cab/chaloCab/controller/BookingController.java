package com.cab.chaloCab.controller;

import com.cab.chaloCab.dto.BookingDTO;
import com.cab.chaloCab.enums.BookingStatus;
import com.cab.chaloCab.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

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

    @PutMapping("/{bookingId}/soft-delete")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> softDeleteBooking(@PathVariable Long bookingId) {
        bookingService.softDeleteBooking(bookingId);
        return ResponseEntity.ok("Booking cancelled and soft deleted successfully");
    }

    @PutMapping("/{bookingId}/negotiated-fare")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BookingDTO> updateNegotiatedFare(
            @PathVariable Long bookingId,
            @RequestParam Double newFare) {
        return ResponseEntity.ok(bookingService.updateNegotiatedFare(bookingId, newFare));
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

    @GetMapping("/outside-station")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<BookingDTO>> getOutsideStationBookings() {
        return ResponseEntity.ok(bookingService.getOutsideStationBookings());
    }

    @GetMapping("/filter")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<BookingDTO>> getFilteredBookings(
            @RequestParam(required = false) BookingStatus status,
            @RequestParam(required = false) Boolean outsideStation,
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) Long driverId,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<BookingDTO> bookings = bookingService.getBookingsWithFilters(
                status, outsideStation, customerId, driverId, keyword, pageable);
        return ResponseEntity.ok(bookings);
    }
    @PutMapping("/{bookingId}/cancel")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> cancelBooking(@PathVariable Long bookingId) {
        bookingService.softDeleteBooking(bookingId);
        return ResponseEntity.ok("Booking cancelled successfully");
    }
}
