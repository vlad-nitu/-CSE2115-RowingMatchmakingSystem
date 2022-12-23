package com.example.activitymicroservice.validators;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import com.example.activitymicroservice.domain.Competition;
import com.example.activitymicroservice.publishers.UserPublisher;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OrganizationValidatorTest {
    @Mock
    UserPublisher userPublisher;

    OrganisationValidator organisationValidator;

    Competition competition;

    String position;

    String userId;

    @BeforeEach
    void setUp() {
        userId = "TataVlad";
        position = "cox";
        competition = new Competition();
        competition.setOrganisation("Best");
        organisationValidator = new OrganisationValidator();
    }

    @Test
    void handleCorrect() throws Exception {
        when(userPublisher.getOrganisation(userId)).thenReturn("Best");
        assertThat(organisationValidator.handle(competition, userPublisher, position, userId)).isTrue();
    }

    @Test
    void handleIncorrect() {
        when(userPublisher.getOrganisation(userId)).thenReturn("Worst");
        assertThatThrownBy(() -> organisationValidator.handle(competition, userPublisher, position, userId))
                .isInstanceOf(InvalidObjectException.class);
    }
}
