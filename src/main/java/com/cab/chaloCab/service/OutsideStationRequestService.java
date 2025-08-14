package com.cab.chaloCab.service;

import com.cab.chaloCab.dto.OutsideStationRequestDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OutsideStationRequestService {
    OutsideStationRequestDTO createRequest(OutsideStationRequestDTO dto);
   // List<OutsideStationRequestDTO> getOpenRequests();
    OutsideStationRequestDTO updateRequestStatus(Long id, String status);
    Page<OutsideStationRequestDTO> getOpenRequests(int page, int size);
}
