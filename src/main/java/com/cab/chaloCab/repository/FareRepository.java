package com.cab.chaloCab.repository;

import com.cab.chaloCab.entity.Fare;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FareRepository extends JpaRepository<Fare, Long> {
    Optional<Fare> findByCabTypeAndDeletedFalse(String cabType);
}
