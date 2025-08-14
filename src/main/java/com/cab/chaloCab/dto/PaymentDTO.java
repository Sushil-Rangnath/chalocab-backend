package com.cab.chaloCab.dto;

import com.cab.chaloCab.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDTO {
    private Long id;
    private Long bookingId;
    private Long customerId;
    private double amount;
    private PaymentStatus status;
    private String paymentMethod;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
