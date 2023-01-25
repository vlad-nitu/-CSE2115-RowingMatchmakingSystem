package com.example.activitymicroservice.services;

import com.example.activitymicroservice.domain.Activity;
import com.example.activitymicroservice.domain.Competition;
import com.example.activitymicroservice.domain.Training;
import com.example.activitymicroservice.repositories.ActivityRepository;
import com.example.activitymicroservice.utils.Pair;
import com.example.activitymicroservice.utils.TimeSlot;
import org.apache.tomcat.jni.Local;
import org.assertj.core.api.AssertionsForClassTypes;
import org.checkerframework.checker.units.qual.A;
import org.checkerframework.checker.units.qual.C;
import org.junit.Rule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SuppressWarnings("checkstyle:Indentation")
@ExtendWith(MockitoExtension.class)
class ActivityServiceTest {

    @Mock
    private ActivityRepository activityRepository;

    @Mock
    private ActivityService activityService;

    Activity training;

    @BeforeEach
    void setUp() {
        activityService = new ActivityService(activityRepository);
        training = new Training();
    }

    @Test
    void unenrollPositionActivityEmpty() {
        Pair<Long, String> posTaken = new Pair(1L, "cox");
        when(activityRepository.findById(posTaken.getFirst())).thenReturn(Optional.empty());
        assertThrows(Exception.class, () -> activityService.unenrollPosition(posTaken));
        verify(activityRepository, never()).save(any());
    }

    @Test
    void unenrollPositionActivitySuccess() throws Exception {
        Pair<Long, String> posTaken = new Pair(1L, "cox");
        Activity act = new Training();
        act.setPositions(List.of("coach", "cox", "port side rower"));
        when(activityRepository.findById(posTaken.getFirst())).thenReturn(Optional.of(act));
        List<String> returned = activityService.unenrollPosition(posTaken);
        verify(activityRepository, times(1)).save(any());
        List<String> expected = List.of("cox", "coach", "cox", "port side rower");
        assertThat(returned).containsAll(expected);

        posTaken = new Pair(1L, "sculling rower");
        act.setPositions(List.of("coach", "port", "cox", "port side rower", "cox"));
        when(activityRepository.findById(posTaken.getFirst())).thenReturn(Optional.of(act));
        returned = activityService.unenrollPosition(posTaken);
        verify(activityRepository, times(2)).save(any());
        expected = List.of("cox", "coach", "port", "cox", "port side rower", "sculling rower");
        assertThat(returned).containsAll(expected);
    }

    @Test
    void findAllTest() {
        List<Activity> expected = new ArrayList<>(List.of(new Training(), new Competition()));
        when(activityService.findAll()).thenReturn(expected);
        AssertionsForClassTypes.assertThat(activityService.findAll()).isEqualTo(expected);
        verify(activityRepository, times(1)).findAll();
    }

    @Test
    public void saveTest() {
        Activity activity1 = new Training();
        activityService.save(activity1);
        verify(activityRepository, times(1)).save(activity1);
    }

    @Test
    public void returnNullSaveTest() {
        when(activityService.save(training)).thenReturn(training);
        assertThat(activityService.save(training) != null).isTrue();
    }

    @Test
    public void getActivitiesByTimeSlotTest() {
        Activity activity1 = new Training();
        Activity activity2 = new Competition();
        activity1.setTimeSlot(new TimeSlot(
                LocalDateTime.of(2023, 12, 1, 23, 15),
                LocalDateTime.of(2024, 11, 2, 15, 00)
        ));
        activity2.setTimeSlot(new TimeSlot(
                LocalDateTime.of(2003, 12, 1, 23, 15),
                LocalDateTime.of(2004, 11, 2, 15, 00)
        ));
        List<TimeSlot> timeSlotSet = List.of(
                new TimeSlot(
                        LocalDateTime.of(2023, 11, 1, 23, 15),
                        LocalDateTime.of(2024, 11, 2, 15, 00)
                ));
        assertThat(activityService.getActivitiesByTimeSlot(List.of(activity1, activity2), timeSlotSet).size())
                .isEqualTo(1);
        assertThat(activityService.getActivitiesByTimeSlot(List.of(activity1, activity2), timeSlotSet).get(0))
                .isEqualTo(activity1);
    }


    @Test
    public void getActivitiesByTimeSlotTestForMutation() {
        Activity activity1 = new Training();
        Activity activity2 = new Competition();
        activity1.setTimeSlot(new TimeSlot(
                LocalDateTime.of(2023, 12, 1, 23, 15),
                LocalDateTime.of(2024, 11, 2, 15, 00)
        ));
        activity2.setTimeSlot(new TimeSlot(
                LocalDateTime.of(2003, 12, 1, 23, 15),
                LocalDateTime.of(2004, 11, 2, 15, 00)
        ));
        List<TimeSlot> timeSlotSet = List.of(
                new TimeSlot(
                        LocalDateTime.of(2023, 11, 1, 23, 15),
                        LocalDateTime.of(2024, 11, 2, 15, 00)
                ));
        activityService.getActivitiesByTimeSlot(List.of(activity1, activity2), timeSlotSet);
        Duration duration = Duration.between(LocalDateTime.now(), activityService.getCheckTime());
        assertThat(duration.getSeconds() > 5400).isTrue();
    }

    @Test
    public void findActivityOptionalTest() {
        Activity activity = new Training();
        activity.setActivityId(1L);
        when(activityRepository.findById(1L)).thenReturn(Optional.of(activity));
        assertThat(activityService.findActivityOptional(1L).get()).isEqualTo(activity);
        verify(activityRepository, times(1)).findById(1L);
    }

    @Test
    public void findActivityOptionalEmptyTest() {
        when(activityRepository.findById(1L)).thenReturn(Optional.empty());
        assertThat(activityService.findActivityOptional(1L)).isEmpty();
        verify(activityRepository, times(1)).findById(1L);
    }

    @Test
    public void deleteByIdTest() {
        Activity activity = new Training();
        activity.setActivityId(1L);
        doNothing().when(activityRepository).deleteById(1L);
        activityRepository.deleteById(1L);
        verify(activityRepository, times(1)).deleteById(1L);
    }

    @Test
    public void testDeleteActivityById() {
        training.setActivityId(1L);
        doNothing().when(activityRepository).deleteById(1L);
        activityRepository.deleteById(1L);
        when(activityRepository.existsById(training.getActivityId())).thenReturn(false);
        assertFalse(activityRepository.existsById(training.getActivityId()));
    }

    @Test
    public void getTimeSlotsByActivityIdsTest() throws Exception {
        Activity activity1 = new Training();
        Activity activity2 = new Competition();
        TimeSlot timeSlot1 = new TimeSlot(
                LocalDateTime.of(2003, 12, 1, 23, 15),
                LocalDateTime.of(2004, 11, 2, 15, 00)
        );
        activity1.setTimeSlot(timeSlot1);
        activity1.setActivityId(1L);
        TimeSlot timeSlot2 = new TimeSlot(
                LocalDateTime.of(2003, 12, 1, 23, 15),
                LocalDateTime.of(2006, 11, 2, 15, 00)
        );
        activity2.setTimeSlot(timeSlot2);
        activity2.setActivityId(2L);
        when(activityService.findActivityOptional(1L)).thenReturn(Optional.of(activity1));
        when(activityService.findActivityOptional(2L)).thenReturn(Optional.of(activity2));

        assertThat(activityService.getTimeSlotsByActivityIds(List.of(1L, 2L)).size()).isEqualTo(2);
        assertThat(activityService.getTimeSlotsByActivityIds(List.of(1L, 2L)).get(0)).isEqualTo(timeSlot1);
        assertThat(activityService.getTimeSlotsByActivityIds(List.of(1L, 2L)).get(1)).isEqualTo(timeSlot2);
    }

    @Test
    public void takeSpotTest() throws Exception {
        Activity activity = new Competition();
        activity.setActivityId(1L);
        activity.setPositions(List.of("cox", "rower"));
        when(activityService.findActivityOptional(1L)).thenReturn(Optional.of(activity));
        when(activityRepository.save(activity)).thenReturn(activity);
        assertThat(activityService.takeSpot(new Pair<>(1L, "cox"))).isTrue();
        assertThat(activity.getPositions().size()).isEqualTo(1);
        assertThat(activity.getPositions().get(0)).isEqualTo("rower");
    }

    @Test
    public void editActivityServiceTestFalse() {
        Activity activity = new Training();
        Activity activity1 = new Competition();
        assertThat(activityService.editActivityService(activity, activity1)).isFalse();
    }

    @Test
    public void editActivityServiceTestCertificate() {
        Activity activity = new Training();
        Activity activity1 = new Training();
        activity1.setCertificate("B4");
        when(activityRepository.save(activity)).thenReturn(activity);
        assertThat(activityService.editActivityService(activity, activity1)).isTrue();
        assertThat(activity.getCertificate()).isEqualTo("B4");
    }

    @Test
    public void editActivityServiceTestFalse1() {
        Activity activity = new Competition();
        Activity activity1 = new Training();
        assertThat(activityService.editActivityService(activity, activity1)).isFalse();
    }

    @Test
    public void editActivityServiceTestGender() {
        Activity activity = new Competition();
        Activity activity1 = new Competition();
        ((Competition) activity1).setGender('M');
        when(activityRepository.save(activity)).thenReturn(activity);
        assertThat(activityService.editActivityService(activity, activity1)).isTrue();
        assertThat(((Competition) activity).getGender()).isEqualTo('M');
    }

    @Test
    public void editActivityServiceTestOrganisation() {
        Activity activity = new Competition();
        Activity activity1 = new Competition();
        ((Competition) activity1).setOrganisation("SEM33A");
        when(activityRepository.save(activity)).thenReturn(activity);
        assertThat(activityService.editActivityService(activity, activity1)).isTrue();
        assertThat(((Competition) activity).getOrganisation()).isEqualTo("SEM33A");
    }

    @Test
    public void editActivityServiceTestCompetitive() {
        Activity activity = new Competition();
        Activity activity1 = new Competition();
        ((Competition) activity1).setCompetitive(true);
        when(activityRepository.save(activity)).thenReturn(activity);
        assertThat(activityService.editActivityService(activity, activity1)).isTrue();
        assertThat(((Competition) activity).isCompetitive()).isTrue();
    }

    @Test
    public void editActivityServiceTestTimeSlot() {
        Activity activity = new Training();
        Activity activity1 = new Training();
        TimeSlot timeSlot = new TimeSlot();
        activity1.setTimeSlot(timeSlot);
        when(activityRepository.save(activity)).thenReturn(activity);
        assertThat(activityService.editActivityService(activity, activity1)).isTrue();
        assertThat(activity.getTimeSlot()).isEqualTo(timeSlot);
    }

    @Test
    public void editActivityServiceTestPositions() {
        Activity activity = new Training();
        Activity activity1 = new Training();
        activity1.setPositions(List.of("cox", "coach"));
        when(activityRepository.save(activity)).thenReturn(activity);
        assertThat(activityService.editActivityService(activity, activity1)).isTrue();
        assertThat(activity.getPositions()).isEqualTo(List.of("cox", "coach"));
    }
}