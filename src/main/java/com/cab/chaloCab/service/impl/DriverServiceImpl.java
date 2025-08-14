package com.cab.chaloCab.service.impl;

import com.cab.chaloCab.dto.DriverDTO;
import com.cab.chaloCab.dto.DriverRequestDTO;
import com.cab.chaloCab.entity.Driver;
import com.cab.chaloCab.entity.DriverRequest;
import com.cab.chaloCab.enums.DriverRequestStatus;
import com.cab.chaloCab.enums.DriverStatus;
import com.cab.chaloCab.repository.DriverRepository;
import com.cab.chaloCab.repository.DriverRequestRepository;
import com.cab.chaloCab.service.DriverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DriverServiceImpl implements DriverService {

    @Autowired
    private DriverRequestRepository requestRepo;

    @Autowired
    private DriverRepository driverRepo;

    @Override
    public void submitDriverRequest(DriverRequestDTO dto) {
        DriverRequest request = DriverRequest.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .licenseNumber(dto.getLicenseNumber())
                .vehicleNumber(dto.getVehicleNumber())
                .address(dto.getAddress())
                .status(DriverRequestStatus.PENDING)
                .build();
        requestRepo.save(request);
    }
    @Override
    public Page<Driver> getDriversByStatus(DriverStatus status, Pageable pageable) {
        return driverRepo.findByStatus(status, pageable);
    }
    @Override
    public List<DriverDTO> getAllDriverRequests() {
        return requestRepo.findAll().stream()
                .map(r -> DriverDTO.builder()
                        .id(r.getId())
                        .name(r.getName())
                        .email(r.getEmail())
                        .phone(r.getPhone())
                        .licenseNumber(r.getLicenseNumber())
                        .vehicleNumber(r.getVehicleNumber())
                        .status(r.getStatus().name())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public void approveDriverRequest(Long requestId) {
        DriverRequest request = requestRepo.findById(requestId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Driver request not found"));

        if (request.getStatus() != DriverRequestStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request already processed");
        }

        Driver driver = Driver.builder()
                .name(request.getName())
                .email(request.getEmail())
                .phoneNumber(request.getPhone())
                .licenseNumber(request.getLicenseNumber())
                .vehicleNumber(request.getVehicleNumber())
                .status(DriverStatus.APPROVED) // ✅ Now storing as APPROVED
                .build();
        driverRepo.save(driver);

        request.setStatus(DriverRequestStatus.APPROVED);
        requestRepo.save(request);
    }

    @Override
    public void rejectDriverRequest(Long requestId) {
        DriverRequest request = requestRepo.findById(requestId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Driver request not found"));

        if (request.getStatus() != DriverRequestStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request already processed");
        }

        request.setStatus(DriverRequestStatus.REJECTED);
        requestRepo.save(request);
    }

    @Override
    public List<DriverDTO> getAllApprovedDrivers() {
        return driverRepo.findByStatus(DriverStatus.APPROVED, Pageable.unpaged()).stream()
                .map(driver -> DriverDTO.builder()
                        .id(driver.getId())
                        .name(driver.getName())
                        .email(driver.getEmail())
                        .phone(driver.getPhoneNumber())
                        .licenseNumber(driver.getLicenseNumber())
                        .vehicleNumber(driver.getVehicleNumber())
                        .status(driver.getStatus().name())
                        .build())
                .collect(Collectors.toList());
    }


    @Override
    public Page<Driver> getApprovedDrivers(int page, int size, String search) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());

        if (search != null && !search.trim().isEmpty()) {
            return driverRepo.searchByStatusAndKeyword(DriverStatus.APPROVED, search.trim(), pageable);
        } else {
            return driverRepo.findByStatus(DriverStatus.APPROVED, pageable);
        }
    }

    @Override
    public Page<Driver> searchDriversByStatus(DriverStatus status, String keyword, Pageable pageable) {
        return driverRepo.searchByStatusAndKeyword(status, keyword.trim(), pageable);
    }
}
