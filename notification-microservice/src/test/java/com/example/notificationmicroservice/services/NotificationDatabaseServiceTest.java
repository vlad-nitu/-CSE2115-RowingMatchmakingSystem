package com.example.notificationmicroservice.services;

import com.example.notificationmicroservice.domain.Notification;
import com.example.notificationmicroservice.repositories.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class NotificationDatabaseServiceTest {

    NotificationRepository notificationRepository;
    NotificationDatabaseService notificationDatabaseService;

    @BeforeEach
    void setUp() {
        notificationRepository = Mockito.mock(NotificationRepository.class);
        notificationDatabaseService = new NotificationDatabaseService(notificationRepository);
    }

    @Test
    public void findAllTest() {
        List<Notification> result = List.of(new Notification("some", "other", 1L, "request", "cox"));
        when(notificationDatabaseService.findAll()).thenReturn(result);
        assertThat(notificationDatabaseService.findAll()).isEqualTo(result);
    }

    @Test
    void findNotificationsByTargetId() {
        Notification notification = new Notification("some", "other", 1L, "request", "cox");
        notificationDatabaseService.findNotificationsByTargetId(notification.getTargetId());
        verify(notificationRepository, times(1)).findNotificationsByTargetId(notification.getTargetId());
        verify(notificationRepository, never()).findNotificationsByTargetId("some");
    }

    @Test
    void removeNotificationsByTargetId() {
        Notification notification = new Notification("some", "other", 1L, "request", "cox");
        notificationDatabaseService.removeNotificationsByTargetId(notification.getTargetId());
        verify(notificationRepository, times(1)).removeNotificationsByTargetId(notification.getTargetId());
        verify(notificationRepository, never()).removeNotificationsByTargetId("some");
    }

    @Test
    void save() {
        Notification notification = new Notification("some", "other", 1L, "request", "cox");
        notificationDatabaseService.save(notification);
        verify(notificationRepository, times(1)).save(notification);
    }
}