package com.example.activitymicroservice.services;

import com.example.activitymicroservice.domain.Activity;
import com.example.activitymicroservice.domain.Competition;
import com.example.activitymicroservice.domain.Training;
import com.example.activitymicroservice.repositories.ActivityRepository;
import com.example.activitymicroservice.utils.Pair;
import com.example.activitymicroservice.utils.TimeSlot;
import org.assertj.core.api.AssertionsForClassTypes;
import org.checkerframework.checker.units.qual.A;
import org.junit.Rule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.parameters.P;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SuppressWarnings("checkstyle:Indentation")
@ExtendWith(MockitoExtension.class)
class ActivityServiceTest {

    @Mock
    private ActivityRepository activityRepository;
    private ActivityService activityService;

    @BeforeEach
    void setUp() {
        activityService = new ActivityService(activityRepository);
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

}