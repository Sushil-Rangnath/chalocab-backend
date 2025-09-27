package com.cab.chaloCab.repository;

import com.cab.chaloCab.entity.Customer;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FlutterCustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByPhone(String phone);
}
