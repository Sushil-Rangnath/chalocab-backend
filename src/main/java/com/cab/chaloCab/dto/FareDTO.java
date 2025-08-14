package com.cab.chaloCab.dto;

import com.cab.chaloCab.enums.CabType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FareDTO {
    private Long id;
    private CabType cabType;
    private double baseFare;
    private double perKmFare;
}
