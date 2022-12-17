package com.example.micro.publishers;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.example.micro.utils.MatchingUtils;
import javax.ws.rs.core.Response;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DefaultPublisherTest {

    private DefaultPublisher defaultPublisher;
    @Mock
    private MatchingUtils matchingUtils;

    /**
     * Method called in order to initialise all instances needed in common tests.
     *
     */
    @BeforeEach
    public void setUp() {
        this.defaultPublisher = new DefaultPublisher(matchingUtils);
    }

    @Test
    void testDefaultConstructor() {
        defaultPublisher = new DefaultPublisher();
        assertThat(defaultPublisher).isNotNull();
    }

    @Test
    void testConstructor() {
        defaultPublisher = new DefaultPublisher(matchingUtils);
        assertThat(defaultPublisher).isNotNull();
    }

    @Test
    void getGreetingsTestPass() throws Exception {
        Response res = Response.ok("Vlad").build();
        when(matchingUtils.getRequest("/hello")).thenReturn(res);
        String obtainedGreeting = defaultPublisher.getGreetings();
        String expectedGreeting = "Vlad";
        assertThat(expectedGreeting).isEqualTo(obtainedGreeting);
    }

    @Test
    void getGreetingsTestFails() throws Exception {
        Response res = Response.ok("Vlad").build();
        when(matchingUtils.getRequest("/hello")).thenThrow(Exception.class);
        assertThat(defaultPublisher.getGreetings()).isEqualTo(""); // Exception cached
    }


}