package nl.tudelft.cse.sem.template.user.publishers;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import nl.tudelft.cse.sem.template.user.utils.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
    private List<TimeSlot> timeSlots;

    @BeforeEach
    void setUp() {
        matchingPublisher = new MatchingPublisher(userUtils);
        matching = new BaseMatching("LotteKremer", 1L, "cox", true);
        timeSlots = new ArrayList<>();
        timeSlots.add(new TimeSlot(LocalDateTime.of(2003, 12, 1, 23, 15),
                LocalDateTime.of(2002, 11, 2, 15, 0)));
        timeSlots.add(new TimeSlot(LocalDateTime.of(2003, 12, 1, 23, 15),
                LocalDateTime.of(2002, 11, 2, 15, 0)));
    }

    @Test
    public void getAvailableActivitiesTestValid() throws Exception {
        List<Pair<Long, String>> activities = List.of(new Pair<Long, String>(1L, "message"));
        Response res = Response.ok(activities).build();
        when(userUtils.postRequest("/getAvailableActivities/LotteKremer", timeSlots)).thenReturn(res);
        assertThat(matchingPublisher.getAvailableActivities("LotteKremer", timeSlots)).isEqualTo(activities);
    }

    @Test
    public void getAvailableActivitiesTestInvalid() throws Exception {
        when(userUtils.postRequest("/getAvailableActivities/LotteKremer", timeSlots)).thenThrow(new Exception());
        assertThat(matchingPublisher.getAvailableActivities("LotteKremer", timeSlots)).
                isEqualTo(new ArrayList<Pair<Long, String>>());
    }

    @Test
    public void getUserActivitiesValid() {}

    @Test
    public void getUserActivitiesTestInvalid() {}

    @Test
    public void decideMatchTestValid() {}

    @Test
    public void decideMatchTestInvalid() {}

    @Test
    public void chooseActivityTestValid() {}

    @Test
    public void chooseActivityTestInvalid() {}

    @Test
    public void unenrollTestValid() {}

    @Test
    public void unenrollTestInvalid() {}
}
