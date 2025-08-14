package com.cab.chaloCab.repository;

import com.cab.chaloCab.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    // Existing methods
    List<Booking> findByCustomerId(Long customerId);
    List<Booking> findByDriverId(Long driverId);
    List<Booking> findByAssignedDriverId(Long driverId);

    @Query("SELECT SUM(b.fare) FROM Booking b WHERE b.status = 'COMPLETED'")
    Double calculateTotalRevenue();

    // Soft delete aware methods
    Optional<Booking> findByIdAndDeletedFalse(Long id);

    List<Booking> findAllByDeletedFalse();
}
