package nl.tudelft.cse.sem.template.user.controllers;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import nl.tudelft.cse.sem.template.user.authentication.AuthManager;
import nl.tudelft.cse.sem.template.user.publishers.NotificationPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class UserNotificationControllerTest {
    MockMvc mockMvc;
    @Mock
    private NotificationPublisher notificationPublisher;
    @Mock
    private AuthManager authManager;
    private UserNotificationController userNotificationController;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        this.userNotificationController = new UserNotificationController(notificationPublisher, authManager);

        mockMvc = MockMvcBuilders
                .standaloneSetup(userNotificationController)
                .build();
    }

    @Test
    void getNotifications() throws Exception {
        List<String> expected = List.of("A new user has applied as a cox for activity with Id: 1",
                "A new user has applied as a cox for activity with Id: 2");
        when(notificationPublisher.getNotifications(null)).thenReturn(expected);
        MvcResult mvcResult = mockMvc
                .perform(get("/getNotifications"))
                .andExpect(status().isOk())
                .andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        assertThat(contentAsString).contains(expected);

        expected = null;
        when(notificationPublisher.getNotifications(null)).thenReturn(expected);
        mvcResult = mockMvc
                .perform(get("/getNotifications"))
                .andExpect(status().is4xxClientError())
                .andReturn();
        contentAsString = mvcResult.getResponse().getContentAsString();
        assertThat(contentAsString).contains("Something went wrong!");
    }
}
