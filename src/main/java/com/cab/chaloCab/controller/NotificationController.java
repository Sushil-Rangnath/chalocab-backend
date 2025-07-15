package com.cab.chaloCab.controller;

import com.cab.chaloCab.dto.NotificationDTO;
import com.cab.chaloCab.enums.RecipientType;
import com.cab.chaloCab.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService service;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/send")
    public ResponseEntity<NotificationDTO> send(@RequestBody NotificationDTO dto) {
        return ResponseEntity.ok(service.sendNotification(dto));
    }

    @GetMapping("/user/{type}/{id}")
    public ResponseEntity<List<NotificationDTO>> getForUser(
            @PathVariable("type") RecipientType type,
            @PathVariable("id") Long id
    ) {
        return ResponseEntity.ok(service.getNotifications(id, type));
    }

    @PutMapping("/mark-read/{id}")
    public ResponseEntity<NotificationDTO> markRead(@PathVariable Long id) {
        return ResponseEntity.ok(service.markAsRead(id));
    }
}
