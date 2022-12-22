package nl.tudelft.cse.sem.template.user.publishers;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import nl.tudelft.cse.sem.template.user.utils.UserUtils;
import nl.tudelft.cse.sem.template.user.utils.TimeSlot;
import nl.tudelft.cse.sem.template.user.utils.BaseActivity;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import java.time.LocalDateTime;
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

    @BeforeEach
    void setUp() {
        activityPublisher = new ActivityPublisher(userUtils);
        TimeSlot timeSlot = new TimeSlot(LocalDateTime.of(2022, 12, 15, 16, 15),
                LocalDateTime.of(2022, 12, 15, 17, 45));
        activity = new BaseActivity(1L, "LotteKremer", timeSlot, Set.of("cox"), "C4",  false, 'M', "sem33a");
    }

    @Test
    public void createActivityTestValid() throws Exception {
        Response response = Response.status(200).entity(new BaseActivity()).build();
        when(userUtils.postRequest("/createActivity", activity)).thenReturn(response);
        assertThat(activityPublisher.createActivity(activity)).isNotEqualTo(null);
        verify(userUtils, times(1)).postRequest("/createActivity", activity);
    }

    @Test
    public void createActivityTestInvalid() throws Exception {
        Response response = Response.status(400).build();
        when(userUtils.postRequest("/createActivity", activity)).thenReturn(response);
        assertThat(activityPublisher.createActivity(activity)).isEqualTo(null);
    }
}
