package com.cab.chaloCab.service;

import com.cab.chaloCab.dto.DriverRequestDTO;
import com.cab.chaloCab.dto.DriverRequestResponseDTO;
import com.cab.chaloCab.entity.DriverRequest;
import com.cab.chaloCab.enums.DriverRequestStatus;

import java.util.List;

public interface DriverRequestService {

    DriverRequestResponseDTO submitRequest(DriverRequestDTO dto);

    List<DriverRequest> getAllPendingRequests();

    DriverRequestResponseDTO approveRequest(Long requestId);

    DriverRequestResponseDTO rejectRequest(Long requestId);

    List<DriverRequest> getAllRequests();

    DriverRequest getRequestById(Long id);

    DriverRequestResponseDTO updateStatus(Long id, DriverRequestStatus status);
}
