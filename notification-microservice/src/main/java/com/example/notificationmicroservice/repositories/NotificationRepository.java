package com.example.notificationmicroservice.repositories;

import com.example.notificationmicroservice.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findNotificationsByTargetId(String targetId);

    List<Notification> removeNotificationsByTargetId(String targetId);
}
