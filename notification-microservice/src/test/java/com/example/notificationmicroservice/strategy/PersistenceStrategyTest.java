package com.example.notificationmicroservice.strategy;

import com.example.notificationmicroservice.domain.Notification;
import com.example.notificationmicroservice.services.NotificationDatabaseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PersistenceStrategyTest {

    @Mock
    NotificationDatabaseService notificationDatabaseService;
    PersistenceStrategy persistenceStrategy;

    @BeforeEach
    void setUp() {
        persistenceStrategy = new PersistenceStrategy(notificationDatabaseService);
    }

    @Test
    void handleNotificationSaveNull() {
        Notification notification = new Notification("targetId", 1L, "notifyOwner", "cox");
        when(notificationDatabaseService.save(notification)).thenReturn(null);
        assertFalse(persistenceStrategy.handleNotification(notification));
    }

    @Test
    void handleNotificationSuccess() {
        Notification notification = new Notification("targetId", 1L, "notifyOwner", "cox");
        when(notificationDatabaseService.save(notification)).thenReturn(notification);
        assertTrue(persistenceStrategy.handleNotification(notification));
    }
}