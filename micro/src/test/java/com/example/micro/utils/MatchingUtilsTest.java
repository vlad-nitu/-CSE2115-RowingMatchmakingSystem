package com.example.micro.utils;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;


@ExtendWith(MockitoExtension.class)
public class MatchingUtilsTest {

    private MatchingUtils matchingUtils;

    public String server;

    @Mock
    private Authentication auth;

    @Mock(answer = RETURNS_DEEP_STUBS, lenient = true)
    public ResteasyClient client;

    /**
     * Executes before each test in order to initialise primary tested object MatchingUtils.
     */
    @BeforeEach
    public void setUp() {
        this.server = "dummyServer";
        matchingUtils = new MatchingUtils(server, client);
    }

    @Test
    public void testEmptyConstructor() {
        MatchingUtils otherMatchingUtils = new MatchingUtils();
        assertThat(otherMatchingUtils).isNotNull();
        assertThat(otherMatchingUtils.server).isEqualTo("");
        assertThat(otherMatchingUtils.client).isInstanceOf(ResteasyClient.class);
    }

    @Test
    public void testServerConstructor() { // the one that is mainly used in the system
        MatchingUtils otherMatchingUtils = new MatchingUtils(server);
        assertThat(otherMatchingUtils).isNotNull();
        assertThat(otherMatchingUtils.server).isEqualTo(server);
        assertThat(otherMatchingUtils.client).isInstanceOf(ResteasyClient.class);
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
                matchingUtils.getRequest("dummyPath"))
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

        Response obtained = matchingUtils.getRequest(server);

        SecurityContextHolder.clearContext();
        assertThat(expected.getStatus())
                .isEqualTo(obtained.getStatus());
    }


    @Test
    public void postRequestFail() {
        String data = "dummyData";

        given(client.target(anyString())
                .path(anyString())
                .request(anyString())
                .accept(anyString()))
                .willAnswer(invocation -> Exception.class);

        assertThatThrownBy(() ->
                matchingUtils.postRequest("dummyPath", data))
                .isInstanceOf(Exception.class)
                .hasMessage("Bad request");
    }

    @Test
    public void postRequestTest() throws Exception {
        String data = "dummyData";
        Response expected = Response.ok().build();
        when(client.target(anyString())
                .path(anyString())
                .request(anyString())
                .header(anyString(), anyString())
                .accept(anyString())
                .post(Entity.entity(data, APPLICATION_JSON), Response.class))
                .thenReturn(Response.ok().build());
        when(auth.getCredentials()).thenReturn("mockedPassword");
        SecurityContextHolder.getContext().setAuthentication(auth);
        Response obtained = matchingUtils.postRequest(server, data);
        assertThat(expected.getStatus())
                .isEqualTo(obtained.getStatus());


    }


}
