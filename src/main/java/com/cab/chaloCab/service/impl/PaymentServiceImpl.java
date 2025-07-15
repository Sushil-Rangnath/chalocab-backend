package com.cab.chaloCab.service.impl;

import com.cab.chaloCab.dto.PaymentDTO;
import com.cab.chaloCab.entity.Payment;
import com.cab.chaloCab.enums.PaymentStatus;
import com.cab.chaloCab.repository.PaymentRepository;
import com.cab.chaloCab.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentRepository paymentRepo;

    @Override
    public PaymentDTO createPayment(PaymentDTO dto) {
        Payment payment = Payment.builder()
                .amount(dto.getAmount())
                .method(dto.getMethod())
                .status(PaymentStatus.PAID) // Assume payment success
                .timestamp(LocalDateTime.now())
                .bookingId(dto.getBookingId())
                .build();

        return convertToDTO(paymentRepo.save(payment));
    }

    @Override
    public List<PaymentDTO> getPaymentsByBooking(Long bookingId) {
        return paymentRepo.findByBookingId(bookingId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PaymentDTO> getAllPayments() {
        return paymentRepo.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private PaymentDTO convertToDTO(Payment p) {
        return PaymentDTO.builder()
                .id(p.getId())
                .amount(p.getAmount())
                .method(p.getMethod())
                .status(p.getStatus())
                .timestamp(p.getTimestamp())
                .bookingId(p.getBookingId())
                .build();
    }
}
