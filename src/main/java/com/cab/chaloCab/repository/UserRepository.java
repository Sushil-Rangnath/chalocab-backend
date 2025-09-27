package com.cab.chaloCab.repository;

import com.cab.chaloCab.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByPhoneNumber(String phoneNumber); // ✅ For OTP login

    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);
}
