package com.cab.chaloCab.service.impl;

import com.cab.chaloCab.dto.DriverRequestDTO;
import com.cab.chaloCab.dto.DriverRequestResponseDTO;
import com.cab.chaloCab.entity.DriverRequest;
import com.cab.chaloCab.entity.Driver;
import com.cab.chaloCab.enums.DriverRequestStatus;
import com.cab.chaloCab.enums.DriverStatus;
import com.cab.chaloCab.repository.DriverRepository;
import com.cab.chaloCab.repository.DriverRequestRepository;
import com.cab.chaloCab.service.DriverRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DriverRequestServiceImpl implements DriverRequestService {

    @Autowired
    private DriverRequestRepository requestRepo;

    @Autowired
    private DriverRepository driverRepo;

    @Override
    public DriverRequestResponseDTO submitRequest(DriverRequestDTO dto) {
        DriverRequest request = DriverRequest.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .licenseNumber(dto.getLicenseNumber())
                .vehicleNumber(dto.getVehicleNumber())
                .address(dto.getAddress())
                .status(DriverRequestStatus.PENDING)
                .build();

        DriverRequest savedRequest = requestRepo.save(request);

        return DriverRequestResponseDTO.builder()
                .status("success")
                .message("Driver request submitted successfully")
                .driverRequest(savedRequest)
                .build();
    }

    @Override
    public DriverRequestResponseDTO approveRequest(Long id) {
        DriverRequest request = getRequestById(id);

        if (request.getStatus() != DriverRequestStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request already processed");
        }

        request.setStatus(DriverRequestStatus.APPROVED);
        requestRepo.save(request);

        Driver driver = Driver.builder()
                .name(request.getName())
                .email(request.getEmail())
                .phoneNumber(request.getPhone())
                .licenseNumber(request.getLicenseNumber())
                .vehicleNumber(request.getVehicleNumber())
                .status(DriverStatus.APPROVED)
                .build();

        driverRepo.save(driver);

        return DriverRequestResponseDTO.builder()
                .status("success")
                .message("Driver request approved and driver created")
                .driverRequest(request)
                .driver(driver)
                .build();
    }

    @Override
    public DriverRequestResponseDTO rejectRequest(Long id) {
        DriverRequest request = getRequestById(id);

        if (request.getStatus() != DriverRequestStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request already processed");
        }

        request.setStatus(DriverRequestStatus.REJECTED);
        requestRepo.save(request);

        return DriverRequestResponseDTO.builder()
                .status("success")
                .message("Driver request rejected successfully")
                .driverRequest(request)
                .build();
    }

    @Override
    public List<DriverRequest> getAllRequests() {
        return requestRepo.findAll();
    }

    @Override
    public DriverRequest getRequestById(Long id) {
        return requestRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Request not found"));
    }

    @Override
    public DriverRequestResponseDTO updateStatus(Long id, DriverRequestStatus status) {
        DriverRequest request = getRequestById(id);
        request.setStatus(status);
        requestRepo.save(request);

        return DriverRequestResponseDTO.builder()
                .status("success")
                .message("Driver request " + status.name().toLowerCase())
                .driverRequest(request)
                .build();
    }

    @Override
    public List<DriverRequest> getAllPendingRequests() {
        return requestRepo.findAll()
                .stream()
                .filter(req -> req.getStatus() == DriverRequestStatus.PENDING)
                .collect(Collectors.toList());
    }
}
