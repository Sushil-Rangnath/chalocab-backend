package com.cab.chaloCab.repository;

import com.cab.chaloCab.entity.Booking;
import com.cab.chaloCab.enums.BookingType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    // Fetch by customer ID
    List<Booking> findByCustomerId(Long customerId);

    // Fetch by driver ID
    List<Booking> findByDriverId(Long driverId);

    // Fetch by assigned driver ID
    List<Booking> findByAssignedDriverId(Long driverId);

    // ✅ Fetch bookings by booking type (OUTSTATION, CITY, RENTAL)
    List<Booking> findByBookingType(BookingType bookingType);

    // ✅ Total revenue (ignores deleted bookings)
    @Query("SELECT SUM(b.fare) FROM Booking b WHERE b.deleted = false")
    Double calculateTotalRevenue();

    // Pagination: all active bookings
    Page<Booking> findByDeletedFalse(Pageable pageable);

    // Pagination: customer active bookings
    Page<Booking> findByCustomerIdAndDeletedFalse(Long customerId, Pageable pageable);

    // Pagination: driver active bookings
    Page<Booking> findByDriverIdAndDeletedFalse(Long driverId, Pageable pageable);

    // Active bookings for customer
    List<Booking> findByCustomerIdAndDeletedFalse(Long customerId);

    // Active bookings for driver
    List<Booking> findByDriverIdAndDeletedFalse(Long driverId);
}
