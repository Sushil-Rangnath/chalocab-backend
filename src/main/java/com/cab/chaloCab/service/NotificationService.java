package com.cab.chaloCab.service;

import com.cab.chaloCab.dto.NotificationDTO;
import com.cab.chaloCab.enums.RecipientType;

import java.util.List;

public interface NotificationService {
    NotificationDTO sendNotification(NotificationDTO dto);
    List<NotificationDTO> getNotifications(Long recipientId, RecipientType type);
    NotificationDTO markAsRead(Long notificationId);
}
