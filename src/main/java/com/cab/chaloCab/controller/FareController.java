package com.cab.chaloCab.controller;

import com.cab.chaloCab.dto.FareDTO;
import com.cab.chaloCab.enums.CabType;
import com.cab.chaloCab.service.FareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fares")
public class FareController {

    @Autowired
    private FareService fareService;

    @PostMapping("/set")
    public ResponseEntity<FareDTO> setFare(@RequestBody FareDTO dto) {
        return ResponseEntity.ok(fareService.setFare(dto));
    }

    @PutMapping("/update/{cabType}")
    public ResponseEntity<FareDTO> updateFare(@PathVariable CabType cabType, @RequestParam double rate) {
        return ResponseEntity.ok(fareService.updateFare(cabType, rate));
    }

    @GetMapping("/type/{cabType}")
    public ResponseEntity<FareDTO> getFare(@PathVariable CabType cabType) {
        return ResponseEntity.ok(fareService.getFareByCabType(cabType));
    }

    @GetMapping("/all")
    public ResponseEntity<List<FareDTO>> getAllFares() {
        return ResponseEntity.ok(fareService.getAllFares());
    }
}
