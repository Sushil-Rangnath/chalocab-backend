package com.cab.chaloCab.controller;

import com.cab.chaloCab.dto.BookingDTO;
import com.cab.chaloCab.entity.Customer;
import com.cab.chaloCab.enums.BookingType;
import com.cab.chaloCab.repository.CustomerRepository;
import com.cab.chaloCab.service.BookingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/flutter/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {

    private final BookingService bookingService;
    private final CustomerRepository customerRepository;

    @PostMapping("/create")
    public ResponseEntity<?> createBooking(@RequestBody BookingDTO bookingDTO,
                                           Authentication authentication) {
        // 1) check auth
        if (authentication == null || !authentication.isAuthenticated()) {
            log.warn("Unauthorized createBooking attempt");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        String tokenSub = authentication.getName();
        log.info("createBooking: tokenSub='{}'", tokenSub);
        if (tokenSub == null || tokenSub.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized - no subject");
        }

        Optional<Customer> maybeCustomer = customerRepository.findByPhoneAndDeletedFalse(tokenSub);
        if (maybeCustomer.isEmpty()) {
            log.warn("createBooking: no customer found for phone='{}'. Returning 401.", tokenSub);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized - no customer");
        }
        Customer customer = maybeCustomer.get();
        Long resolvedUserId = customer.getId();

        bookingDTO.setCustomerId(resolvedUserId);
        log.info("createBooking: overridden bookingDTO.customerId -> {}", resolvedUserId);

        // 4) validate & delegate
        if (bookingDTO.getBookingType() == BookingType.LOCAL && bookingDTO.getDistanceKm() == null) {
            log.warn("createBooking: missing distanceKm for userId={}", resolvedUserId);
            return ResponseEntity.badRequest().body("distanceKm is required");
        }

        try {
            BookingDTO created = bookingService.createBooking(bookingDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (Exception ex) {
            // Log and return error message to client (helps Flutter show reason)
            log.error("createBooking: failed for userId={}: {}", resolvedUserId, ex.toString(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create booking: " + ex.getMessage());
        }
    }

    @GetMapping("/{id}")
    public BookingDTO getBookingById(@PathVariable Long id) {
        return bookingService.getBookingById(id);
    }

    @GetMapping("/all")
    public List<BookingDTO> getAllBookings() {
        return bookingService.getAllBookings();
    }

    @GetMapping("/customer/{customerId}/history")
    public List<BookingDTO> getBookingHistoryByCustomer(
            @PathVariable Long customerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return bookingService.getBookingHistoryByCustomer(customerId, PageRequest.of(page, size));
    }

    @GetMapping("/driver/{driverId}/history")
    public List<BookingDTO> getBookingHistoryByDriver(
            @PathVariable Long driverId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return bookingService.getBookingHistoryByDriver(driverId, PageRequest.of(page, size));
    }

    @GetMapping("/assignedDriver/{driverId}")
    public List<BookingDTO> getBookingsByAssignedDriver(@PathVariable Long driverId) {
        return bookingService.getBookingsByAssignedDriverId(driverId);
    }

    @PutMapping("/update/{id}")
    public BookingDTO updateBooking(@PathVariable Long id, @RequestBody BookingDTO bookingDTO) {
        return bookingService.updateBooking(id, bookingDTO);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteBooking(@PathVariable Long id) {
        bookingService.deleteBooking(id);
    }

    @GetMapping("/outside-station")
    public List<BookingDTO> getOutsideStationBookings() {
        return bookingService.getOutsideStationBookings();
    }

    @GetMapping("/revenue")
    public Double getTotalRevenue() {
        return bookingService.calculateTotalRevenue();
    }
}
