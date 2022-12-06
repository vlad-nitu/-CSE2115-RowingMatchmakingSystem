package com.example.micro.controllers;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.micro.domain.Matching;
import com.example.micro.publishers.ActivityPublisher;
import com.example.micro.publishers.NotificationPublisher;
import com.example.micro.publishers.UserPublisher;
import com.example.micro.services.MatchingServiceImpl;
import com.example.micro.utils.FunctionUtils;
import com.example.micro.utils.Pair;
import com.example.micro.utils.TimeSlot;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.checkerframework.checker.nullness.Opt;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
public class MatchingControllerTest {
    MockMvc mockMvc;
    @Mock
    private MatchingServiceImpl matchingServiceImpl;
    @Mock
    private ActivityPublisher activityPublisher;
    @Mock
    private UserPublisher userPublisher;
    @Mock
    private NotificationPublisher notificationPublisher;
    private final ObjectMapper objectMapper = new ObjectMapper();


    private MatchingController matchingController;
    private Matching matching;
    private String userId;
    private Long activityId;
    private List<TimeSlot> timeSlots;

    @BeforeEach
    void setUp() {
        userId = "Vlad";
        activityId = 1L;

        timeSlots = List.of(new TimeSlot(
                LocalDateTime.of(2003, 12, 1, 23, 15),
                LocalDateTime.of(2002, 11, 2, 15, 00)
        ));
        matching = new Matching("Vlad", 1L, "rower", false);
        this.matchingController = new MatchingController(matchingServiceImpl,
                activityPublisher,
                userPublisher,
                notificationPublisher);
        mockMvc = MockMvcBuilders
                .standaloneSetup(matchingController)
                .build();
    }

    @Test
    public void getAvailableActivitiesTest() throws Exception {
        when(matchingServiceImpl.findActivitiesByUserId(userId)).thenReturn(List.of());
        when(activityPublisher.getTimeSlots(List.of())).thenReturn(List.of());
        // when(FunctionUtils.filterTimeSlots(List.of(), List.of())).thenReturn(List.of());
        when(activityPublisher.getAvailableActivities(userId, List.of())).thenReturn(List.of());
        doNothing().when(userPublisher).sendAvailableActivities(List.of()); // mock void method

        MvcResult result = mockMvc
                .perform(post("/getAvailableActivities/Vlad")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of()))
                )
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertThat(content).isEqualTo("[]"); // Empty list
    }

    @Test
    public void chooseActivityFail1() throws Exception {
        when(activityPublisher.check(any(Matching.class))).thenReturn(false);
        MvcResult mvcResult = mockMvc
                .perform(post("/chooseActivity")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(matching))
                )
                .andExpect(status().isBadRequest())
                .andReturn();

        assertThat(mvcResult.getResponse().getContentAsString()).isEmpty(); // no response received
    }

    @Test
    public void chooseActivityFail2() throws Exception {
        when(activityPublisher.check(any(Matching.class))).thenReturn(true);
        when(matchingServiceImpl.findMatchingWithPendingTrue(anyString(), anyLong())).thenReturn(Optional.of(matching));

        MvcResult mvcResult = mockMvc
                .perform(post("/chooseActivity")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(matching))
                )
                .andExpect(status().isBadRequest())
                .andReturn();

        assertThat(mvcResult.getResponse().getContentAsString()).isEmpty(); // no response received
    }

    @Test
    public void chooseActivity() throws Exception {
        Matching savedMatching = new Matching("Niq", 2L, "side", true);

        when(activityPublisher.check(any(Matching.class))).thenReturn(true);
        when(matchingServiceImpl.findMatchingWithPendingTrue(anyString(), anyLong())).thenReturn(Optional.empty());
        lenient().when(matchingServiceImpl.save(any(Matching.class))).thenReturn(savedMatching);
        when(activityPublisher.getOwnerId(anyLong())).thenReturn("dummyString");
        doNothing().when(notificationPublisher).notifyUser(anyString(), anyString(), anyLong(), anyString(), anyString());


        MvcResult mvcResult = mockMvc
                .perform(post("/chooseActivity")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(matching))
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andReturn();

        String contentAsString = mvcResult.getResponse().getContentAsString();
        Matching obtained = objectMapper.readValue(contentAsString, Matching.class);

        assertThat(savedMatching).isEqualTo(obtained);
    }

    @Test
    public void  getUserActivitiesTest() throws Exception {

        when(matchingServiceImpl.findActivitiesByUserId("Vlad")).thenReturn(List.of(1L));

        MvcResult mvcResult = mockMvc
                .perform(get("/getUserActivities/Vlad"))
                .andExpect(status().isOk())
                .andReturn();

        //JSON String representation of List<Long> object.
        String contentAsString = mvcResult.getResponse().getContentAsString();
        assertThat(contentAsString).contains("1");
    }

    @Test
    public void unenrollFail() throws Exception {
        // Empty matching found -> badRequest
        MvcResult mvcResult = mockMvc
                .perform(post("/unenroll"))
                .andExpect(status().isBadRequest())
                .andReturn();

        assertThat(mvcResult.getResponse().getContentAsString()).isEmpty();
    }

    @Test
    public void unenrollTest() throws Exception {
        Pair<String, Long> expected = new Pair<String, Long>(userId, activityId);
        when(matchingServiceImpl.findMatchingWithPendingTrue(userId, activityId))
                .thenReturn(Optional.of(matching));

        MvcResult mvcResult = mockMvc
                .perform(post("/unenroll")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(expected)))
                .andExpect(status().isOk())
                .andReturn();

        String contentAsString = mvcResult.getResponse().getContentAsString();
        Pair<String, Long> obtained = objectMapper.readValue(contentAsString, expected.getClass());
        assertThat(obtained.getFirst()).isEqualTo(expected.getFirst());
    }

    @Test
    public void findAllTest() throws Exception  {
        List<Matching> expected = List.of(matching);
        when(matchingServiceImpl.findAll()).thenReturn(expected);
        MvcResult mvcResult = mockMvc
                .perform(get("/findAll")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(expected))
                )
                .andExpect(status().isOk())
                .andReturn();

        String contentAsString = mvcResult.getResponse().getContentAsString();
        List<Matching> obtained = objectMapper.readValue(contentAsString, List.class);
        assertThat(obtained.toString()).contains("Vlad", "1", "false");
        assertThat(obtained.size()).isEqualTo(1);
    }

    @Test
    public void saveMatchingTest() throws Exception {
        Matching savedMatching = new Matching("Niq", 2L, "side", true);
        when(matchingServiceImpl.save(matching))
                .thenReturn(savedMatching);

        MvcResult mvcResult = mockMvc
                .perform(post("/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(matching))
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Niq")))
                .andReturn();

        String contentAsString = mvcResult.getResponse().getContentAsString();
        Matching obtained = objectMapper.readValue(contentAsString, Matching.class);
        assertThat(obtained).isEqualTo(savedMatching);

    }
}
