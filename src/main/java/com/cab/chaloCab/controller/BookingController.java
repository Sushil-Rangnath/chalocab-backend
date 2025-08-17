package com.cab.chaloCab.controller;

import com.cab.chaloCab.dto.BookingDTO;
import com.cab.chaloCab.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping("/create")
    public BookingDTO createBooking(@RequestBody BookingDTO bookingDTO) {
        return bookingService.createBooking(bookingDTO);
    }

    @GetMapping("/{id}")
    public BookingDTO getBookingById(@PathVariable Long id) {
        return bookingService.getBookingById(id);
    }

    @GetMapping("/all")
    public List<BookingDTO> getAllBookings() {
        return bookingService.getAllBookings();
    }

    @GetMapping("/customer/{customerId}")
    public List<BookingDTO> getBookingsByCustomer(@PathVariable Long customerId) {
        return bookingService.getBookingsByCustomerId(customerId);
    }

    @GetMapping("/driver/{driverId}")
    public List<BookingDTO> getBookingsByDriver(@PathVariable Long driverId) {
        return bookingService.getBookingsByDriverId(driverId);
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
