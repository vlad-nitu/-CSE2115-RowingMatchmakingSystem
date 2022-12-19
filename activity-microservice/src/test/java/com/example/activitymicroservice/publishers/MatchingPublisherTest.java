package com.example.activitymicroservice.publishers;

import com.example.activitymicroservice.utils.ActivityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.ws.rs.core.Response;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MatchingPublisherTest {

    @Mock
    ActivityUtils activityUtils;
    MatchingPublisher matchingPublisher;

    @BeforeEach
    void setUp() {
        matchingPublisher = new MatchingPublisher(activityUtils);
    }

    @Test
    void not204Response() throws Exception {
        Response res = Response.status(500).build();
        when(activityUtils.getRequest(anyString())).thenReturn(res);
        assertFalse(matchingPublisher.deleteMatchingByActivityId(1L));
    }

    @Test
    void successfulDeletionResponse() throws Exception {
        Response res = Response.status(204).build();
        when(activityUtils.getRequest(anyString())).thenReturn(res);
        assertTrue(matchingPublisher.deleteMatchingByActivityId(1L));
    }

    @Test
    void errorRequest() throws Exception {
        when(activityUtils.getRequest(anyString())).thenThrow(new Exception());
        assertFalse(matchingPublisher.deleteMatchingByActivityId(1L));
    }
}