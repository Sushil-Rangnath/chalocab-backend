package com.cab.chaloCab.controller;

import com.cab.chaloCab.dto.PaymentDTO;
import com.cab.chaloCab.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    // ================== Customer Endpoints ==================
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/make")
    public ResponseEntity<PaymentDTO> makePayment(@RequestBody PaymentDTO dto) {
        return ResponseEntity.ok(paymentService.createPayment(dto));
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/status/{bookingId}")
    public ResponseEntity<PaymentDTO> getPaymentStatus(@PathVariable Long bookingId) {
        return ResponseEntity.ok(paymentService.getPaymentStatusByBooking(bookingId));
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/history/{customerId}")
    public ResponseEntity<List<PaymentDTO>> getCustomerPayments(@PathVariable Long customerId) {
        return ResponseEntity.ok(paymentService.getPaymentsByCustomer(customerId));
    }

    // ================== Admin Endpoints ==================
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<List<PaymentDTO>> getAllPayments() {
        return ResponseEntity.ok(paymentService.getAllPayments());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<List<PaymentDTO>> getByBooking(@PathVariable Long bookingId) {
        return ResponseEntity.ok(paymentService.getPaymentsByBooking(bookingId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/refund/{paymentId}")
    public ResponseEntity<PaymentDTO> refundPayment(@PathVariable Long paymentId) {
        return ResponseEntity.ok(paymentService.refundPayment(paymentId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/report")
    public ResponseEntity<Map<String, Object>> getPaymentReport() {
        return ResponseEntity.ok(paymentService.generatePaymentReport());
    }

    // ================== Payment Gateway Callback ==================
    @PostMapping("/callback")
    public ResponseEntity<?> paymentCallback(@RequestBody PaymentDTO dto) {
        paymentService.handlePaymentCallback(dto);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/all/paginated")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getPaymentsPaginated(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "ALL") String status) {

        return ResponseEntity.ok(paymentService.getPaymentsPaginated(page, size, status));
    }

}
