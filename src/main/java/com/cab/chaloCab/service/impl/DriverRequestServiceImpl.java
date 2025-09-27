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
        // Build entity from DTO
        DriverRequest request = DriverRequest.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .phoneNumber(dto.getPhone())
                .licenseNumber(dto.getLicenseNumber())
                .vehicleNumber(dto.getVehicleNumber())
                .vehicleType(dto.getVehicleType())
                .address(dto.getAddress())
                .status(DriverRequestStatus.PENDING)
                .build();

        DriverRequest savedRequest = requestRepo.save(request);

        return DriverRequestResponseDTO.builder()
                .status("success")
                .message("Driver request submitted successfully")
                .requestId(savedRequest.getId())
                .name(savedRequest.getName())
                .phone(savedRequest.getPhoneNumber())
                .licenseNumber(savedRequest.getLicenseNumber())
                .vehicleRegistration(savedRequest.getVehicleNumber())
                .vehicleType(savedRequest.getVehicleType())
                .address(savedRequest.getAddress())
                .requestStatus(savedRequest.getStatus() != null ? savedRequest.getStatus().name() : DriverRequestStatus.PENDING.name())
                .build();
    }

    @Override
    public DriverRequestResponseDTO approveRequest(Long id) {
        DriverRequest request = getRequestById(id);

        if (request.getStatus() != DriverRequestStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request already processed");
        }

        request.setStatus(DriverRequestStatus.APPROVED);
        DriverRequest updated = requestRepo.save(request);

        // Create Driver entity from request. Adjust fields as per your Driver entity.
        Driver driver = Driver.builder()
                .name(updated.getName())
                .email(updated.getEmail())
                .phoneNumber(updated.getPhoneNumber())
                .licenseNumber(updated.getLicenseNumber())
                .vehicleNumber(updated.getVehicleNumber()) // map to your Driver field name
                .status(DriverStatus.APPROVED)
                .build();

        Driver savedDriver = driverRepo.save(driver);

        return DriverRequestResponseDTO.builder()
                .status("success")
                .message("Driver request approved and driver created")
                .requestId(updated.getId())
                .name(updated.getName())
                .phone(updated.getPhoneNumber())
                .licenseNumber(updated.getLicenseNumber())
                .vehicleRegistration(updated.getVehicleNumber())
                .vehicleType(updated.getVehicleType())
                .address(updated.getAddress())
                .requestStatus(updated.getStatus() != null ? updated.getStatus().name() : DriverRequestStatus.APPROVED.name())
                .build();
    }

    @Override
    public DriverRequestResponseDTO rejectRequest(Long id) {
        DriverRequest request = getRequestById(id);

        if (request.getStatus() != DriverRequestStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request already processed");
        }

        request.setStatus(DriverRequestStatus.REJECTED);
        DriverRequest updated = requestRepo.save(request);

        return DriverRequestResponseDTO.builder()
                .status("success")
                .message("Driver request rejected successfully")
                .requestId(updated.getId())
                .name(updated.getName())
                .phone(updated.getPhoneNumber())
                .licenseNumber(updated.getLicenseNumber())
                .vehicleRegistration(updated.getVehicleNumber())
                .vehicleType(updated.getVehicleType())
                .address(updated.getAddress())
                .requestStatus(updated.getStatus() != null ? updated.getStatus().name() : DriverRequestStatus.REJECTED.name())
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
        DriverRequest updated = requestRepo.save(request);

        return DriverRequestResponseDTO.builder()
                .status("success")
                .message("Driver request " + status.name().toLowerCase())
                .requestId(updated.getId())
                .name(updated.getName())
                .phone(updated.getPhoneNumber())
                .licenseNumber(updated.getLicenseNumber())
                .vehicleRegistration(updated.getVehicleNumber())
                .vehicleType(updated.getVehicleType())
                .address(updated.getAddress())
                .requestStatus(updated.getStatus() != null ? updated.getStatus().name() : status.name())
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
