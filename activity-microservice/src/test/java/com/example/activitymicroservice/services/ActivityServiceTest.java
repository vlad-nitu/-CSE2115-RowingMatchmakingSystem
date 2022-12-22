package com.example.activitymicroservice.services;

import com.example.activitymicroservice.domain.Activity;
import com.example.activitymicroservice.domain.Training;
import com.example.activitymicroservice.repositories.ActivityRepository;
import com.example.activitymicroservice.utils.Pair;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.parameters.P;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
}