package com.cab.chaloCab.service.impl;

import com.cab.chaloCab.dto.PaymentDTO;
import com.cab.chaloCab.entity.Payment;
import com.cab.chaloCab.enums.PaymentStatus;
import com.cab.chaloCab.repository.PaymentRepository;
import com.cab.chaloCab.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    private PaymentDTO convertToDTO(Payment payment) {
        return new PaymentDTO(
                payment.getId(),
                payment.getBookingId(),
                payment.getCustomerId(),
                payment.getAmount(),
                payment.getStatus(),
                payment.getPaymentMethod(),
                payment.getCreatedAt(),
                payment.getUpdatedAt()
        );
    }

    @Override
    public PaymentDTO createPayment(PaymentDTO dto) {
        Payment payment = new Payment();
        payment.setBookingId(dto.getBookingId());
        payment.setCustomerId(dto.getCustomerId());
        payment.setAmount(dto.getAmount());
        payment.setPaymentMethod(dto.getPaymentMethod());
        payment.setStatus(PaymentStatus.PENDING);
        payment.setCreatedAt(LocalDateTime.now());
        payment.setUpdatedAt(LocalDateTime.now());
        paymentRepository.save(payment);
        return convertToDTO(payment);
    }

    @Override
    public PaymentDTO getPaymentStatusByBooking(Long bookingId) {
        List<Payment> payments = paymentRepository.findByBookingId(bookingId);
        if (payments.isEmpty()) return null;
        // return latest payment status
        Payment latest = payments.get(payments.size() - 1);
        return convertToDTO(latest);
    }

    @Override
    public List<PaymentDTO> getPaymentsByBooking(Long bookingId) {
        return paymentRepository.findByBookingId(bookingId)
                .stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public List<PaymentDTO> getPaymentsByCustomer(Long customerId) {
        return paymentRepository.findByCustomerId(customerId)
                .stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public List<PaymentDTO> getAllPayments() {
        return paymentRepository.findAll()
                .stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public PaymentDTO refundPayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        payment.setStatus(PaymentStatus.REFUNDED);
        payment.setUpdatedAt(LocalDateTime.now());
        paymentRepository.save(payment);
        return convertToDTO(payment);
    }

    @Override
    public Map<String, Object> generatePaymentReport() {
        List<Payment> all = paymentRepository.findAll();
        double totalEarnings = all.stream()
                .filter(p -> p.getStatus() == PaymentStatus.COMPLETED)
                .mapToDouble(Payment::getAmount).sum();
        long totalPayments = all.size();
        long completed = all.stream().filter(p -> p.getStatus() == PaymentStatus.COMPLETED).count();
        long pending = all.stream().filter(p -> p.getStatus() == PaymentStatus.PENDING).count();
        long refunded = all.stream().filter(p -> p.getStatus() == PaymentStatus.REFUNDED).count();

        Map<String, Object> report = new HashMap<>();
        report.put("totalEarnings", totalEarnings);
        report.put("totalPayments", totalPayments);
        report.put("completed", completed);
        report.put("pending", pending);
        report.put("refunded", refunded);
        return report;
    }

    @Override
    public void handlePaymentCallback(PaymentDTO dto) {
        Payment payment = paymentRepository.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        payment.setStatus(dto.getStatus());
        payment.setUpdatedAt(LocalDateTime.now());
        paymentRepository.save(payment);
    }
    @Override
    public Map<String, Object> getPaymentsPaginated(int page, int size, String status) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("id").descending());
        Page<Payment> paymentsPage;

        if ("ALL".equalsIgnoreCase(status)) {
            paymentsPage = paymentRepository.findAll(pageable);
        } else {
            paymentsPage = paymentRepository.findByStatus(PaymentStatus.valueOf(status), pageable);
        }

        List<PaymentDTO> dtos = paymentsPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("payments", dtos);
        response.put("currentPage", page);
        response.put("totalPages", paymentsPage.getTotalPages());
        response.put("totalItems", paymentsPage.getTotalElements());

        return response;
    }

}
