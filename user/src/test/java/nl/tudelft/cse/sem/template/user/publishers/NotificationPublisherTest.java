package nl.tudelft.cse.sem.template.user.publishers;

import nl.tudelft.cse.sem.template.user.utils.BaseNotification;
import nl.tudelft.cse.sem.template.user.utils.UserUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class NotificationPublisherTest {
    @Mock
    private UserUtils userUtils;

    private NotificationPublisher notificationPublisher;
    private BaseNotification notification;
    private List<BaseNotification> notifications;

    @BeforeEach
    void setUp() {
        notificationPublisher = new NotificationPublisher(userUtils);
        notification = new BaseNotification("LotteKremer", "LotteKremer",
                1L, "Cox", "type1");
        notifications = new ArrayList<>();
        notifications.add(notification);
    }

    @Test
    public void getNotificationsTestValid() throws Exception {
        Response res = Response.ok(notifications).build();
        when(userUtils.getRequest("/getNotifications/LotteKremer")).thenReturn(res);
        assertThat(notificationPublisher.getNotifications("LotteKremer")).isEqualTo(notifications);
    }

    @Test
    public void getNotificationsTestInvalid() throws Exception {
        Response response = Response.status(400).build();
        when(userUtils.getRequest("/getNotifications/LotteKremer")).thenReturn(response);
        assertThat(notificationPublisher.getNotifications("LotteKremer")).isEqualTo(null);
    }

}
