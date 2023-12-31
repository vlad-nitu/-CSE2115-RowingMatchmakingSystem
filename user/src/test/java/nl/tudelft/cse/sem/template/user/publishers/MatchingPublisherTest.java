package nl.tudelft.cse.sem.template.user.publishers;

import nl.tudelft.cse.sem.template.user.utils.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
public class MatchingPublisherTest {
    @Mock
    private UserUtils userUtils;
    private MatchingPublisher matchingPublisher;

    private BaseMatching matching;
    private Set<TimeSlot> timeSlots;

    @BeforeEach
    void setUp() {
        matchingPublisher = new MatchingPublisher(userUtils);
        matching = new BaseMatching("LotteKremer", 1L, "cox", true);
        timeSlots = new HashSet<>();
        timeSlots.add(new TimeSlot(LocalDateTime.of(2003, 12, 1, 23, 15),
                LocalDateTime.of(2002, 11, 2, 15, 0)));
        timeSlots.add(new TimeSlot(LocalDateTime.of(2003, 12, 1, 23, 15),
                LocalDateTime.of(2002, 11, 2, 15, 0)));
    }

    @Test
    public void getAvailableActivitiesTestValid() throws Exception {
        List<Pair<Long, String>> activities = List.of(new Pair<Long, String>(1L, "message"));
        Response res = Response.ok(activities).build();
        when(userUtils.postRequest("/getAvailableActivities/LotteKremer", new ArrayList<>(timeSlots))).thenReturn(res);
        assertThat(matchingPublisher.getAvailableActivities("LotteKremer", timeSlots)).isEqualTo(activities);
    }

    @Test
    public void getAvailableActivitiesTestInvalid() throws Exception {
        Response response = Response.status(400).build();
        when(userUtils.postRequest("/getAvailableActivities/LotteKremer", new ArrayList<>(timeSlots)))
                .thenReturn(response);
        assertThat(matchingPublisher.getAvailableActivities("LotteKremer", timeSlots))
                .isEqualTo(null);
    }

    @Test
    public void getUserActivitiesValid() throws Exception {
        List<Long> activityIds = List.of(1L, 2L, 3L);
        Response res = Response.ok(activityIds).build();
        when(userUtils.getRequest("/getUserActivities/LotteKremer")).thenReturn(res);
        assertThat(matchingPublisher.getUserActivities("LotteKremer")).isEqualTo(activityIds);
    }

    @Test
    public void getUserActivitiesTestInvalid() throws Exception {
        Response response = Response.status(400).build();
        when(userUtils.getRequest("/getUserActivities/LotteKremer")).thenReturn(response);
        assertThat(matchingPublisher.getUserActivities("LotteKremer")).isEqualTo(null);
    }

    @Test
    public void decideMatchTestValid() throws Exception {
        Response res = Response.ok(matching).build();
        when(userUtils.postRequest("/decideMatch/LotteKremer/accept", matching)).thenReturn(res);
        assertThat(matchingPublisher.decideMatch("LotteKremer", "accept", matching)).isEqualTo(matching);
    }

    @Test
    public void decideMatchTestInvalid() throws Exception {
        Response response = Response.status(400).build();
        when(userUtils.postRequest("/decideMatch/LotteKremer/accept", matching))
                .thenReturn(response);
        assertThat(matchingPublisher.decideMatch("LotteKremer", "accept", matching))
                .isEqualTo(null);
    }

    @Test
    public void chooseActivityTestInvalid() throws Exception {
        Response response = Response.status(400).build();
        when(userUtils.postRequest("/chooseActivity", matching)).thenReturn(response);
        assertThat(matchingPublisher.chooseActivity(matching)).isEqualTo(null);
    }

    @Test
    public void chooseActivityTestValid() throws Exception {
        Response res = Response.ok(matching).build();
        when(userUtils.postRequest("/chooseActivity", matching)).thenReturn(res);
        assertThat(matchingPublisher.chooseActivity(matching)).isEqualTo(matching);
    }

    @Test
    public void unenrollTestValid() throws Exception {
        Pair<String, Long>  userActivityPair = new Pair<String, Long>("LotteKremer", 1L);
        Response res = Response.ok(userActivityPair).build();
        when(userUtils.postRequest("/unenroll", userActivityPair)).thenReturn(res);
        assertThat(matchingPublisher.unenroll(userActivityPair)).isEqualTo(userActivityPair);
    }

    @Test
    public void unenrollTestInvalid() throws Exception {
        Response response = Response.status(400).build();
        Pair<String, Long>  userActivityPair = new Pair<String, Long>("LotteKremer", 1L);
        when(userUtils.postRequest("/unenroll", userActivityPair)).thenReturn(response);
        assertThat(matchingPublisher.unenroll(userActivityPair)).isEqualTo(null);
    }
}
