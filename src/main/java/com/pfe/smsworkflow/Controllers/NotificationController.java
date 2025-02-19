package com.pfe.smsworkflow.Controllers;

import com.pfe.smsworkflow.Models.Notification;
import com.pfe.smsworkflow.Repository.NotificationRepository;
import com.pfe.smsworkflow.Services.IMPL.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private NotificationRepository notificationRepository ;

    @GetMapping("/{userId}")
    public List<Notification> getUserNotifications(@PathVariable Long userId) {
        return notificationService.getNotificationsForUser(userId);
    }
    @GetMapping("/notifications/{userId}")
    public ResponseEntity<List<Notification>> getNotifications(@PathVariable Long userId) {
        List<Notification> notifications = notificationRepository.findByRecipientIdAndIsReadFalse(userId);
        return ResponseEntity.ok(notifications);
    }

}
