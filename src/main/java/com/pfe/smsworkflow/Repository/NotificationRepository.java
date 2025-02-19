package com.pfe.smsworkflow.Repository;

import com.pfe.smsworkflow.Models.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification,Long> {
    List<Notification> findByRecipientId(Long recipientId);
    List<Notification> findByRecipientIdAndIsReadFalse(Long recipientId);

}
