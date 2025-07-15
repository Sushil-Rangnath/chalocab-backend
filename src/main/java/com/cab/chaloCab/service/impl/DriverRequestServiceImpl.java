package com.cab.chaloCab.service.impl;

import com.cab.chaloCab.dto.DriverRequestDTO;
import com.cab.chaloCab.entity.DriverRequest;
import com.cab.chaloCab.enums.DriverRequestStatus;
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


    @Override
    public DriverRequest submitRequest(DriverRequestDTO dto) {
        DriverRequest request = DriverRequest.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .licenseNumber(dto.getLicenseNumber())
                .vehicleNumber(dto.getVehicleNumber())
                .address(dto.getAddress())
                .status(DriverRequestStatus.PENDING)
                .build();
        return requestRepo.save(request);
    }
    @Override
    public String approveRequest(Long id) {
        DriverRequest request = getRequestById(id);

        if (request.getStatus() != DriverRequestStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request already processed");
        }

        request.setStatus(DriverRequestStatus.APPROVED);
        requestRepo.save(request);

        return "Driver request approved successfully";
    }
    @Override
    public String rejectRequest(Long id) {
        DriverRequest request = getRequestById(id);

        if (request.getStatus() != DriverRequestStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request already processed");
        }

        request.setStatus(DriverRequestStatus.REJECTED);
        requestRepo.save(request);

        return "Driver request rejected successfully";
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
    public String updateStatus(Long id, DriverRequestStatus status) {
        DriverRequest request = getRequestById(id);
        request.setStatus(status);
        requestRepo.save(request);
        return "Driver request " + status.name().toLowerCase();
    }

    @Override
    public List<DriverRequest> getAllPendingRequests() {
        return requestRepo.findAll()
                .stream()
                .filter(req -> req.getStatus() == DriverRequestStatus.PENDING)
                .collect(Collectors.toList());
    }
}
