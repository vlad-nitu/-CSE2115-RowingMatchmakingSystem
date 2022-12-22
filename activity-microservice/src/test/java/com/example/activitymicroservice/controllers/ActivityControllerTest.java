package com.example.activitymicroservice.controllers;

import com.example.activitymicroservice.authentication.AuthManager;
import com.example.activitymicroservice.domain.Activity;
import com.example.activitymicroservice.domain.Training;
import com.example.activitymicroservice.publishers.MatchingPublisher;
import com.example.activitymicroservice.publishers.UserPublisher;
import com.example.activitymicroservice.services.ActivityService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ActivityControllerTest {
    private MockMvc mockMvc;

    @Mock
    private AuthManager authManager;
    @Mock
    private MatchingPublisher matchingPublisher;
    @Mock
    private UserPublisher userPublisher;
    @Mock
    private ActivityService activityService;
    private final ObjectMapper objectMapper = new ObjectMapper();


    @BeforeEach
    void setUp() {
        ActivityController activityController = new ActivityController(activityService, userPublisher,
                matchingPublisher, authManager);
        mockMvc = MockMvcBuilders
                .standaloneSetup(activityController)
                .build();
    }

    @Test
    void emptyOptional() throws Exception {
        when(activityService.findActivityOptional(1L)).thenReturn(Optional.empty());
        MvcResult res = mockMvc
                .perform(post("/cancelActivity/1")
                )
                .andExpect(status().isNotFound())
                .andReturn();

        assertThat(res.getResponse().getContentAsString()).isEmpty();
    }

    @Test
    void wrongOwner() throws Exception {
        Activity act = new Training();
        act.setOwnerId("Owner");
        when(activityService.findActivityOptional(1L)).thenReturn(Optional.of(act));
        when(authManager.getUserId()).thenReturn("User");
        MvcResult res = mockMvc
                .perform(post("/cancelActivity/1"))
                .andExpect(status().isForbidden())
                .andReturn();

        assertThat(res.getResponse().getContentAsString()).isEmpty();
    }

    @Test
    void matchingsFailedToDelete() throws Exception {
        Activity act = new Training();
        act.setOwnerId("Owner");
        when(activityService.findActivityOptional(1L)).thenReturn(Optional.of(act));
        when(authManager.getUserId()).thenReturn("Owner");
        when(matchingPublisher.deleteMatchingByActivityId(1L)).thenReturn(false);
        MvcResult res = mockMvc
                .perform(post("/cancelActivity/1"))
                .andExpect(status().isInternalServerError())
                .andReturn();

        assertThat(res.getResponse().getContentAsString()).isEmpty();
    }

    @Test
    void success() throws Exception {
        Activity act = new Training();
        act.setOwnerId("Owner");
        when(activityService.findActivityOptional(1L)).thenReturn(Optional.of(act));
        when(authManager.getUserId()).thenReturn("Owner");
        when(matchingPublisher.deleteMatchingByActivityId(1L)).thenReturn(true);
        MvcResult res = mockMvc
                .perform(post("/cancelActivity/1"))
                .andExpect(status().isNoContent())
                .andReturn();

        Activity returned = objectMapper.readValue(res.getResponse().getContentAsString(), Activity.class);
        assertThat(returned).isEqualTo(act);
    }
}