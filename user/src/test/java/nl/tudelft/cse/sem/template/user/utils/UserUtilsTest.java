package nl.tudelft.cse.sem.template.user.utils;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;


@ExtendWith(MockitoExtension.class)
public class UserUtilsTest {

    private UserUtils userUtils;

    public String server;

    @Mock
    private Authentication auth;

    @Mock(answer = RETURNS_DEEP_STUBS, lenient = true)
    public ResteasyClient client;

    /**
     * Executes before each test in order to initialise primary tested object UserUtils.
     */
    @BeforeEach
    public void setUp() {
        this.server = "dummyServer";
        userUtils = new UserUtils(server, client);
    }

    @Test
    public void testEmptyConstructor() {
        UserUtils otherUserUtils = new UserUtils();
        assertThat(otherUserUtils).isNotNull();
        assertThat(otherUserUtils.server).isEqualTo("");
        assertThat(otherUserUtils.client).isInstanceOf(ResteasyClient.class);
    }

    @Test
    public void testServerConstructor() { // the one that is mainly used in the system
        UserUtils otherUserUtils = new UserUtils(server);
        assertThat(otherUserUtils).isNotNull();
        assertThat(otherUserUtils.server).isEqualTo(server);
        assertThat(otherUserUtils.client).isInstanceOf(ResteasyClient.class);
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
                userUtils.getRequest("dummyPath"))
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

        Response obtained = userUtils.getRequest(server);

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
                userUtils.postRequest("dummyPath", data))
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
        Response obtained = userUtils.postRequest(server, data);
        assertThat(expected.getStatus())
                .isEqualTo(obtained.getStatus());
        SecurityContextHolder.clearContext();
    }
}
