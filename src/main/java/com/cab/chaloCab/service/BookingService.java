package com.cab.chaloCab.service;

import com.cab.chaloCab.dto.BookingDTO;
import com.cab.chaloCab.enums.BookingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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

    List<BookingDTO> getOutsideStationBookings();

    BookingDTO updateNegotiatedFare(Long bookingId, Double newFare);

    // Pagination and filtering
    Page<BookingDTO> getBookingsWithFilters(
            BookingStatus status,
            Boolean outsideStation,
            Long customerId,
            Long driverId,
            String keyword,
            Pageable pageable);

    // Soft delete / cancel booking
    void softDeleteBooking(Long bookingId);
}
