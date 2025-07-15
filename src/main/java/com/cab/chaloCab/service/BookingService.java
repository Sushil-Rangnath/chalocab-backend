package com.cab.chaloCab.service;

import com.cab.chaloCab.dto.BookingDTO;
import com.cab.chaloCab.enums.BookingStatus;

import java.util.List;

public interface BookingService {
    BookingDTO createBooking(BookingDTO bookingDTO);
    BookingDTO updateBookingStatus(Long bookingId, BookingStatus status);
    List<BookingDTO> getAllBookings();
    List<BookingDTO> getBookingsByCustomerId(Long customerId);
    List<BookingDTO> getBookingsByDriverId(Long driverId);
    BookingDTO getBookingById(Long id);
    List<BookingDTO> getBookingHistoryByCustomer(Long customerId);
    List<BookingDTO> getBookingHistoryByDriver(Long driverId);
    String completeTrip(Long bookingId, Long driverId);
    String getDropoffLocation(Long bookingId);
    Long getAssignedDriverId(Long bookingId);
}
