package com.example.notificationmicroservice.controllers;

import com.example.notificationmicroservice.domain.Notification;
import com.example.notificationmicroservice.services.NotificationDatabaseService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class NotificationControllerTest {
    private NotificationDatabaseService notificationDatabaseService;
    private NotificationController notificationController;
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        notificationDatabaseService = Mockito.mock(NotificationDatabaseService.class);
        notificationController = new NotificationController(notificationDatabaseService);
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

    /*
    Gonna add when can use authorisation
    @Test
    void getNotificationsByTargetAuthorised() throws Exception
    @Test
    void getNotificationsByTargetUnauthorised() throws Exception
    */
}