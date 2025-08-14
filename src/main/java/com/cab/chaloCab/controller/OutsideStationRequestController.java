package com.cab.chaloCab.controller;

import com.cab.chaloCab.dto.OutsideStationRequestDTO;
import com.cab.chaloCab.service.OutsideStationRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestParam;




import java.util.List;

@RestController
@RequestMapping("/api/outside-station-requests")
public class OutsideStationRequestController {

    @Autowired
    private OutsideStationRequestService requestService;

    @PostMapping("/create")
    public ResponseEntity<OutsideStationRequestDTO> createRequest(@RequestBody OutsideStationRequestDTO dto) {
        return ResponseEntity.ok(requestService.createRequest(dto));
    }


    @GetMapping("/open")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<OutsideStationRequestDTO>> getOpenRequests(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<OutsideStationRequestDTO> pageResult = requestService.getOpenRequests(page, size);

        return ResponseEntity.ok(pageResult);
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OutsideStationRequestDTO> updateRequestStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        return ResponseEntity.ok(requestService.updateRequestStatus(id, status));
    }
}
