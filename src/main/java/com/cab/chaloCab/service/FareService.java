package com.cab.chaloCab.service;

import com.cab.chaloCab.dto.FareDTO;
import com.cab.chaloCab.enums.CabType;

import java.util.List;

public interface FareService {
    FareDTO setFare(FareDTO fareDTO);
    FareDTO updateFare(CabType cabType, double newRate);
    FareDTO getFareByCabType(CabType cabType);
    List<FareDTO> getAllFares();
}
