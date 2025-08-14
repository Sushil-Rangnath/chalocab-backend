package com.cab.chaloCab.repository;

import com.cab.chaloCab.entity.OutsideStationRequest;
import com.cab.chaloCab.enums.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;


import java.util.List;

public interface OutsideStationRequestRepository extends JpaRepository<OutsideStationRequest, Long> {
   // List<OutsideStationRequest> findByStatusOrderByRequestTimeDesc(RequestStatus status);
    Page<OutsideStationRequest> findByStatusOrderByRequestTimeDesc(RequestStatus status, Pageable pageable);
    Page<OutsideStationRequest> findByStatus(RequestStatus status, Pageable pageable);
}
