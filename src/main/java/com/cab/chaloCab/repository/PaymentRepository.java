package com.cab.chaloCab.repository;

import com.cab.chaloCab.entity.Payment;
import com.cab.chaloCab.enums.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByBookingId(Long bookingId);
    List<Payment> findByCustomerId(Long customerId);
    Page<Payment> findByStatus(PaymentStatus status, Pageable pageable);
    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.status = 'COMPLETED'")
    BigDecimal sumCompletedPayments();

    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.status = 'PENDING'")
    BigDecimal sumPendingPayments();
}
