package com.cab.chaloCab.dto;

import com.cab.chaloCab.enums.PaymentMethod;
import com.cab.chaloCab.enums.PaymentStatus;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDTO {
    private Long id;
    private Double amount;
    private PaymentMethod method;
    private PaymentStatus status;
    private LocalDateTime timestamp;
    private Long bookingId;
}
