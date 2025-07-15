package com.cab.chaloCab.service;

import com.cab.chaloCab.dto.DriverDTO;
import com.cab.chaloCab.dto.DriverRequestDTO;

import java.util.List;

public interface DriverService {
    void submitDriverRequest(DriverRequestDTO requestDTO);
    List<DriverDTO> getAllDriverRequests();
    void approveDriverRequest(Long requestId);
    void rejectDriverRequest(Long requestId);
    List<DriverDTO> getAllApprovedDrivers();
}
