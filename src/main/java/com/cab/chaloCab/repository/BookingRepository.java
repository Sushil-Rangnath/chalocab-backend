package com.cab.chaloCab.repository;

import com.cab.chaloCab.entity.Booking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    // Fetch by customer ID
    List<Booking> findByCustomer_Id(Long customerId);

    // Fetch by driver ID (driver field)
    List<Booking> findByDriver_Id(Long driverId);

    // Fetch by assigned driver ID (assignedDriver field)
    List<Booking> findByAssignedDriverId(Long driverId);

    // Fetch bookings outside station
    List<Booking> findByOutsideStationTrue();

    // Total revenue
    @Query("SELECT SUM(b.fare) FROM Booking b WHERE b.deleted = false")
    Double calculateTotalRevenue();

    // 🔹 Pagination support: all active bookings
    Page<Booking> findByDeletedFalse(Pageable pageable);

    // 🔹 Pagination support: customer active bookings
    Page<Booking> findByCustomer_IdAndDeletedFalse(Long customerId, Pageable pageable);

    // 🔹 Pagination support: driver active bookings
    Page<Booking> findByDriver_IdAndDeletedFalse(Long driverId, Pageable pageable);
    // Active bookings for customer
    List<Booking> findByCustomer_IdAndDeletedFalse(Long customerId);

    // Active bookings for driver
    List<Booking> findByDriver_IdAndDeletedFalse(Long driverId);
}




