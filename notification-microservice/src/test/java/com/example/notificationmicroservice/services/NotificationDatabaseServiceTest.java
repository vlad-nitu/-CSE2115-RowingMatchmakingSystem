package com.example.notificationmicroservice.services;

import com.example.notificationmicroservice.domain.Notification;
import com.example.notificationmicroservice.repositories.NotificationRepository;
import org.aspectj.weaver.ast.Not;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationDatabaseServiceTest {

    @Mock
    NotificationRepository notificationRepository;
    NotificationDatabaseService notificationDatabaseService;

    @BeforeEach
    void setUp() {
        notificationDatabaseService = new NotificationDatabaseService(notificationRepository);
    }

    @Test
    public void findAllTest() {
        List<Notification> result = List.of(new Notification("other", 1L, "request", "cox"));
        when(notificationDatabaseService.findAll()).thenReturn(result);
        assertThat(notificationDatabaseService.findAll()).isEqualTo(result);
    }

    @Test
    void findNotificationsByTargetId() {
        Notification notification = new Notification("other", 1L, "request", "cox");
        when(notificationRepository.findNotificationsByTargetId("other")).thenReturn(List.of(notification));
        List<Notification> res = notificationDatabaseService.findNotificationsByTargetId(notification.getTargetId());
        verify(notificationRepository, times(1)).findNotificationsByTargetId(notification.getTargetId());
        assertThat(res).containsExactly(notification);
    }

    @Test
    void removeNotificationsByTargetId() {
        Notification notification = new Notification("other", 1L, "request", "cox");
        when(notificationRepository.removeNotificationsByTargetId("other")).thenReturn(List.of(notification));
        List<Notification> res = notificationDatabaseService.removeNotificationsByTargetId(notification.getTargetId());
        verify(notificationRepository, times(1)).removeNotificationsByTargetId(notification.getTargetId());
        assertThat(res).containsExactly(notification);
    }

    @Test
    void save() {
        Notification notification = new Notification("other", 1L, "request", "cox");
        when(notificationDatabaseService.save(notification)).thenReturn(notification);
        Notification res = notificationDatabaseService.save(notification);
        verify(notificationRepository, times(1)).save(notification);
        assertThat(res).isEqualTo(notification);
    }
}