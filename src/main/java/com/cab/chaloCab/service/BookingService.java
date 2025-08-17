package com.cab.chaloCab.service;

import com.cab.chaloCab.dto.BookingDTO;
import java.util.List;

import com.cab.chaloCab.entity.Booking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookingService {
    BookingDTO createBooking(BookingDTO bookingDTO);
    BookingDTO getBookingById(Long id);
    List<BookingDTO> getAllBookings();
    List<BookingDTO> getBookingsByCustomerId(Long customerId);
    List<BookingDTO> getBookingsByDriverId(Long driverId);
    List<BookingDTO> getBookingsByAssignedDriverId(Long driverId);
    BookingDTO updateBooking(Long id, BookingDTO bookingDTO);
    void deleteBooking(Long id);
    List<BookingDTO> getOutsideStationBookings();
    Double calculateTotalRevenue();
    BookingDTO completeTrip(Long bookingId, Long driverId);
    List<BookingDTO> getBookingHistoryByCustomer(Long customerId, Pageable pageable);
    List<BookingDTO> getBookingHistoryByDriver(Long driverId, Pageable pageable);


    Page<Booking> getAllBookings(Pageable pageable);

    List<BookingDTO> getBookingHistoryByCustomer(Long customerId);

    List<BookingDTO> getBookingHistoryByDriver(Long driverId);




}
