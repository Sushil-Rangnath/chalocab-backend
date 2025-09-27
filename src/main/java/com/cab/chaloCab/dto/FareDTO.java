package com.cab.chaloCab.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FareDTO {
    private Long id;
    // changed from enum to String to allow dynamic cab types
    private String cabType;
    private double baseFare;
    private double perKmFare;
}
