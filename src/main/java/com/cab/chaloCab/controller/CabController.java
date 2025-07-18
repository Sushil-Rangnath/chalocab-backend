package com.cab.chaloCab.controller;

import com.cab.chaloCab.dto.CabDTO;
import com.cab.chaloCab.service.CabService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cabs")
public class CabController {

    @Autowired
    private CabService cabService;

    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CabDTO> addCab(@RequestBody CabDTO cabDTO) {
        return ResponseEntity.ok(cabService.addCab(cabDTO));
    }

    @GetMapping("/all")
    public ResponseEntity<List<CabDTO>> getAllCabs() {
        return ResponseEntity.ok(cabService.getAllCabs());
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CabDTO> updateCab(@PathVariable Long id, @RequestBody CabDTO cabDTO) {
        return ResponseEntity.ok(cabService.updateCab(id, cabDTO));
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteCab(@PathVariable Long id) {
        cabService.deleteCab(id);
        return ResponseEntity.ok("Cab deleted successfully");
    }
}
