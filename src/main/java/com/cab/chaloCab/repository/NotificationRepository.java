package com.cab.chaloCab.repository;

import com.cab.chaloCab.entity.Notification;
import com.cab.chaloCab.enums.RecipientType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByRecipientIdAndRecipientType(Long recipientId, RecipientType type);
}
