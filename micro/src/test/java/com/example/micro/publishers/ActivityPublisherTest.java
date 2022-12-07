package com.example.micro.publishers;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.micro.domain.Matching;
import com.example.micro.utils.MatchingUtils;
import com.example.micro.utils.Pair;
import com.example.micro.utils.TimeSlot;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ActivityPublisherTest {
    @Mock
    private MatchingUtils matchingUtils;

    private ActivityPublisher activityPublisher;

    private List<Long> activityIds;

    private List<TimeSlot> timeSlots;

    @BeforeEach
    void setUp() {
        activityPublisher = new ActivityPublisher(matchingUtils);
        activityIds = List.of(1L, 2L);
        timeSlots = new ArrayList<>();
        timeSlots.add(new TimeSlot(LocalDateTime.of(2003, 12, 1, 23, 15),
                LocalDateTime.of(2002, 11, 2, 15, 0)));
        timeSlots.add(new TimeSlot(LocalDateTime.of(2003, 12, 1, 23, 15),
                LocalDateTime.of(2002, 11, 2, 15, 0)));
    }

    @Test
    public void getTimeSlotsTestPass() throws Exception {
        Response res = Response.ok(timeSlots).build();
        when(matchingUtils.postRequest("/sendTimeSlots", activityIds)).thenReturn(res);
        assertThat(activityPublisher.getTimeSlots(activityIds)).isEqualTo(timeSlots);
    }

    @Test
    public void getTimeSlotsTestInvalid() throws Exception {
        when(matchingUtils.postRequest("/sendTimeSlots", activityIds)).thenThrow(new Exception("lol"));
        assertThat(activityPublisher.getTimeSlots(activityIds)).isEqualTo(new ArrayList<>());
    }

    @Test
    public void getAvailableActivitiesValid() throws Exception {
        Pair<Long, String> a = new Pair<Long, String>(1L, "swimmer");
        List<Pair<Long, String>> b = new ArrayList<>();
        b.add(a);
        Response res = Response.ok(b).build();
        when(matchingUtils.postRequest("/sendAvailableActivities/User", timeSlots)).thenReturn(res);
        assertThat(activityPublisher.getAvailableActivities("User", timeSlots)).isEqualTo(b);
    }

    @Test
    public void getAvailableActivitiesInvalid() throws Exception {
        when(matchingUtils.postRequest("/sendAvailableActivities/User", timeSlots)).thenThrow(new Exception("lol"));
        assertThat(activityPublisher.getAvailableActivities("User", timeSlots)).isEqualTo(new ArrayList<>());
    }

    @Test
    public void getOwnerIdValid() throws Exception {
        Response res = Response.ok("tataVlad").build();
        when(matchingUtils.getRequest("/sendOwnerId/1")).thenReturn(res);
        assertThat(activityPublisher.getOwnerId(1L)).isEqualTo("tataVlad");
    }

    @Test
    public void getOwnerIdInvalid() throws Exception {
        when(matchingUtils.getRequest("/sendOwnerId/1")).thenThrow(new Exception("lol"));
        assertThat(activityPublisher.getOwnerId(1L)).isEqualTo("");
    }

    @Test
    public void unenrollValid() throws Exception {
        Response res = Response.ok().build();
        Pair<Long, String> posTaken = new Pair<Long, String>(1L, "cox");
        when(matchingUtils.postRequest("/unenrollPosition", posTaken)).thenReturn(res);
        activityPublisher.unenroll(1L, "cox");
        verify(matchingUtils, times(1)).postRequest("/unenrollPosition", posTaken);
    }

    @Test
    public void checkValid() throws Exception {
        Matching a = new Matching("tataVlad", 1L, "maestru", null);
        Response res = Response.ok(true).build();
        when(matchingUtils.getRequest("/check/tataVlad/1")).thenReturn(res);
        assertThat(activityPublisher.check(a)).isEqualTo(true);
    }

    @Test
    public void checkInvalid() throws Exception {
        Matching a = new Matching("tataVlad", 1L, "maestru", null);
        when(matchingUtils.getRequest("/check/tataVlad/1")).thenThrow(new Exception("lol"));
        assertThat(activityPublisher.check(a)).isEqualTo(false);
    }
}
