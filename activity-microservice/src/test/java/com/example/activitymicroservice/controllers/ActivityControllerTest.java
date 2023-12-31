package com.example.activitymicroservice.controllers;

import com.example.activitymicroservice.authentication.AuthManager;
import com.example.activitymicroservice.domain.Activity;
import com.example.activitymicroservice.domain.Competition;
import com.example.activitymicroservice.domain.Training;
import com.example.activitymicroservice.publishers.MatchingPublisher;
import com.example.activitymicroservice.publishers.UserPublisher;
import com.example.activitymicroservice.services.ActivityService;
import com.example.activitymicroservice.utils.InputValidation;
import com.example.activitymicroservice.utils.Pair;
import com.example.activitymicroservice.utils.TimeSlot;
import com.example.activitymicroservice.validators.CertificateValidator;
import com.example.activitymicroservice.validators.Validator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.checkerframework.checker.units.qual.C;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ActivityControllerTest {
    private MockMvc mockMvc;

    @Mock
    private AuthManager authManager;
    @Mock
    private MatchingPublisher matchingPublisher;
    @Mock
    private UserPublisher userPublisher;
    @Mock
    private ActivityService activityService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private String userId;


    @BeforeEach
    void setUp() {
        ActivityController activityController = new ActivityController(activityService, userPublisher,
                matchingPublisher, authManager);

        mockMvc = MockMvcBuilders
                .standaloneSetup(activityController)
                .build();
    }

    @Test
    void emptyOptional() throws Exception {
        when(activityService.findActivityOptional(1L)).thenReturn(Optional.empty());
        MvcResult res = mockMvc
                .perform(post("/cancelActivity/1")
                )
                .andExpect(status().isNotFound())
                .andReturn();

        assertThat(res.getResponse().getContentAsString()).isEmpty();
    }

    @Test
    void wrongOwner() throws Exception {
        Activity act = new Training();
        act.setOwnerId("Owner");
        when(activityService.findActivityOptional(1L)).thenReturn(Optional.of(act));
        when(authManager.getUserId()).thenReturn("User");
        MvcResult res = mockMvc
                .perform(post("/cancelActivity/1"))
                .andExpect(status().isForbidden())
                .andReturn();

        assertThat(res.getResponse().getContentAsString()).isEmpty();
    }

    @Test
    void matchingsFailedToDelete() throws Exception {
        Activity act = new Training();
        act.setOwnerId("Owner");
        when(activityService.findActivityOptional(1L)).thenReturn(Optional.of(act));
        when(authManager.getUserId()).thenReturn("Owner");
        when(matchingPublisher.deleteMatchingByActivityId(1L)).thenReturn(false);
        MvcResult res = mockMvc
                .perform(post("/cancelActivity/1"))
                .andExpect(status().isInternalServerError())
                .andReturn();

        assertThat(res.getResponse().getContentAsString()).isEmpty();
    }

    @Test
    void success() throws Exception {
        Activity act = new Training();
        act.setOwnerId("Owner");
        when(activityService.findActivityOptional(1L)).thenReturn(Optional.of(act));
        when(authManager.getUserId()).thenReturn("Owner");
        when(matchingPublisher.deleteMatchingByActivityId(1L)).thenReturn(true);
        MvcResult res = mockMvc
                .perform(post("/cancelActivity/1"))
                .andExpect(status().isNoContent())
                .andReturn();

        Activity returned = objectMapper.readValue(res.getResponse().getContentAsString(), Activity.class);
        assertThat(returned).isEqualTo(act);
    }

    @Test
    void getOwnerIdSuccess() throws Exception {
        Activity activity = new Training();
        activity.setOwnerId("Razvan");
        activity.setActivityId(1L);
        when(activityService.findActivityOptional(activity.getActivityId())).thenReturn(Optional.of(activity));
        MvcResult res = mockMvc
                .perform(get("/sendOwnerId/1"))
                .andExpect(status().isOk())
                .andReturn();
        assertThat(res.getResponse().getContentAsString()).isEqualTo("Razvan");
    }

    @Test
    void getOwnerIdFail() throws Exception {
        Activity activity = new Training();
        activity.setOwnerId("Razvan");
        activity.setActivityId(1L);
        when(activityService.findActivityOptional(2L)).thenReturn(Optional.empty());
        MvcResult res = mockMvc
                .perform(get("/sendOwnerId/2"))
                .andExpect(status().isNotFound())
                .andReturn();
        assertThat(res.getResponse().getContentAsString()).isEmpty();
    }

    @Test
    void getTimeSlotsSuccess() throws Exception {
        TimeSlot timeSlot1 = new TimeSlot(
                LocalDateTime.of(2003, 12, 1, 23, 15),
                LocalDateTime.of(2002, 11, 2, 15, 0));
        Activity activity1 = new Training();
        activity1.setActivityId(1L);
        activity1.setTimeSlot(timeSlot1);

        TimeSlot timeSlot2 = new TimeSlot(
                LocalDateTime.of(2004, 12, 1, 23, 15),
                LocalDateTime.of(2002, 11, 2, 15, 0));
        Activity activity2 = new Training();
        activity2.setActivityId(2L);
        activity2.setTimeSlot(timeSlot2);

        when(activityService.getTimeSlotsByActivityIds(List.of(1L, 2L))).thenReturn(List.of(timeSlot1, timeSlot2));
        MvcResult res = mockMvc
                .perform(post("/sendTimeSlots")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of(1L, 2L))))
                .andExpect(status().isOk())
                .andReturn();
        String contentAsString = res.getResponse().getContentAsString();
        List<TimeSlot> obtained = objectMapper.readValue(contentAsString, new TypeReference<List<TimeSlot>>() {
        });
        assertThat(obtained.size()).isEqualTo(2);
        assertThat(obtained.get(0)).isEqualTo(timeSlot1);
        assertThat(obtained.get(1)).isEqualTo(timeSlot2);
    }

    @Test
    void getTimeSlotsFail() throws Exception {
        TimeSlot timeSlot1 = new TimeSlot(
                LocalDateTime.of(2003, 12, 1, 23, 15),
                LocalDateTime.of(2002, 11, 2, 15, 0));
        Activity activity1 = new Training();
        activity1.setActivityId(1L);
        activity1.setTimeSlot(timeSlot1);


        when(activityService.getTimeSlotsByActivityIds(List.of(1L, 2L))).thenThrow(new Exception());
        MvcResult res = mockMvc
                .perform(post("/sendTimeSlots")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of(1L, 2L))))
                .andExpect(status().isBadRequest())
                .andReturn();
        assertThat(res.getResponse().getContentAsString()).isEmpty();
    }

    @Test
    void takeAvailableSpotSuccess() throws Exception {
        Activity activity = new Training();
        activity.setActivityId(1L);
        activity.setPositions(List.of("cox", "rower"));
        Pair<Long, String> pair = new Pair<>(1L, "cox");
        when(activityService.takeSpot(pair)).thenReturn(true);
        MvcResult res = mockMvc
                .perform(post("/takeAvailableSpot")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pair)))
                .andExpect(status().isOk())
                .andReturn();

        assertThat(res.getResponse().getContentAsString()).isEqualTo("cox");
    }

    @Test
    void takeAvailableSpotFail() throws Exception {
        Activity activity = new Training();
        activity.setActivityId(1L);
        activity.setPositions(List.of("cox", "rower"));
        Pair<Long, String> pair = new Pair<>(2L, "cox");
        when(activityService.takeSpot(pair)).thenThrow(new Exception());
        MvcResult res = mockMvc
                .perform(post("/takeAvailableSpot")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pair)))
                .andExpect(status().isBadRequest())
                .andReturn();
        assertThat(res.getResponse().getContentAsString()).isEqualTo("");
    }

    @Test
    void checkWrongPositions() throws Exception {
        Activity activity = new Training();
        activity.setActivityId(1L);
        activity.setPositions(List.of("cox", "rower"));
        when(activityService.findActivityOptional(1L)).thenReturn(Optional.of(activity));
        MvcResult res = mockMvc
                .perform(get("/check/Razvan/1/SideRower"))
                .andExpect(status().isOk())
                .andReturn();
        String contentAsString = res.getResponse().getContentAsString();
        boolean obtained = objectMapper.readValue(contentAsString, boolean.class);
        assertThat(obtained).isFalse();
    }

    @Test
    void checkNoSuchActivity() throws Exception {
        when(activityService.findActivityOptional(1L)).thenReturn(Optional.empty());
        MvcResult res = mockMvc
                .perform(get("/check/Razvan/1/SideRower"))
                .andExpect(status().isNotFound())
                .andReturn();
        assertThat(res.getResponse().getContentAsString()).isEmpty();
    }

    @Test
    void checkTimeSlotNotGood() throws Exception {
        Activity activity = new Training();
        activity.setActivityId(1L);
        activity.setPositions(List.of("cox", "rower"));
        List<TimeSlot> timeSlotSet = new ArrayList<>();
        TimeSlot timeSlot = new TimeSlot(LocalDateTime.of(2001, 12, 1, 23, 15),
                LocalDateTime.of(2002, 11, 2, 15, 00));
        TimeSlot timeSlot1 = new TimeSlot(LocalDateTime.of(2004, 12, 1, 23, 15),
                LocalDateTime.of(2006, 11, 2, 15, 00));
        timeSlotSet.add(timeSlot1);
        TimeSlot timeSlot2 = new TimeSlot(LocalDateTime.of(1999, 12, 1, 23, 15),
                LocalDateTime.of(2000, 11, 2, 15, 00));
        timeSlotSet.add(timeSlot2);
        activity.setTimeSlot(timeSlot);
        userId = "Razvan";
        when(activityService.findActivityOptional(1L)).thenReturn(Optional.of(activity));
        when(userPublisher.getTimeslots(userId)).thenReturn(timeSlotSet);
        when(activityService.getActivitiesByTimeSlot(List.of(activity), userPublisher.getTimeslots(userId)))
                .thenReturn(List.of());
        MvcResult res = mockMvc
                .perform(get("/check/Razvan/1/rower"))
                .andExpect(status().isOk())
                .andReturn();
        String contentAsString = res.getResponse().getContentAsString();
        boolean obtained = objectMapper.readValue(contentAsString, boolean.class);
        assertThat(obtained).isFalse();
    }

    @Test
    void checkTrainingCoxSuccess() throws Exception {
        Activity activity = new Training();
        activity.setActivityId(1L);
        activity.setPositions(List.of("cox", "rower"));
        List<TimeSlot> timeSlotSet = new ArrayList<>();
        TimeSlot timeSlot = new TimeSlot(LocalDateTime.of(2001, 12, 1, 23, 15),
                LocalDateTime.of(2002, 11, 2, 15, 00));
        TimeSlot timeSlot1 = new TimeSlot(LocalDateTime.of(2004, 12, 1, 23, 15),
                LocalDateTime.of(2006, 11, 2, 15, 00));
        timeSlotSet.add(timeSlot1);
        TimeSlot timeSlot2 = new TimeSlot(LocalDateTime.of(1999, 12, 1, 23, 15),
                LocalDateTime.of(2000, 11, 2, 15, 00));
        timeSlotSet.add(timeSlot2);
        activity.setTimeSlot(timeSlot);
        activity.setCertificate("C4");
        userId = "Razvan";
        when(activityService.findActivityOptional(1L)).thenReturn(Optional.of(activity));
        when(userPublisher.getTimeslots(userId)).thenReturn(timeSlotSet);
        when(activityService.getActivitiesByTimeSlot(List.of(activity), userPublisher.getTimeslots(userId)))
                .thenReturn(List.of(activity));
        lenient().when(userPublisher.getCertificate("Razvan")).thenReturn("C4");
        lenient().when(userPublisher.getGender("Razvan")).thenReturn('M');
        lenient().when(userPublisher.getCompetitiveness("Razvan")).thenReturn(true);
        lenient().when(userPublisher.getOrganisation("Razvan")).thenReturn("33a");
        lenient().when(userPublisher.getPositions("Razvan")).thenReturn(Set.of("cox", "rower"));
        MvcResult res = mockMvc
                .perform(get("/check/Razvan/1/cox"))
                .andExpect(status().isOk())
                .andReturn();
        String contentAsString = res.getResponse().getContentAsString();
        boolean obtained = objectMapper.readValue(contentAsString, boolean.class);
        assertThat(obtained).isTrue();
    }

    @Test
    void checkTrainingSuccess() throws Exception {
        Activity activity = new Training();
        activity.setActivityId(1L);
        activity.setPositions(List.of("cox", "rower"));
        List<TimeSlot> timeSlotSet = new ArrayList<>();
        TimeSlot timeSlot = new TimeSlot(LocalDateTime.of(2001, 12, 1, 23, 15),
                LocalDateTime.of(2002, 11, 2, 15, 00));
        TimeSlot timeSlot1 = new TimeSlot(LocalDateTime.of(2004, 12, 1, 23, 15),
                LocalDateTime.of(2006, 11, 2, 15, 00));
        timeSlotSet.add(timeSlot1);
        TimeSlot timeSlot2 = new TimeSlot(LocalDateTime.of(1999, 12, 1, 23, 15),
                LocalDateTime.of(2000, 11, 2, 15, 00));
        timeSlotSet.add(timeSlot2);
        activity.setTimeSlot(timeSlot);
        userId = "Razvan";
        when(activityService.findActivityOptional(1L)).thenReturn(Optional.of(activity));
        when(userPublisher.getTimeslots(userId)).thenReturn(timeSlotSet);
        when(activityService.getActivitiesByTimeSlot(List.of(activity), userPublisher.getTimeslots(userId)))
                .thenReturn(List.of(activity));
        lenient().when(userPublisher.getGender("Razvan")).thenReturn('M');
        lenient().when(userPublisher.getCompetitiveness("Razvan")).thenReturn(true);
        lenient().when(userPublisher.getOrganisation("Razvan")).thenReturn("33a");
        lenient().when(userPublisher.getPositions("Razvan")).thenReturn(Set.of("cox", "rower"));
        MvcResult res = mockMvc
                .perform(get("/check/Razvan/1/rower"))
                .andExpect(status().isOk())
                .andReturn();
        String contentAsString = res.getResponse().getContentAsString();
        boolean obtained = objectMapper.readValue(contentAsString, boolean.class);
        assertThat(obtained).isTrue();
    }

    @Test
    void checkCompetitionSuccess() throws Exception {
        Activity activity = new Competition();
        activity.setActivityId(1L);
        activity.setPositions(List.of("cox", "rower"));
        List<TimeSlot> timeSlotSet = new ArrayList<>();
        TimeSlot timeSlot = new TimeSlot(LocalDateTime.of(2001, 12, 1, 23, 15),
                LocalDateTime.of(2002, 11, 2, 15, 00));
        TimeSlot timeSlot1 = new TimeSlot(LocalDateTime.of(2004, 12, 1, 23, 15),
                LocalDateTime.of(2006, 11, 2, 15, 00));
        timeSlotSet.add(timeSlot1);
        TimeSlot timeSlot2 = new TimeSlot(LocalDateTime.of(1999, 12, 1, 23, 15),
                LocalDateTime.of(2000, 11, 2, 15, 00));
        timeSlotSet.add(timeSlot2);
        activity.setTimeSlot(timeSlot);
        ((Competition) activity).setGender('M');
        ((Competition) activity).setCompetitive(true);
        ((Competition) activity).setOrganisation("33a");
        userId = "Razvan";
        when(activityService.findActivityOptional(1L)).thenReturn(Optional.of(activity));
        when(userPublisher.getTimeslots(userId)).thenReturn(timeSlotSet);
        when(activityService.getActivitiesByTimeSlot(List.of(activity), userPublisher.getTimeslots(userId)))
                .thenReturn(List.of(activity));
        lenient().when(userPublisher.getGender("Razvan")).thenReturn('M');
        lenient().when(userPublisher.getCompetitiveness("Razvan")).thenReturn(true);
        lenient().when(userPublisher.getOrganisation("Razvan")).thenReturn("33a");
        lenient().when(userPublisher.getPositions("Razvan")).thenReturn(Set.of("cox", "rower"));
        MvcResult res = mockMvc
                .perform(get("/check/Razvan/1/rower"))
                .andExpect(status().isOk())
                .andReturn();
        String contentAsString = res.getResponse().getContentAsString();
        boolean obtained = objectMapper.readValue(contentAsString, boolean.class);
        assertThat(obtained).isTrue();
    }

    @Test
    void checkCompetitionCoxSuccess() throws Exception {
        Activity activity = new Competition();
        activity.setActivityId(1L);
        activity.setPositions(List.of("cox", "rower"));
        List<TimeSlot> timeSlotSet = new ArrayList<>();
        TimeSlot timeSlot = new TimeSlot(LocalDateTime.of(2001, 12, 1, 23, 15),
                LocalDateTime.of(2002, 11, 2, 15, 00));
        TimeSlot timeSlot1 = new TimeSlot(LocalDateTime.of(2004, 12, 1, 23, 15),
                LocalDateTime.of(2006, 11, 2, 15, 00));
        timeSlotSet.add(timeSlot1);
        TimeSlot timeSlot2 = new TimeSlot(LocalDateTime.of(1999, 12, 1, 23, 15),
                LocalDateTime.of(2000, 11, 2, 15, 00));
        timeSlotSet.add(timeSlot2);
        activity.setTimeSlot(timeSlot);
        activity.setCertificate("C4");
        ((Competition) activity).setGender('M');
        ((Competition) activity).setCompetitive(true);
        ((Competition) activity).setOrganisation("33a");
        userId = "Razvan";
        when(activityService.findActivityOptional(1L)).thenReturn(Optional.of(activity));
        when(userPublisher.getTimeslots(userId)).thenReturn(timeSlotSet);
        when(activityService.getActivitiesByTimeSlot(List.of(activity), userPublisher.getTimeslots(userId)))
                .thenReturn(List.of(activity));
        lenient().when(userPublisher.getCertificate("Razvan")).thenReturn("C4");
        lenient().when(userPublisher.getGender("Razvan")).thenReturn('M');
        lenient().when(userPublisher.getCompetitiveness("Razvan")).thenReturn(true);
        lenient().when(userPublisher.getOrganisation("Razvan")).thenReturn("33a");
        lenient().when(userPublisher.getPositions("Razvan")).thenReturn(Set.of("cox", "rower"));
        MvcResult res = mockMvc
                .perform(get("/check/Razvan/1/cox"))
                .andExpect(status().isOk())
                .andReturn();
        String contentAsString = res.getResponse().getContentAsString();
        boolean obtained = objectMapper.readValue(contentAsString, boolean.class);
        assertThat(obtained).isTrue();
    }

    @Test
    void createActivityGood() throws Exception {
        Activity activity = new Training();
        activity.setOwnerId("Razvan");
        activity.setTimeSlot(new TimeSlot(LocalDateTime.of(2001, 12, 1, 23, 15),
                LocalDateTime.of(2002, 11, 2, 15, 00)));
        activity.setPositions(List.of("cox"));
        activity.setCertificate("C4");

        when(activityService.save(activity)).thenReturn(activity);
        MvcResult res = mockMvc
                .perform(post("/createActivity")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(activity)))
                .andExpect(status().isOk())
                .andReturn();
        String contentAsString = res.getResponse().getContentAsString();
        Activity obtained = objectMapper.readValue(contentAsString, Activity.class);
        assertThat(obtained).isEqualTo(activity);
        assertThat(InputValidation.validatePositions(activity.getPositions())).isTrue();
    }

    @Test
    void createActivityFail() throws Exception {
        Activity activity = new Training();
        activity.setPositions(List.of("Rower"));
        lenient().when(activityService.save(activity)).thenReturn(activity);
        MvcResult res = mockMvc
                .perform(post("/createActivity")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(activity)))
                .andExpect(status().isBadRequest())
                .andReturn();
        assertThat(res.getResponse().getContentAsString()).isEmpty();
        assertThat(InputValidation.validatePositions(activity.getPositions())).isFalse();
    }

    @Test
    void inputValidationTestGood() {
        List<String> positions = List.of("cox", "coach");
        assertThat(InputValidation.validatePositions(positions)).isTrue();
    }

    @Test
    void inputValidationTestBad() {
        List<String> positions = List.of("cox", "oach");
        assertThat(InputValidation.validatePositions(positions)).isFalse();
    }

    @Test
    void findAllTest() throws Exception {
        Activity activity1 = new Training();
        Activity activity2 = new Competition();
        List<Activity> activityList = List.of(activity1, activity2);
        lenient().when(activityService.save(activity1)).thenReturn(activity1);
        lenient().when(activityService.save(activity2)).thenReturn(activity2);
        when(activityService.findAll()).thenReturn(activityList);
        MvcResult res = mockMvc
                .perform(get("/findAll"))
                .andExpect(status().isOk())
                .andReturn();
        String contentAsString = res.getResponse().getContentAsString();
        List<Activity> obtained = objectMapper.readValue(contentAsString, new TypeReference<List<Activity>>() {
        });
        assertThat(obtained).isEqualTo(activityList);
    }

    @Test
    void editActivitySuccessfully() throws Exception {
        Activity activity = new Training();
        activity.setActivityId(1L);
        activity.setOwnerId("Razvan");
        Activity activity1 = new Training();
        activity1.setOwnerId("Razvan");
        activity1.setTimeSlot(new TimeSlot(LocalDateTime.of(2001, 12, 1, 23, 15),
                LocalDateTime.of(2002, 11, 2, 15, 00)));
        activity1.setPositions(List.of("cox"));
        activity1.setCertificate("C4");
        when(activityService.findActivityOptional(1L)).thenReturn(Optional.of(activity));
        when(authManager.getUserId()).thenReturn("Razvan");
        when(matchingPublisher.deleteMatchingByActivityId(1L)).thenReturn(true);
        when(activityService.editActivityService(activity, activity1)).thenReturn(true);
        mockMvc.perform(post("/editActivity/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(activity1)))
                .andExpect(status().isOk());
    }

    @Test
    void editActivityFailNoActivity() throws Exception {
        Activity activity = new Training();
        activity.setOwnerId("Razvan");
        activity.setTimeSlot(new TimeSlot(LocalDateTime.of(2001, 12, 1, 23, 15),
                LocalDateTime.of(2002, 11, 2, 15, 00)));
        activity.setPositions(List.of("cox"));
        activity.setCertificate("C4");
        when(activityService.findActivityOptional(1L)).thenReturn(Optional.empty());
        mockMvc.perform(post("/editActivity/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(activity)))
                .andExpect(status().isNotFound());
    }

    @Test
    void editActivityFailWrongUser() throws Exception {
        Activity activity = new Training();
        activity.setOwnerId("Razvan");
        activity.setTimeSlot(new TimeSlot(LocalDateTime.of(2001, 12, 1, 23, 15),
                LocalDateTime.of(2002, 11, 2, 15, 00)));
        activity.setPositions(List.of("cox"));
        activity.setCertificate("C4");

        Activity activity1 = new Training();
        activity1.setActivityId(1L);
        activity1.setOwnerId("Razvan");
        when(activityService.findActivityOptional(1L)).thenReturn(Optional.of(activity1));
        when(authManager.getUserId()).thenReturn("NotRazvan");
        mockMvc.perform(post("/editActivity/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(activity)))
                .andExpect(status().isForbidden());
    }

    @Test
    void editActivityFailMatchingPublisherError() throws Exception {
        Activity activity = new Training();
        activity.setOwnerId("Razvan");
        activity.setTimeSlot(new TimeSlot(LocalDateTime.of(2001, 12, 1, 23, 15),
                LocalDateTime.of(2002, 11, 2, 15, 00)));
        activity.setPositions(List.of("cox"));
        activity.setCertificate("C4");

        Activity activity1 = new Training();
        activity1.setActivityId(1L);
        activity1.setOwnerId("Razvan");
        when(activityService.findActivityOptional(1L)).thenReturn(Optional.of(activity1));
        when(authManager.getUserId()).thenReturn("Razvan");
        when(matchingPublisher.deleteMatchingByActivityId(1L)).thenReturn(false);
        mockMvc.perform(post("/editActivity/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(activity)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void editActivityFailActivityServiceError() throws Exception {
        Activity activity = new Training();
        activity.setOwnerId("Razvan");
        activity.setTimeSlot(new TimeSlot(LocalDateTime.of(2001, 12, 1, 23, 15),
                LocalDateTime.of(2002, 11, 2, 15, 00)));
        activity.setPositions(List.of("cox"));
        activity.setCertificate("C4");

        Activity activity1 = new Training();
        activity1.setActivityId(1L);
        activity1.setOwnerId("Razvan");
        when(activityService.findActivityOptional(1L)).thenReturn(Optional.of(activity1));
        when(authManager.getUserId()).thenReturn("Razvan");
        when(matchingPublisher.deleteMatchingByActivityId(1L)).thenReturn(true);
        when(activityService.editActivityService(activity1, activity)).thenReturn(false);
        mockMvc.perform(post("/editActivity/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(activity)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void sendAvailableActivitiesEmptyTest() throws Exception {
        when(activityService.getActivitiesByTimeSlot(any(), any())).thenReturn(List.of());
        List<TimeSlot> timeSlots = new ArrayList<>();

        MvcResult res = mockMvc
                .perform(post("/sendAvailableActivities/Razvan")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(timeSlots)))
                .andExpect(status().isOk())
                .andReturn();
        String contentAsString = res.getResponse().getContentAsString();
        List<Pair<Long, String>> obtained = objectMapper.readValue(contentAsString, List.class);
        assertThat(obtained).isEqualTo(List.of());
    }

    @Test
    void sendAvailableActivitiesLargeTest() throws Exception {

        Activity training = new Training();
        training.setActivityId(1L);
        training.setOwnerId("Vlad");
        training.setTimeSlot(new TimeSlot(LocalDateTime.of(2022, 10, 10, 10, 10),
                LocalDateTime.of(2023, 10, 10, 10, 10)));
        training.setCertificate("C4");
        training.setPositions(List.of("cox"));
        Activity competition = new Competition();
        competition.setActivityId(1L);
        competition.setOwnerId("Vlad");
        competition.setTimeSlot(new TimeSlot(LocalDateTime.of(2022, 10, 10, 10, 10),
                LocalDateTime.of(2023, 10, 10, 10, 10)));
        competition.setCertificate("C4");
        competition.setPositions(List.of("cox"));

        Activity competition1 = new Competition();
        competition1.setActivityId(1L);
        competition1.setOwnerId("Vlad");
        competition1.setTimeSlot(new TimeSlot(LocalDateTime.of(2022, 10, 10, 10, 10),
                LocalDateTime.of(2023, 10, 10, 10, 10)));
        competition1.setCertificate("C4");
        competition1.setPositions(List.of("coach"));

        Activity training1 = new Training();
        training1.setActivityId(1L);
        training1.setOwnerId("Vlad");
        training1.setTimeSlot(new TimeSlot(LocalDateTime.of(2022, 10, 10, 10, 10),
                LocalDateTime.of(2023, 10, 10, 10, 10)));
        training1.setCertificate("C4");
        training1.setPositions(List.of("coach"));

        List<Activity> acitvityList = new ArrayList<>();
        acitvityList.add(training);
        acitvityList.add(competition);
        acitvityList.add(training1);
        acitvityList.add(competition1);

        when(activityService.getActivitiesByTimeSlot(any(), any())).thenReturn(acitvityList);

        List<TimeSlot> timeSlots = new ArrayList<>();
        MvcResult res = mockMvc
                .perform(post("/sendAvailableActivities/Razvan")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(timeSlots)))
                .andExpect(status().isOk())
                .andReturn();
        String contentAsString = res.getResponse().getContentAsString();
        List<Pair<Long, String>> obtained = objectMapper.readValue(contentAsString, List.class);
        assertThat(obtained.size()).isEqualTo(0);
    }


}