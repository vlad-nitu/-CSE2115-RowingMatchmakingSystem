package com.example.activitymicroservice.validators;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import com.example.activitymicroservice.domain.Competition;
import com.example.activitymicroservice.domain.Training;
import com.example.activitymicroservice.publishers.UserPublisher;
import com.example.activitymicroservice.utils.ActivityContext;
import com.example.activitymicroservice.utils.ActivityUtils;
import org.checkerframework.checker.units.qual.C;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.ws.rs.core.Response;

import java.io.InvalidObjectException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PositionValidatorTest {
    @Mock
    UserPublisher userPublisher;

    PositionValidator positionValidator;

    Training training;

    String position;

    String userId;

    ActivityContext context;

    @BeforeEach
    void setUp() {
        userId = "TataVlad";
        position = "cox";
        training = new Training();
        training.setPositions(List.of("cox"));
        positionValidator = new PositionValidator();
        context = new ActivityContext(training, userPublisher, position, userId);
    }

    @Test
    void handleCorrect() throws Exception {
        when(userPublisher.getPositions(userId)).thenReturn(Set.of("cox", "coach"));
        assertThat(positionValidator.handle(context)).isTrue();
    }

    @Test
    void handleIncorrect() {
        when(userPublisher.getPositions(userId)).thenReturn(Set.of("coach"));
        assertThatThrownBy(() -> positionValidator.handle(context))
                .isInstanceOf(InvalidObjectException.class);
    }
}
