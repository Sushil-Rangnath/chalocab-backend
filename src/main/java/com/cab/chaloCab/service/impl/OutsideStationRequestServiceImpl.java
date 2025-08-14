package com.cab.chaloCab.service.impl;

import com.cab.chaloCab.dto.OutsideStationRequestDTO;
import com.cab.chaloCab.entity.OutsideStationRequest;
import com.cab.chaloCab.enums.RequestStatus;
import com.cab.chaloCab.repository.OutsideStationRequestRepository;
import com.cab.chaloCab.service.OutsideStationRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;


import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OutsideStationRequestServiceImpl implements OutsideStationRequestService {

    @Autowired
    private OutsideStationRequestRepository repository;

    @Override
    public OutsideStationRequestDTO createRequest(OutsideStationRequestDTO dto) {
        OutsideStationRequest request = OutsideStationRequest.builder()
                .sourceLocation(dto.getSourceLocation())
                .destinationLocation(dto.getDestinationLocation())
                .contactName(dto.getContactName())
                .contactPhone(dto.getContactPhone())
                .contactEmail(dto.getContactEmail())
                .notes(dto.getNotes())
                .requestTime(LocalDateTime.now())
                .status(RequestStatus.OPEN)
                .build();

        OutsideStationRequest saved = repository.save(request);
        return convertToDTO(saved);
    }

 /*   @Override
    public List<OutsideStationRequestDTO> getOpenRequests() {
        return repository.findByStatusOrderByRequestTimeDesc(RequestStatus.OPEN)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }*/

    @Override
    public OutsideStationRequestDTO updateRequestStatus(Long id, String statusStr) {
        OutsideStationRequest request = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Request not found"));

        RequestStatus status;
        try {
            status = RequestStatus.valueOf(statusStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid status value");
        }

        request.setStatus(status);
        OutsideStationRequest updated = repository.save(request);
        return convertToDTO(updated);
    }

    private OutsideStationRequestDTO convertToDTO(OutsideStationRequest request) {
        return OutsideStationRequestDTO.builder()
                .id(request.getId())
                .sourceLocation(request.getSourceLocation())
                .destinationLocation(request.getDestinationLocation())
                .contactName(request.getContactName())
                .contactPhone(request.getContactPhone())
                .contactEmail(request.getContactEmail())
                .notes(request.getNotes())
                .requestTime(request.getRequestTime())
                .status(request.getStatus())
                .build();
    }
    @Override
    public Page<OutsideStationRequestDTO> getOpenRequests(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("requestTime").descending());
        Page<OutsideStationRequest> pageData = repository.findByStatus(RequestStatus.OPEN, pageable);

        return pageData.map(this::convertToDTO);
    }
}
