package com.example.notificationmicroservice.controllers;

import com.example.notificationmicroservice.authentication.AuthManager;
import com.example.notificationmicroservice.domain.Notification;
import com.example.notificationmicroservice.services.NotificationDatabaseService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class NotificationControllerTest {
    private NotificationDatabaseService notificationDatabaseService;
    private NotificationController notificationController;
    private AuthManager authManager;
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        notificationDatabaseService = Mockito.mock(NotificationDatabaseService.class);
        authManager = Mockito.mock(AuthManager.class);
        notificationController = new NotificationController(notificationDatabaseService, authManager);
        mockMvc = MockMvcBuilders
                .standaloneSetup(notificationController)
                .build();
    }

    @Test
    void saveNotification() throws Exception {
        Notification notification = new Notification("userId", "targetId", 1L, "request", "cox");
        when(notificationDatabaseService.save(notification))
                .thenReturn(notification);

        MvcResult mvcResult = mockMvc
                .perform(post("/notifyUser")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(notification))
                )
                .andExpect(status().isOk())
                .andReturn();

        verify(notificationDatabaseService, times(1)).save(notification);
        String contentAsString = mvcResult.getResponse().getContentAsString();
        Notification returned = objectMapper.readValue(contentAsString, Notification.class);
        assertThat(returned).isEqualTo(notification);
    }

    @Test
    void getNotificationsCorrectUser() throws Exception {
        Notification notification = new Notification("userId", "targetId", 1L, "request", "cox");
        List<Notification> expected = List.of(notification);
        when(notificationDatabaseService.findNotificationsByTargetId(notification.getTargetId()))
                .thenReturn(expected);
        when(authManager.getNetId()).thenReturn(notification.getTargetId());
        MvcResult mvcResult = mockMvc
                .perform(get("/getNotifications/" + notification.getTargetId()))
                .andExpect(status().isOk())
                .andReturn();

        verify(notificationDatabaseService, times(1)).findNotificationsByTargetId(notification.getTargetId());
        String contentAsString = mvcResult.getResponse().getContentAsString();
        List<Notification> returned = objectMapper.readValue(contentAsString, new TypeReference<>(){});
        assertThat(returned).isEqualTo(expected);
    }

    @Test
    void getNotificationsIncorrectUser() throws Exception {
        Notification notification = new Notification("userId", "targetId", 1L, "request", "cox");
        String expected = "Only the recipient can ask for their notifications";
        when(authManager.getNetId()).thenReturn(notification.getUserId());
        MvcResult mvcResult = mockMvc
                .perform(get("/getNotifications/" + notification.getTargetId()))
                .andExpect(status().isBadRequest())
                .andReturn();

        verify(notificationDatabaseService, never()).findNotificationsByTargetId(notification.getTargetId());
        String contentAsString = mvcResult.getResponse().getContentAsString();
        assertThat(contentAsString).isEqualTo(expected);
    }

    @Test
    void validationTest() throws Exception {
        Notification notification = new Notification("", "", null, "", "cox");
        String expected = "{\"activityId\":\"activityId is mandatory and cannot be null\",\"" +
                "targetId\":\"targetId is mandatory and cannot be blank\",\"" +
                "type\":\"type is mandatory and cannot be blank\",\"userId\":\"" +
                "userId is mandatory and cannot be blank\"}";
        MvcResult mvcResult = mockMvc
                .perform(post("/notifyUser")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(notification))
                )
                .andExpect(status().isBadRequest())
                .andReturn();

        String contentAsString = mvcResult.getResponse().getContentAsString();
        assertThat(contentAsString).isEqualTo(expected);
    }
}