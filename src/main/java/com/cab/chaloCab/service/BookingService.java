package com.cab.chaloCab.service;

import com.cab.chaloCab.dto.BookingDTO;
import com.cab.chaloCab.entity.Booking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BookingService {

    // Core booking operations
    BookingDTO createBooking(BookingDTO bookingDTO);
    BookingDTO getBookingById(Long id);
    List<BookingDTO> getAllBookings();
    BookingDTO updateBooking(Long id, BookingDTO bookingDTO);
    void deleteBooking(Long id);

    // Booking by user/driver
    List<BookingDTO> getBookingsByCustomerId(Long customerId);
    List<BookingDTO> getBookingsByDriverId(Long driverId);
    List<BookingDTO> getBookingsByAssignedDriverId(Long driverId);

    // Special cases
    List<BookingDTO> getOutsideStationBookings();
    Double calculateTotalRevenue();
    BookingDTO completeTrip(Long bookingId, Long driverId);

    // Booking history with pagination
    List<BookingDTO> getBookingHistoryByCustomer(Long customerId, Pageable pageable);
    List<BookingDTO> getBookingHistoryByDriver(Long driverId, Pageable pageable);

    // Pagination support for admin
    Page<Booking> getAllBookings(Pageable pageable);

}
