package com.cab.chaloCab.service.impl;

import com.cab.chaloCab.dto.FareDTO;
import com.cab.chaloCab.entity.Fare;
import com.cab.chaloCab.repository.FareRepository;
import com.cab.chaloCab.service.FareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FareServiceImpl implements FareService {

    @Autowired
    private FareRepository fareRepository;

    private FareDTO mapToDTO(Fare fare) {
        return new FareDTO(fare.getId(), fare.getCabType(), fare.getBaseFare(), fare.getPerKmFare());
    }

    @Override
    public FareDTO setFare(FareDTO dto) {
        if (fareRepository.findByCabTypeAndDeletedFalse(dto.getCabType()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Fare already exists for this cab type");
        }
        Fare fare = new Fare();
        fare.setCabType(dto.getCabType());
        fare.setBaseFare(dto.getBaseFare());
        fare.setPerKmFare(dto.getPerKmFare());
        fare.setCreatedAt(LocalDateTime.now());
        fare.setUpdatedAt(LocalDateTime.now());
        fare = fareRepository.save(fare);
        return mapToDTO(fare);
    }

    @Override
    public FareDTO updateFare(String cabType, double baseFare, double perKmFare) {
        Fare fare = fareRepository.findByCabTypeAndDeletedFalse(cabType)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Fare not found"));
        fare.setBaseFare(baseFare);
        fare.setPerKmFare(perKmFare);
        fare.setUpdatedAt(LocalDateTime.now());
        fare = fareRepository.save(fare);
        return mapToDTO(fare);
    }

    @Override
    public FareDTO getFareByCabType(String cabType) {
        Fare fare = fareRepository.findByCabTypeAndDeletedFalse(cabType)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Fare not found"));
        return mapToDTO(fare);
    }

    @Override
    public List<FareDTO> getAllFares() {
        return fareRepository.findAll().stream()
                .filter(f -> !f.isDeleted())
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteFare(String cabType) {
        Fare fare = fareRepository.findByCabTypeAndDeletedFalse(cabType)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Fare not found"));
        fare.setDeleted(true);
        fare.setUpdatedAt(LocalDateTime.now());
        fareRepository.save(fare);
    }

    @Override
    public List<FareDTO> getFares(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Fare> farePage = fareRepository.findAll(pageable);
        return farePage.stream()
                .filter(f -> !f.isDeleted())
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
}
