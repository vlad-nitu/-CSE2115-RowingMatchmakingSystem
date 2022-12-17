package com.example.notificationmicroservice.strategy;

import com.example.notificationmicroservice.domain.Notification;

public interface NotificationStrategy {
    boolean handleNotification(Notification notification);

    String getFailureMessage();
}
