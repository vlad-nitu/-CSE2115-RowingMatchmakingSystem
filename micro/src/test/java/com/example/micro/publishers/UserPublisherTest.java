package com.example.micro.publishers;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.micro.utils.MatchingUtils;
import com.example.micro.utils.Pair;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UserPublisherTest {
    @Mock
    private MatchingUtils matchingUtils;

    private UserPublisher userPublisher;

    @BeforeEach
    void setUp() {
        userPublisher = new UserPublisher(matchingUtils);
    }

    @Test
    public void sendAvailableActivitiesValid() throws Exception {
        List<Pair<Long, String>> possibleMatchings = new ArrayList<>();
        possibleMatchings.add(new Pair<Long, String>(1L, "iron"));
        Response res = Response.ok().build();
        when(matchingUtils.postRequest("/getAvailableActivities", possibleMatchings)).thenReturn(res);
        userPublisher.sendAvailableActivities(possibleMatchings);
        verify(matchingUtils, times(1)).postRequest("/getAvailableActivities", possibleMatchings);
    }

    @Test
    public void sendAvailableActivitiesInvalid() throws Exception {
        List<Pair<Long, String>> possibleMatchings = new ArrayList<>();
        possibleMatchings.add(new Pair<Long, String>(1L, "iron"));
        when(matchingUtils.postRequest("/getAvailableActivities", possibleMatchings)).thenThrow(new Exception("lol"));
        userPublisher.sendAvailableActivities(possibleMatchings);
    }
}
