package com.cab.chaloCab.service.impl;

import com.cab.chaloCab.dto.NotificationDTO;
import com.cab.chaloCab.entity.Notification;
import com.cab.chaloCab.enums.RecipientType;
import com.cab.chaloCab.repository.NotificationRepository;
import com.cab.chaloCab.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationRepository repo;

    @Override
    public NotificationDTO sendNotification(NotificationDTO dto) {
        Notification notification = Notification.builder()
                .message(dto.getMessage())
                .recipientId(dto.getRecipientId())
                .recipientType(dto.getRecipientType())
                .isRead(false)
                .timestamp(LocalDateTime.now())
                .build();
        return convertToDTO(repo.save(notification));
    }

    @Override
    public List<NotificationDTO> getNotifications(Long recipientId, RecipientType type) {
        return repo.findByRecipientIdAndRecipientType(recipientId, type).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public NotificationDTO markAsRead(Long notificationId) {
        Notification notification = repo.findById(notificationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Notification not found"));
        notification.setRead(true);
        return convertToDTO(repo.save(notification));
    }

    private NotificationDTO convertToDTO(Notification n) {
        return NotificationDTO.builder()
                .id(n.getId())
                .message(n.getMessage())
                .recipientId(n.getRecipientId())
                .recipientType(n.getRecipientType())
                .isRead(n.isRead())
                .timestamp(n.getTimestamp())
                .build();
    }
}
