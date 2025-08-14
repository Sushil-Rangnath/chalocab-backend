package com.cab.chaloCab.service;

import com.cab.chaloCab.dto.PaymentDTO;
import com.cab.chaloCab.enums.PaymentStatus;

import java.util.List;
import java.util.Map;

public interface PaymentService {
    PaymentDTO createPayment(PaymentDTO dto);
    PaymentDTO getPaymentStatusByBooking(Long bookingId);
    List<PaymentDTO> getPaymentsByBooking(Long bookingId);
    List<PaymentDTO> getPaymentsByCustomer(Long customerId);
    List<PaymentDTO> getAllPayments();
    PaymentDTO refundPayment(Long paymentId);
    Map<String, Object> generatePaymentReport();
    void handlePaymentCallback(PaymentDTO dto);
    Map<String, Object> getPaymentsPaginated(int page, int size, String status);
}
