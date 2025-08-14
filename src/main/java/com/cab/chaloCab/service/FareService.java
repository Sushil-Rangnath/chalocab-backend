package com.cab.chaloCab.service;

import com.cab.chaloCab.dto.FareDTO;
import com.cab.chaloCab.enums.CabType;

import java.util.List;

public interface FareService {
    FareDTO setFare(FareDTO dto);
    FareDTO updateFare(CabType cabType, double baseFare, double perKmFare);
    FareDTO getFareByCabType(CabType cabType);
    List<FareDTO> getAllFares();
    void deleteFare(CabType cabType);
    List<FareDTO> getFares(int page, int size, String sortBy, String sortDir);
}
