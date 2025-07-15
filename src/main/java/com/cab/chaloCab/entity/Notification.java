package com.cab.chaloCab.entity;

import com.cab.chaloCab.enums.RecipientType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String message;

    private Long recipientId;

    @Enumerated(EnumType.STRING)
    private RecipientType recipientType;

    @Column(name = "is_read") // optional but explicit
    private boolean isRead;

    private LocalDateTime timestamp;
}
