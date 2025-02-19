package com.pfe.smsworkflow.Controllers;

import com.pfe.smsworkflow.Models.Notification;
import com.pfe.smsworkflow.Services.IMPL.NotificationService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketNotificationController {
    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationService notificationService;

    public WebSocketNotificationController(SimpMessagingTemplate messagingTemplate, NotificationService notificationService) {
        this.messagingTemplate = messagingTemplate;
        this.notificationService = notificationService;
    }

    /**
     * Méthode qui écoute les messages WebSocket envoyés à /app/sendNotification
     * et les envoie aux abonnés du topic correspondant.
     */
    @MessageMapping("/sendNotification")
    @SendTo("/topic/notifications")
    public Notification broadcastNotification(Notification notification) {
        // Sauvegarde la notification en base de données
        notificationService.createNotification(
                notification.getRecipient().getId(),
                notification.getMessage(),
                notification.getType()
        );

        // Retourne la notification pour l'envoyer en temps réel aux abonnés
        return notification;
    }
}
