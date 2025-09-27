package com.cab.chaloCab.service;

import com.cab.chaloCab.dto.FareDTO;

import java.util.List;

public interface FareService {
    FareDTO setFare(FareDTO dto);
    FareDTO updateFare(String cabType, double baseFare, double perKmFare);
    FareDTO getFareByCabType(String cabType);
    List<FareDTO> getAllFares();
    void deleteFare(String cabType);
    List<FareDTO> getFares(int page, int size, String sortBy, String sortDir);
}
