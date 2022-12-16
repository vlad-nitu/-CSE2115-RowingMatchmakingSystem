package com.example.notificationmicroservice.utils;

import com.example.notificationmicroservice.domain.Notification;
import com.example.notificationmicroservice.publishers.UserPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
class EmailStrategyTest {

    @Mock
    UserPublisher userPublisher;
    @Mock
    JavaMailSender mailSender;
    EmailStrategy emailStrategy;
    Notification notification;

    @BeforeEach
    void setUp() {
        emailStrategy = new EmailStrategy(userPublisher, mailSender);
    }

    @Test
    void handleNotificationOwnerBadEmail() {
        notification = new Notification("userId", "targetId", 1L, "notifyOwner", "cox");
        doThrow(new MailSendException(""))
                .when(mailSender)
                .send(any(SimpleMailMessage.class));
        assertFalse(emailStrategy.handleNotification(notification));
    }

    @Test
    void handleNotificationUserGoodEmail() {
        notification = new Notification("userId", "targetId", 1L, "notifyUser", "cox");
        assertTrue(emailStrategy.handleNotification(notification));
    }
}