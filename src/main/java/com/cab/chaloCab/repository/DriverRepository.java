package com.cab.chaloCab.repository;

import com.cab.chaloCab.entity.Driver;
import com.cab.chaloCab.enums.DriverStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface DriverRepository extends JpaRepository<Driver, Long> {

    Page<Driver> findByStatus(DriverStatus status, Pageable pageable);
    Optional<Driver> findByPhoneNumber(String phone);

    @Query("SELECT d FROM Driver d " +
            "WHERE d.status = :status AND " +
            "(LOWER(d.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(d.email) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Driver> searchByStatusAndKeyword(DriverStatus status, String keyword, Pageable pageable);
}
