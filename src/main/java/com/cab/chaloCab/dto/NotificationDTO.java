package com.cab.chaloCab.dto;

import com.cab.chaloCab.enums.RecipientType;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {
    private Long id;
    private String message;
    private Long recipientId;
    private RecipientType recipientType;
    private boolean isRead;
    private LocalDateTime timestamp;
}
