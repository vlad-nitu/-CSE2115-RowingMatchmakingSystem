package com.example.notificationmicroservice.publishers;

import com.example.notificationmicroservice.utils.NotificationUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import javax.ws.rs.core.Response;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserPublisherTest {

    @Mock
    NotificationUtils notificationUtils;
    UserPublisher userPublisher;

    @BeforeEach
    void setUp() {
        userPublisher = new UserPublisher(notificationUtils);
    }

    @Test
    void getEmailErr() throws Exception {
        doThrow(new Exception()).when(notificationUtils).getRequest(anyString());
        assertNull(userPublisher.getEmail("userId"));
    }

    @Test
    void getEmail() throws Exception {
        String expected = "emailAddress";
        when(notificationUtils.getRequest(anyString())).thenReturn(Response.ok(expected).build());
        assertEquals(expected, userPublisher.getEmail("userId"));
    }

    @Test
    void constructorServerTest() throws Exception {
        UserPublisher userPublisher1 = new UserPublisher();
        assertNotNull(userPublisher1);
    }
}