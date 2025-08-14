package com.cab.chaloCab.dto;

import com.cab.chaloCab.enums.RequestStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OutsideStationRequestDTO {
    private Long id;
    private String sourceLocation;

    private String destinationLocation;
    private String contactName;
    private String contactPhone;
    private String contactEmail;
    private String notes;
    private LocalDateTime requestTime;
    private RequestStatus status;
}
