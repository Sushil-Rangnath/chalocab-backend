package com.cab.chaloCab.repository;

import com.cab.chaloCab.entity.Customer;
import com.cab.chaloCab.entity.Driver;
import com.cab.chaloCab.entity.DriverRequest;
import com.cab.chaloCab.enums.DriverRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface DriverRequestRepository extends JpaRepository<DriverRequest, Long> {
    Optional<DriverRequest> findByPhoneNumberAndStatus(String phoneNumber, DriverRequestStatus status);
    Optional<DriverRequest> findByPhoneNumber(String phoneNumber);
}
