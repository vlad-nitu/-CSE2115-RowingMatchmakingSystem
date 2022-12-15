package nl.tudelft.cse.sem.template.user.publishers;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

import nl.tudelft.cse.sem.template.user.utils.UserUtils;
import nl.tudelft.cse.sem.template.user.utils.BaseNotification;

import javax.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class NotificationPublisherTest {
    @Mock
    private UserUtils userUtils;

    private NotificationPublisher notificationPublisher;
    private BaseNotification notification;

    @BeforeEach
    void setUp() {
        notificationPublisher = new NotificationPublisher(userUtils);
        notification = new BaseNotification("LotteKremer", "LotteKremer",
                1L, "Cox", "type1");
    }

    @Test
    public void getNotificationTestValid() throws Exception {
        Response res = Response.ok(notification).build();
        when(userUtils.getRequest("/getNotifications/LotteKremer")).thenReturn(res);
        assertThat(notificationPublisher.getNotifications("LotteKremer")).isEqualTo(notification);
    }

    @Test
    public void getNotificationTestInvalid() throws Exception {
        when(userUtils.getRequest("/getNotifications/LotteKremer")).thenThrow(new Exception("Invalid"));
        assertThat(notificationPublisher.getNotifications("LotteKremer")).isEqualTo(new BaseNotification());
    }

}
