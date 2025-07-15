package com.cab.chaloCab.repository;

import com.cab.chaloCab.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByCustomerId(Long customerId);
    List<Booking> findByDriverId(Long driverId);
    List<Booking> findByAssignedDriverId(Long driverId);
}
