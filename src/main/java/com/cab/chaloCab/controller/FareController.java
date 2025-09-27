package com.cab.chaloCab.controller;

import com.cab.chaloCab.dto.FareDTO;
import com.cab.chaloCab.service.FareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/flutter/fares")
public class FareController {

    @Autowired
    private FareService fareService;

    /**
     * Add a new fare
     */
    @PostMapping("/set")
    public ResponseEntity<FareDTO> setFare(@RequestBody FareDTO dto) {
        return ResponseEntity.ok(fareService.setFare(dto));
    }

    /**
     * Update an existing fare (base fare and per km fare)
     */
    @PutMapping("/update/{cabType}")
    public ResponseEntity<FareDTO> updateFare(
            @PathVariable String cabType,
            @RequestBody FareDTO dto) {
        return ResponseEntity.ok(fareService.updateFare(cabType, dto.getBaseFare(), dto.getPerKmFare()));
    }

    /**
     * Get fare by cab type
     */
    @GetMapping("/type/{cabType}")
    public ResponseEntity<FareDTO> getFare(@PathVariable String cabType) {
        return ResponseEntity.ok(fareService.getFareByCabType(cabType));
    }

    /**
     * Get all fares (full list, no pagination)
     */
    @GetMapping("/all")
    public ResponseEntity<List<FareDTO>> getAllFares() {
        return ResponseEntity.ok(fareService.getAllFares());
    }

    /**
     * Delete fare by cab type
     */
    @DeleteMapping("/{cabType}")
    public ResponseEntity<Void> deleteFare(@PathVariable String cabType) {
        fareService.deleteFare(cabType);
        return ResponseEntity.ok().build();
    }

    /**
     * Get fares with pagination and sorting
     */
    @GetMapping
    public ResponseEntity<List<FareDTO>> getFares(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "cabType") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        List<FareDTO> fares = fareService.getFares(page, size, sortBy, sortDir);
        return ResponseEntity.ok(fares);
    }
}
