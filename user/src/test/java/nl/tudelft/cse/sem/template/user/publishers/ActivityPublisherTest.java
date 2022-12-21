package nl.tudelft.cse.sem.template.user.publishers;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import nl.tudelft.cse.sem.template.user.utils.UserUtils;
import nl.tudelft.cse.sem.template.user.utils.TimeSlot;
import nl.tudelft.cse.sem.template.user.utils.BaseActivity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import javax.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ActivityPublisherTest {
    @Mock
    private UserUtils userUtils;

    private ActivityPublisher activityPublisher;

    private BaseActivity activity;
    //private List<TimeSlot> timeSlots;

    @BeforeEach
    void setUp() {
        activityPublisher = new ActivityPublisher(userUtils);
        TimeSlot timeSlot = new TimeSlot(LocalDateTime.of(2022, 12, 15, 16, 15),
                LocalDateTime.of(2022, 12, 15, 17, 45));
        activity = new BaseActivity(1L, "LotteKremer", timeSlot, Set.of("cox"), "C4",  false, 'M', "sem33a");
    }

    @Test
    public void createActivityTestValid() throws Exception {
        Response res = Response.ok().build();
        when(userUtils.postRequest("/createActivity", activity)).thenReturn(res);
        activityPublisher.createActivity(activity);
        verify(userUtils, times(1)).postRequest("/createActivity", activity);
    }

    @Test
    public void createActivityTestInvalid() throws Exception {
        when(userUtils.postRequest("/createActivity", activity)).thenThrow(new Exception("Invalid"));
        activityPublisher.createActivity(activity);
    }
}
