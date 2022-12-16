package com.example.notificationmicroservice.utils;

import com.example.notificationmicroservice.domain.Notification;

public interface NotificationStrategy {
    boolean handleNotification(Notification notification);

    String getFailureMessage();
}
