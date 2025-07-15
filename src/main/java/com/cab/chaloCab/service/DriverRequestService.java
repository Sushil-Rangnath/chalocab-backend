package com.cab.chaloCab.service;

import com.cab.chaloCab.dto.DriverRequestDTO;
import com.cab.chaloCab.entity.DriverRequest;
import com.cab.chaloCab.enums.DriverRequestStatus;

import java.util.List;

public interface DriverRequestService {
    DriverRequest submitRequest(DriverRequestDTO dto);
    List<DriverRequest> getAllPendingRequests();
    String approveRequest(Long requestId);
    String rejectRequest(Long requestId);
    List<DriverRequest> getAllRequests();
    DriverRequest getRequestById(Long id);
    String updateStatus(Long id, DriverRequestStatus status);
}
