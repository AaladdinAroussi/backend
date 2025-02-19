package com.pfe.smsworkflow.Services.IMPL;

import com.pfe.smsworkflow.Models.Notification;
import com.pfe.smsworkflow.Models.NotificationType;
import com.pfe.smsworkflow.Models.User;
import com.pfe.smsworkflow.Repository.NotificationRepository;
import com.pfe.smsworkflow.Repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {
    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UsersRepository userRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public void createNotification(Long recipientId, String message, NotificationType type) {
        User recipient = userRepository.findById(recipientId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        Notification notification = new Notification();
        notification.setRecipient(recipient);
        notification.setMessage(message);
        notification.setType(type);
        notification.setRead(false);

        notificationRepository.save(notification);

        // Envoi en temps réel via WebSockets
        messagingTemplate.convertAndSend("/topic/notifications/" + recipientId, notification);
    }

    public List<Notification> getNotificationsForUser(Long userId) {
        return notificationRepository.findByRecipientId(userId);
    }
}
