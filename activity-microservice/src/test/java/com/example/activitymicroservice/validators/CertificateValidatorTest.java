package com.example.activitymicroservice.validators;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import com.example.activitymicroservice.domain.Competition;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CertificateValidatorTest {
    @Mock
    UserPublisher userPublisher;

    CertificateValidator certificateValidator;

    Competition competition;

    String position;

    String userId;

    ActivityContext context;

    @BeforeEach
    void setUp() {
        userId = "TataVlad";
        position = "cox";
        competition = new Competition();
        competition.setCertificate("4+");
        certificateValidator = new CertificateValidator();
        context = new ActivityContext(competition, userPublisher, position, userId);
    }

    @Test
    void updateCertificateListTest() throws Exception {
        List<String> list = List.of("a", "b", "c");
        CertificateValidator.updateCertificateList(list);
        when(userPublisher.getCertificate(userId)).thenReturn("c");
        competition.setCertificate("b");
        assertThat(certificateValidator.handle(context)).isTrue();
        CertificateValidator.updateCertificateList(List.of("C4", "4+", "8+"));
    }

    @Test
    void addCertificateTest() {
        CertificateValidator.addCertificate("a");
        when(userPublisher.getCertificate(userId)).thenReturn("8+");
        competition.setCertificate("a");
        assertThatThrownBy(() -> certificateValidator.handle(context))
                .isInstanceOf(InvalidObjectException.class);
        CertificateValidator.updateCertificateList(List.of("C4", "4+", "8+"));
    }

    @Test
    void handleCorrect() throws Exception {
        when(userPublisher.getCertificate(userId)).thenReturn("8+");
        assertThat(certificateValidator.handle(context)).isTrue();
    }

    @Test
    void handleIncorrect() {
        when(userPublisher.getCertificate(userId)).thenReturn("C4");
        assertThatThrownBy(() -> certificateValidator.handle(context))
                .isInstanceOf(InvalidObjectException.class);
    }

    @Test
    void handleInValid1() {
        when(userPublisher.getCertificate(userId)).thenReturn("ADA");
        assertThatThrownBy(() -> certificateValidator.handle(context))
                .isInstanceOf(InvalidObjectException.class);
    }

    @Test
    void handleInValid2() {
        when(userPublisher.getCertificate(userId)).thenReturn("C4");
        competition.setCertificate("abra");
        assertThatThrownBy(() -> certificateValidator.handle(context))
                .isInstanceOf(InvalidObjectException.class);
    }
}