package com.pfe.smsworkflow.Models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
@Entity
public class Notification extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String message;
    private boolean isRead;
    private String Title ;
    @Enumerated(EnumType.STRING)
    private NotificationLevel level;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    @ManyToOne
    private User recipient; // Destinataire de la notification

    private LocalDateTime createdAt = LocalDateTime.now();
}
