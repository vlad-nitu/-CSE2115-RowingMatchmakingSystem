package com.example.notificationmicroservice.services;

import com.example.notificationmicroservice.domain.Notification;
import com.example.notificationmicroservice.repositories.NotificationRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class NotificationDatabaseService {

    private final NotificationRepository notificationRepository;

    public List<Notification> findAll() {
        return notificationRepository.findAll();
    }

    public List<Notification> findNotificationsByTargetId(String targetId) {
        return notificationRepository.findNotificationsByTargetId(targetId);
    }

    public List<Notification> removeNotificationsByTargetId(String targetId) {
        return notificationRepository.removeNotificationsByTargetId(targetId);
    }

    public Notification save(Notification notification) {
        return notificationRepository.save(notification);
    }
}
