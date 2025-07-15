package com.cab.chaloCab.repository;

import com.cab.chaloCab.entity.Fare;
import com.cab.chaloCab.enums.CabType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FareRepository extends JpaRepository<Fare, Long> {
    Optional<Fare> findByCabType(CabType cabType);
}
