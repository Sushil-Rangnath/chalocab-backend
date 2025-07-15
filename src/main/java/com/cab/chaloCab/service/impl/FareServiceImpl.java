package com.cab.chaloCab.service.impl;

import com.cab.chaloCab.dto.FareDTO;
import com.cab.chaloCab.entity.Fare;
import com.cab.chaloCab.enums.CabType;
import com.cab.chaloCab.repository.FareRepository;
import com.cab.chaloCab.service.FareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FareServiceImpl implements FareService {

    @Autowired
    private FareRepository fareRepository;

    @Override
    public FareDTO setFare(FareDTO fareDTO) {
        if (fareRepository.findByCabType(fareDTO.getCabType()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Fare for cab type already exists");
        }

        Fare fare = Fare.builder()
                .cabType(fareDTO.getCabType())
                .ratePerKm(fareDTO.getRatePerKm())
                .build();

        return mapToDTO(fareRepository.save(fare));
    }

    @Override
    public FareDTO updateFare(CabType cabType, double newRate) {
        Fare fare = fareRepository.findByCabType(cabType)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Fare not found"));

        fare.setRatePerKm(newRate);
        return mapToDTO(fareRepository.save(fare));
    }

    @Override
    public FareDTO getFareByCabType(CabType cabType) {
        Fare fare = fareRepository.findByCabType(cabType)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Fare not found"));
        return mapToDTO(fare);
    }

    @Override
    public List<FareDTO> getAllFares() {
        return fareRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private FareDTO mapToDTO(Fare fare) {
        return FareDTO.builder()
                .id(fare.getId())
                .cabType(fare.getCabType())
                .ratePerKm(fare.getRatePerKm())
                .build();
    }
}
