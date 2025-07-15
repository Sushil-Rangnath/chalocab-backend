package com.cab.chaloCab.service;

import com.cab.chaloCab.dto.PaymentDTO;

import java.util.List;

public interface PaymentService {
    PaymentDTO createPayment(PaymentDTO paymentDTO);
    List<PaymentDTO> getPaymentsByBooking(Long bookingId);
    List<PaymentDTO> getAllPayments();
}
