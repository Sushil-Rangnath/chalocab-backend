package com.cab.chaloCab.entity;

import com.cab.chaloCab.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long bookingId;
    private Long customerId;
    private double amount;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status; // PENDING, COMPLETED, FAILED, REFUNDED

    private String paymentMethod; // CASH, CARD, UPI etc.

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
