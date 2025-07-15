package com.cab.chaloCab.dto;

import com.cab.chaloCab.enums.CabType;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FareDTO {
    private Long id;
    private CabType cabType;
    private double ratePerKm;
}
