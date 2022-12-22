package com.example.notificationmicroservice.controllers;

import com.example.notificationmicroservice.authentication.AuthManager;
import com.example.notificationmicroservice.domain.Notification;
import com.example.notificationmicroservice.services.NotificationDatabaseService;
import com.example.notificationmicroservice.strategy.NotificationStrategy;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class NotificationControllerTest {
    @Mock
    private NotificationDatabaseService notificationDatabaseService;
    @Mock
    private AuthManager authManager;
    @Mock
    private NotificationStrategy strategy;
    private NotificationController notificationController;
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        notificationController = new NotificationController(notificationDatabaseService, authManager, strategy);
        mockMvc = MockMvcBuilders
                .standaloneSetup(notificationController)
                .build();
    }

    @Test
    void notifyUserSuccess() throws Exception {
        String expectedSuccessMessage = "Successfully notified";
        Notification notification = new Notification("userId", "targetId", 1L, "notifyOwner", "cox");
        when(strategy.handleNotification(notification)).thenReturn(true);
        MvcResult mvcResult = mockMvc
                .perform(post("/notifyUser")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(notification))
                )
                .andExpect(status().isOk())
                .andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        assertThat(contentAsString).isEqualTo(expectedSuccessMessage);
    }

    @Test
    void notifyUserFailure() throws Exception {
        String expectedFailureMessage = "message";
        Notification notification = new Notification("userId", "targetId", 1L, "notifyOwner", "cox");
        when(strategy.handleNotification(notification)).thenReturn(false);
        when(strategy.getFailureMessage()).thenReturn(expectedFailureMessage);
        MvcResult mvcResult = mockMvc
                .perform(post("/notifyUser")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(notification))
                )
                .andExpect(status().isInternalServerError())
                .andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        assertThat(contentAsString).isEqualTo(expectedFailureMessage);
    }

    @Test
    void getNotificationsCorrectUser() throws Exception {
        Notification notification = new Notification("userId", "targetId", 1L, "notifyOwner", "cox");
        List<Notification> foundNotifications = List.of(notification);
        List<String> expected = foundNotifications.stream().map(x -> x.buildMessage()).collect(Collectors.toList());
        when(notificationDatabaseService.findNotificationsByTargetId(notification.getTargetId()))
                .thenReturn(foundNotifications);
        when(authManager.getNetId()).thenReturn(notification.getTargetId());
        MvcResult mvcResult = mockMvc
                .perform(get("/getNotifications/" + notification.getTargetId()))
                .andExpect(status().isOk())
                .andReturn();

        verify(notificationDatabaseService, times(1)).findNotificationsByTargetId(notification.getTargetId());
        String contentAsString = mvcResult.getResponse().getContentAsString();
        List<String> returned = objectMapper.readValue(contentAsString, new TypeReference<>(){});
        assertThat(returned).isEqualTo(expected);
    }

    @Test
    void getNotificationsIncorrectUser() throws Exception {
        Notification notification = new Notification("userId", "targetId", 1L, "notifyOwner", "cox");
        when(authManager.getNetId()).thenReturn(notification.getUserId());
        MvcResult mvcResult = mockMvc
                .perform(get("/getNotifications/" + notification.getTargetId()))
                .andExpect(status().isForbidden())
                .andReturn();

        verify(notificationDatabaseService, never()).findNotificationsByTargetId(notification.getTargetId());
    }

    @Test
    void validationTest() throws Exception {
        Notification notification = new Notification("", "", null, "", "");
        MvcResult mvcResult = mockMvc
                .perform(post("/notifyUser")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(notification))
                )
                .andExpect(status().isBadRequest())
                .andReturn();

        String contentAsString = mvcResult.getResponse().getContentAsString();
        assertThat(contentAsString).contains("activityId is mandatory and cannot be null");
        assertThat(contentAsString).contains("targetId is mandatory and cannot be blank");
        assertThat(contentAsString).contains("userId is mandatory and cannot be blank");
        assertThat(contentAsString).contains("type is mandatory and cannot be blank");
        assertThat(contentAsString).contains("userId is mandatory and cannot be blank");
    }
}