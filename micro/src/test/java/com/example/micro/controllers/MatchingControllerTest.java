package com.example.micro.controllers;

import com.example.micro.authentication.AuthManager;
import com.example.micro.domain.Matching;
import com.example.micro.publishers.ActivityPublisher;
import com.example.micro.publishers.CollectionPublisher;
import com.example.micro.publishers.NotificationPublisher;
import com.example.micro.services.MatchingServiceImpl;
import com.example.micro.utils.BaseNotification;
import com.example.micro.utils.Pair;
import com.example.micro.utils.TimeSlot;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class MatchingControllerTest {
    MockMvc mockMvc;
    @Mock
    private MatchingServiceImpl matchingServiceImpl;
    @Mock
    private ActivityPublisher activityPublisher;
    @Mock
    private NotificationPublisher notificationPublisher;
    @Mock
    private AuthManager authManager;
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
                new CollectionPublisher(activityPublisher, notificationPublisher),
                authManager);
        mockMvc = MockMvcBuilders
                .standaloneSetup(matchingController)
                .build();
    }

    @Test
    public void getAvailableActivitiesTest() throws Exception {
        when(authManager.getUserId()).thenReturn(userId);
        when(matchingServiceImpl.findActivitiesByUserId(userId)).thenReturn(List.of());
        when(activityPublisher.getTimeSlots(List.of())).thenReturn(List.of());
        // when(FunctionUtils.filterTimeSlots(List.of(), List.of())).thenReturn(List.of());
        when(activityPublisher.getAvailableActivities(userId, List.of())).thenReturn(List.of());

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
    void getAvailableActivitiesUnauthorizedTest() throws Exception {

        lenient().when(authManager.getUserId()).thenReturn("Niq");
        lenient().when(matchingServiceImpl.findActivitiesByUserId(userId)).thenReturn(List.of());
        lenient().when(activityPublisher.getTimeSlots(List.of())).thenReturn(List.of());
        // when(FunctionUtils.filterTimeSlots(List.of(), List.of())).thenReturn(List.of());
        lenient().when(activityPublisher.getAvailableActivities(userId, List.of())).thenReturn(List.of());

        MvcResult result = mockMvc
                .perform(post("/getAvailableActivities/Vlad")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of()))
                )
                .andExpect(status().isUnauthorized())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertThat(content).isEqualTo(""); // Blank

    }

    @Test
    public void chooseActivityFail1() throws Exception {
        when(authManager.getUserId()).thenReturn(userId);
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
        when(authManager.getUserId()).thenReturn(userId);
        when(activityPublisher.check(any(Matching.class))).thenReturn(true);
        when(matchingServiceImpl.findMatchingWithPendingFalse(anyString(), anyLong())).thenReturn(Optional.of(matching));

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
    public void chooseActivityUnauthorizedTest() throws Exception {

        lenient()
                .when(authManager.getUserId()).thenReturn("Niq");
        lenient()
                .when(activityPublisher.check(any(Matching.class))).thenReturn(true);
        lenient()
                .when(matchingServiceImpl
                        .findMatchingWithPendingFalse(anyString(), anyLong()))
                .thenReturn(Optional.of(matching));

        MvcResult mvcResult = mockMvc
                .perform(post("/chooseActivity")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(matching))
                )
                .andExpect(status().isUnauthorized())
                .andReturn();

        assertThat(mvcResult.getResponse().getContentAsString()).isEmpty(); // no response received

    }

    @Test
    public void chooseActivity() throws Exception {
        Matching savedMatching = new Matching("Niq", 2L, "side", true);

        when(authManager.getUserId()).thenReturn(userId);
        when(activityPublisher.check(any(Matching.class))).thenReturn(true);
        when(matchingServiceImpl.findMatchingWithPendingFalse(anyString(), anyLong())).thenReturn(Optional.empty());
        lenient().when(matchingServiceImpl.save(any(Matching.class))).thenReturn(savedMatching);
        when(activityPublisher.getOwnerId(anyLong())).thenReturn("dummyString");
        doNothing().when(notificationPublisher).notifyUser(any(BaseNotification.class));


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
    public void getUserActivitiesTest() throws Exception {

        when(matchingServiceImpl.findActivitiesByUserId("Vlad")).thenReturn(List.of(1L));
        lenient().when(authManager.getUserId()).thenReturn("Vlad");
        MvcResult mvcResult = mockMvc
                .perform(get("/getUserActivities/Vlad"))
                .andExpect(status().isOk())
                .andReturn();

        //JSON String representation of List<Long> object.
        String contentAsString = mvcResult.getResponse().getContentAsString();
        assertThat(contentAsString).contains("1");
    }

    @Test
    public void getUserActivitiesTestFails() throws Exception {
        lenient().when(authManager.getUserId()).thenReturn("Niq");
        MvcResult mvcResult = mockMvc
                .perform(get("/getUserActivities/Vlad"))
                .andExpect(status().isUnauthorized())
                .andReturn();

        //JSON String representation of List<Long> object.
        String contentAsString = mvcResult.getResponse().getContentAsString();
        assertThat(contentAsString).contains("");
    }

    @Test
    public void unenrollFail() throws Exception {
        // Empty matching found -> badRequest
        Pair<String, Long> expected = new Pair<String, Long>(userId, activityId);
        lenient().when(authManager.getUserId()).thenReturn(userId);
        when(matchingServiceImpl.findMatchingWithPendingFalse(userId, activityId))
                .thenReturn(Optional.empty());
        MvcResult mvcResult = mockMvc
                .perform(post("/unenroll")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(expected)))
                .andExpect(status().isBadRequest())
                .andReturn();

        assertThat(mvcResult.getResponse().getContentAsString()).isEmpty();
    }

    @Test
    public void unenrollUnauthorizedTest() throws Exception {
        Pair<String, Long> expected = new Pair<String, Long>(userId, activityId);
        lenient().when(authManager.getUserId()).thenReturn("Niq");
        lenient().when(matchingServiceImpl.findMatchingWithPendingFalse(userId, activityId))
                .thenReturn(Optional.empty());
        MvcResult mvcResult = mockMvc
                .perform(post("/unenroll")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(expected)))
                .andExpect(status().isUnauthorized())
                .andReturn();

        assertThat(mvcResult.getResponse().getContentAsString()).isEmpty();
    }

    @Test
    public void unenrollTest() throws Exception {
        Pair<String, Long> expected = new Pair<String, Long>(userId, activityId);
        lenient().when(authManager.getUserId()).thenReturn(userId);
        when(matchingServiceImpl.findMatchingWithPendingFalse(userId, activityId))
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
    void deleteMatchingByActivityIdUnauthorizedTest() throws Exception {
        lenient().when(authManager.getUserId()).thenReturn("Niq");
        lenient().when(activityPublisher.getOwnerId(1L)).thenReturn("Vlad");

        MvcResult mvcResult = mockMvc
                .perform(post("/deleteMatchingByActivityId/1"))
                .andExpect(status().isUnauthorized())
                .andReturn();

        verify(matchingServiceImpl, never()).deleteByActivityId(activityId);
    }

    @Test
    void deleteMatchingByActivityIdTest() throws Exception {
        Long activityId = 1L;
        lenient().when(authManager.getUserId()).thenReturn("Vlad");
        lenient().when(activityPublisher.getOwnerId(1L)).thenReturn("Vlad");

        MvcResult mvcResult = mockMvc
                .perform(post("/deleteMatchingByActivityId/1"))
                .andExpect(status().isNoContent())
                .andReturn();

        lenient().doNothing().when(matchingServiceImpl).deleteByActivityId(activityId);
    }

    @Test
    public void findAllTest() throws Exception {
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

    @Test
    public void decideMatchFails1() throws Exception {

        String ownerId = "Vlad";
        lenient().when(activityPublisher.getOwnerId(matching.getActivityId())).thenReturn(ownerId);
        lenient().when(authManager.getUserId()).thenReturn("Niq");
        MvcResult mvcResult = mockMvc
                .perform(post("/decideMatch/Vlad/accept")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(matching)))
                .andExpect(status().isUnauthorized())
                .andReturn();

        String contentAsString = mvcResult.getResponse().getContentAsString();
        assertThat(contentAsString).isEqualTo(""); // no response

        verify(matchingServiceImpl, never()).deleteById(anyString(), anyLong(), anyString());
    }

    @Test
    public void decideMatchFails2() throws Exception {

        String ownerId = "Vlad";
        lenient().when(activityPublisher.getOwnerId(matching.getActivityId())).thenReturn("Radu");
        lenient().when(authManager.getUserId()).thenReturn(ownerId);
        MvcResult mvcResult = mockMvc
                .perform(post("/decideMatch/Vlad/accept")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(matching)))
                .andExpect(status().isUnauthorized())
                .andReturn();

        String contentAsString = mvcResult.getResponse().getContentAsString();
        assertThat(contentAsString).isEqualTo(""); // no response

        verify(matchingServiceImpl, never()).deleteById(anyString(), anyLong(), anyString());
    }

    @Test
    public void decideMatchFails3() throws Exception {
        String ownerId = "Vlad";
        lenient().when(activityPublisher.getOwnerId(matching.getActivityId())).thenReturn(ownerId);
        lenient().when(authManager.getUserId()).thenReturn(ownerId);
        lenient().when(matchingServiceImpl.checkId(matching.getUserId(),
                matching.getActivityId(),
                matching.getPosition())).thenReturn(false);
        MvcResult mvcResult = mockMvc
                .perform(post("/decideMatch/Vlad/accept")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(matching)))
                .andExpect(status().isBadRequest())
                .andReturn();

        String contentAsString = mvcResult.getResponse().getContentAsString();
        assertThat(contentAsString).isEqualTo(""); // no response

        verify(matchingServiceImpl, never()).deleteById(anyString(), anyLong(), anyString());
    }

    @Test
    public void decideMatchFails4() throws Exception {
        String ownerId = "Vlad";
        lenient().when(activityPublisher.getOwnerId(matching.getActivityId())).thenReturn(ownerId);
        lenient().when(authManager.getUserId()).thenReturn(ownerId);
        lenient().when(matchingServiceImpl.checkId(matching.getUserId(),
                matching.getActivityId(),
                matching.getPosition())).thenReturn(true);
        MvcResult mvcResult = mockMvc
                .perform(post("/decideMatch/Vlad/lol")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(matching)))
                .andExpect(status().isBadRequest())
                .andReturn();

        String contentAsString = mvcResult.getResponse().getContentAsString();
        assertThat(contentAsString).isEqualTo(""); // no response

        verify(matchingServiceImpl, never()).deleteById(anyString(), anyLong(), anyString());
    }

    @Test
    public void decideMatchAcceptPasses() throws Exception {
        String ownerId = "Radu";
        lenient()
                .when(activityPublisher.getOwnerId(matching.getActivityId())).thenReturn(ownerId);
        lenient()
                .when(matchingServiceImpl.checkId(anyString(), anyLong(), anyString())).thenReturn(true);
        lenient().when(authManager.getUserId()).thenReturn(ownerId);
        when(matchingServiceImpl.save(matching)).thenReturn(matching);

        doNothing().when(matchingServiceImpl).deleteById(anyString(), anyLong(), anyString());
        doNothing().when(activityPublisher).takeAvailableSpot(anyLong(), anyString());
        doNothing().when(notificationPublisher).notifyUser(any(BaseNotification.class));


        MvcResult mvcResult = mockMvc
                .perform(post("/decideMatch/Radu/accept")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(matching)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andReturn();

        String contentAsString = mvcResult.getResponse().getContentAsString();
        Matching obtained = objectMapper.readValue(contentAsString, Matching.class);
        assertThat(obtained).isEqualTo(matching);
    }

    @Test
    public void decideMatchDeclinePass() throws Exception {

        String ownerId = "Radu";
        lenient()
                .when(activityPublisher.getOwnerId(matching.getActivityId())).thenReturn(ownerId);
        lenient()
                .when(matchingServiceImpl.checkId(anyString(), anyLong(), anyString())).thenReturn(true);
        lenient().when(authManager.getUserId()).thenReturn(ownerId);
        doNothing().when(matchingServiceImpl).deleteById(anyString(), anyLong(), anyString());


        MvcResult mvcResult = mockMvc
                .perform(post("/decideMatch/Radu/decline")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(matching)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andReturn();

        matching.setPending(true);

        String contentAsString = mvcResult.getResponse().getContentAsString();
        Matching obtained = objectMapper.readValue(contentAsString, Matching.class);
        assertThat(obtained).isEqualTo(matching);
    }

    @Test
    void validationTest() throws Exception {
        Matching matchingInvalid = new Matching("", null, "", false);
        MvcResult mvcResult = mockMvc
                .perform(post("/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(matchingInvalid))
                )
                .andExpect(status().isBadRequest())
                .andReturn();

        String contentAsString = mvcResult.getResponse().getContentAsString();
        assertThat(contentAsString).contains("userId is mandatory, thus it cannot be blank.");
        assertThat(contentAsString).contains("activityId is mandatory, thus it cannot be null.");
        assertThat(contentAsString).contains("position is mandatory, thus it cannot be blank.");
    }

    @Test
    void addTimeSlotsTest() throws Exception {

        MvcResult result = mockMvc
                .perform(post("/addTimeSlots/Vlad")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of(
                                new TimeSlot(LocalDateTime.of(2003, 12, 22, 12, 12),
                                        LocalDateTime.of(2003, 12, 22, 14, 12)),
                                new TimeSlot(LocalDateTime.of(2004, 12, 22, 12, 12),
                                        LocalDateTime.of(2005, 12, 22, 14, 12))
                        )))
                )
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertThat(content).contains(
                "start", "end",
                "2003-12-22 12:12", "2003-12-22 14:12",
                "2004-12-22 12:12", "2005-12-22 14:12"
        ); // List of deserialized LocalDateTimes as Strings
    }

}
