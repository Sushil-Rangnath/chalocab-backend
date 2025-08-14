// File: com.cab.chaloCab.service.DriverService.java
package com.cab.chaloCab.service;

import com.cab.chaloCab.dto.DriverDTO;
import com.cab.chaloCab.dto.DriverRequestDTO;
import com.cab.chaloCab.entity.Driver;
import com.cab.chaloCab.enums.DriverStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface DriverService {

    void submitDriverRequest(DriverRequestDTO requestDTO);

    List<DriverDTO> getAllDriverRequests();

    void approveDriverRequest(Long requestId);

    void rejectDriverRequest(Long requestId);

    List<DriverDTO> getAllApprovedDrivers();

    Page<Driver> getApprovedDrivers(int page, int size, String search);

    Page<Driver> getDriversByStatus(DriverStatus status, Pageable pageable);

    Page<Driver> searchDriversByStatus(DriverStatus status, String keyword, Pageable pageable);
}
