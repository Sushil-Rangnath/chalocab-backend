package com.cab.chaloCab.controller;

import com.cab.chaloCab.dto.DriverRequestDTO;
import com.cab.chaloCab.dto.DriverRequestResponseDTO;
import com.cab.chaloCab.entity.DriverRequest;
import com.cab.chaloCab.service.DriverRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/driver-requests")
public class DriverRequestController {

    @Autowired
    private DriverRequestService driverRequestService;

    /**
     * Submit or update a driver registration request.
     * Returns 201 Created when a new request is created.
     */
    @PostMapping(value = "/submit", consumes = "application/json", produces = "application/json")

    public ResponseEntity<DriverRequestResponseDTO> submitRequest(@RequestBody DriverRequestDTO dto) {
        if (dto == null) {
            return ResponseEntity.badRequest()
                    .body(DriverRequestResponseDTO.builder()
                            .status("failed")
                            .message("Request body cannot be null")
                            .build());
        }

        DriverRequestResponseDTO resp = driverRequestService.submitRequest(dto);
        // If service returns requestId (new resource), return 201, else 200
        if (resp.getRequestId() != null) {
            return ResponseEntity.status(HttpStatus.CREATED).body(resp);
        } else {
            return ResponseEntity.ok(resp);
        }
    }

    /**
     * Admin: list all pending driver requests.
     */
    @GetMapping(value = "/pending", produces = "application/json")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<DriverRequest>> getPendingRequests() {
        List<DriverRequest> pending = driverRequestService.getAllPendingRequests();
        return ResponseEntity.ok(pending);
    }

    /**
     * Admin: approve a pending driver request (creates driver row).
     */
    @PostMapping(value = "/approve/{id}", produces = "application/json")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DriverRequestResponseDTO> approve(@PathVariable Long id) {
        DriverRequestResponseDTO resp = driverRequestService.approveRequest(id);
        return ResponseEntity.ok(resp);
    }

    /**
     * Admin: reject a pending driver request.
     */
    @PostMapping(value = "/reject/{id}", produces = "application/json")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DriverRequestResponseDTO> reject(@PathVariable Long id) {
        DriverRequestResponseDTO resp = driverRequestService.rejectRequest(id);
        return ResponseEntity.ok(resp);
    }

    /**
     * Admin: set an explicit status for a request (PENDING/APPROVED/REJECTED).
     * Example: POST /api/driver-requests/status/123?status=APPROVED
     */
    @PostMapping(value = "/status/{id}", produces = "application/json")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DriverRequestResponseDTO> updateStatus(@PathVariable Long id,
                                                                 @RequestParam String status) {
        // Service will throw ResponseStatusException if status invalid or request not found
        DriverRequestResponseDTO resp = driverRequestService.updateStatus(
                id,
                Enum.valueOf(com.cab.chaloCab.enums.DriverRequestStatus.class, status.toUpperCase())
        );
        return ResponseEntity.ok(resp);
    }
}
