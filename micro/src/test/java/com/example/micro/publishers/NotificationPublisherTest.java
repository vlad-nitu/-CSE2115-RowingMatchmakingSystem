package com.example.micro.publishers;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.micro.utils.BaseNotification;
import com.example.micro.utils.MatchingUtils;
import javax.ws.rs.core.Response;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

@ExtendWith(MockitoExtension.class)
public class NotificationPublisherTest {

    private final PrintStream standardOut = System.out;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

    @Mock
    private MatchingUtils matchingUtils;

    private NotificationPublisher notificationPublisher;

    @BeforeEach
    void setUp() {
        notificationPublisher = new NotificationPublisher(matchingUtils);
        System.setOut(new PrintStream(outputStreamCaptor));
    }

    @AfterEach
    public void cleanUp() {
        System.setOut(standardOut);
    }

    @Test
    public void notifyUserValid() throws Exception {
        BaseNotification notification = new BaseNotification("Nicu", 1L, "iron", "notifyOwner");
        Response res = Response.ok().build();
        when(matchingUtils.postRequest("/notifyUser", notification)).thenReturn(res);
        notificationPublisher.notifyUser(notification);
        verify(matchingUtils, times(1)).postRequest("/notifyUser", notification);
    }

    @Test
    public void notifyUserInvalid() throws Exception {
        BaseNotification notification = new BaseNotification("Nicu", 1L, "iron", "notifyOwner");
        when(matchingUtils.postRequest("/notifyUser", notification)).thenThrow(new Exception("lol"));
        notificationPublisher.notifyUser(notification);

        assertThat(outputStreamCaptor.toString().trim()).isEqualTo("lol");
    }
}
