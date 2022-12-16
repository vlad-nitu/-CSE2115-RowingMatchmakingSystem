package com.example.notificationmicroservice.utils;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.ws.rs.core.Response;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationUtilsTest {

    private NotificationUtils notificationUtils;
    public String server;

    @Mock
    private Authentication auth;

    @Mock(answer = RETURNS_DEEP_STUBS, lenient = true)
    public ResteasyClient client;

    @BeforeEach
    public void setUp() {
        this.server = "dummyServer";
        notificationUtils = new NotificationUtils(server, client);
    }

    @Test
    public void testServerConstructor() { // the one that is mainly used in the system
        NotificationUtils otherNotificationUtils = new NotificationUtils(server);
        assertThat(otherNotificationUtils).isNotNull();
        assertThat(otherNotificationUtils.server).isEqualTo(server);
        assertThat(otherNotificationUtils.client).isInstanceOf(ResteasyClient.class);
    }

    @Test
    public void getRequestFail() {
        given(client.target(anyString())
                .path(anyString())
                .request(anyString())
                .header(anyString(), anyString())
                .accept(anyString())
                .get())
                .willAnswer(invocation -> Exception.class);


        assertThatThrownBy(() ->
                notificationUtils.getRequest("dummyPath"))
                .isInstanceOf(Exception.class)
                .hasMessage("Bad request");
    }

    @Test
    public void getRequestTest() throws Exception {
        when(auth.getCredentials()).thenReturn("mockedPassword");
        SecurityContextHolder.getContext().setAuthentication(auth);
        Response expected = Response.ok().build();
        when(client.target(anyString())
                .path(anyString())
                .request(anyString())
                .header(anyString(), anyString())
                .accept(anyString())
                .get(Response.class))
                .thenReturn(Response.ok().build());

        Response obtained = notificationUtils.getRequest(server);

        SecurityContextHolder.clearContext();
        assertThat(expected.getStatus())
                .isEqualTo(obtained.getStatus());
    }
}