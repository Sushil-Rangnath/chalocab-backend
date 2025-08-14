package com.cab.chaloCab.repository;

import com.cab.chaloCab.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByEmailAndDeletedFalse(String email);

    boolean existsByEmailAndDeletedFalse(String email);

    boolean existsByPhoneAndDeletedFalse(String phone);

    Optional<Customer> findByPhoneAndDeletedFalse(String phone);

    Page<Customer> findByDeletedFalse(Pageable pageable);

    Optional<Customer> findByIdAndDeletedFalse(Long id);

    // Search by name, email, or phone
    @Query("SELECT c FROM Customer c WHERE c.deleted = false AND " +
            "(LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(c.email) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR c.phone LIKE %:keyword%)")
    Page<Customer> searchCustomers(@Param("keyword") String keyword, Pageable pageable);

    // Soft delete query
    @Modifying
    @Transactional
    @Query("UPDATE Customer c SET c.deleted = true WHERE c.id = :id")
    void softDeleteCustomer(@Param("id") Long id);
}
