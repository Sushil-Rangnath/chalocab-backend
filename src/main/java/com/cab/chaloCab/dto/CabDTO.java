package com.cab.chaloCab.dto;

import com.cab.chaloCab.enums.CabType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CabDTO {
    private Long id;
    private String model;
    private String registrationNumber;
    private String color;
    private CabType type;
    private boolean available;
}
