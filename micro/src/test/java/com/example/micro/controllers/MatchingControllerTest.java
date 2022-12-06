package com.example.micro.controllers;

import com.example.micro.publishers.ActivityPublisher;
import com.example.micro.publishers.NotificationPublisher;
import com.example.micro.publishers.UserPublisher;
import com.example.micro.services.MatchingServiceImpl;
import com.example.micro.utils.FunctionUtils;
import com.example.micro.utils.TimeSlot;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class MatchingControllerTest {
    MockMvc mockMvc;
    @Mock
    private MatchingServiceImpl matchingServiceImpl;
    @Mock
    private ActivityPublisher activityPublisher;
    @Mock
    private UserPublisher userPublisher;
    @Mock
    private NotificationPublisher notificationPublisher;


    private MatchingController matchingController;
    private String userId;
    private List<TimeSlot> timeSlots;
    private String server = "localhost:8083";

    @BeforeEach
    void setUp() {
        userId = "Vlad";
        timeSlots = List.of(new TimeSlot(
                LocalDateTime.of(2003, 12, 1, 23, 15),
                LocalDateTime.of(2002, 11, 2, 15, 00)
        ));
        this.matchingController = new MatchingController(matchingServiceImpl, activityPublisher, userPublisher, notificationPublisher);
        mockMvc = MockMvcBuilders
                .standaloneSetup(matchingController)
                .build();
    }

    @Test
    public void getAvailableActivitiesTest() throws Exception {
        when(matchingServiceImpl.findActivitiesByUserId(userId)).thenReturn(List.of());
        when(activityPublisher.getTimeSlots(List.of())).thenReturn(List.of());
        // when(FunctionUtils.filterTimeSlots(List.of(), List.of())).thenReturn(List.of());
        when(activityPublisher.getAvailableActivities(userId, List.of())).thenReturn(List.of());
        doNothing().when(userPublisher).sendAvailableActivities(List.of()); // mock void method

        System.out.println(new ObjectMapper().writeValueAsString(timeSlots));

        MvcResult result = mockMvc
                .perform(post("/getAvailableActivities/Vlad")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(List.of()))
                )
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertThat(content).isEqualTo("[]"); // Empty list
    }


}
