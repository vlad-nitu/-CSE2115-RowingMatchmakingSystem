package com.example.activitymicroservice.validators;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import com.example.activitymicroservice.domain.Competition;
import com.example.activitymicroservice.publishers.UserPublisher;
import com.example.activitymicroservice.utils.ActivityContext;
import com.example.activitymicroservice.utils.ActivityUtils;
import lombok.Getter;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GenderValidatorTest {
    @Mock
    UserPublisher userPublisher;

    GenderValidator genderValidator;

    Competition competition;

    String position;

    String userId;

    ActivityContext context;

    @BeforeEach
    void setUp() {
        userId = "TataVlad";
        position = "cox";
        competition = new Competition();
        competition.setGender('M');
        competition.setCompetitive(true);
        genderValidator = new GenderValidator();
        context = new ActivityContext(competition, userPublisher, position, userId);
    }

    @Test
    void handleCorrect() throws Exception {
        when(userPublisher.getGender(userId)).thenReturn('M');
        assertThat(genderValidator.handle(context)).isTrue();
    }

    @Test
    void handleIncorrect() {
        when(userPublisher.getGender(userId)).thenReturn('F');
        assertThatThrownBy(() -> genderValidator.handle(context))
                .isInstanceOf(InvalidObjectException.class);
    }

    @Test
    void handleCorrectButReturnFalse() throws InvalidObjectException {
        when(userPublisher.getGender(userId)).thenReturn('M');

        // Create a Validator to be tested next
        CompetitivenessValidator competitivenessValidator = new CompetitivenessValidator();
        when(userPublisher.getCompetitiveness(userId)).thenReturn(false);
        genderValidator.setNext(competitivenessValidator);

        assertThatThrownBy(() -> genderValidator.handle(context))
                .isInstanceOf(InvalidObjectException.class);
    }
}
